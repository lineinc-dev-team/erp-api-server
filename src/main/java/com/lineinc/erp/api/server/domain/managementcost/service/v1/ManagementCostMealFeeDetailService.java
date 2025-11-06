package com.lineinc.erp.api.server.domain.managementcost.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.service.v1.LaborService;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeHistoryType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostMealFeeDetailRepository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagementCostMealFeeDetailService {

    private final ManagementCostMealFeeDetailRepository managementCostMealFeeDetailRepository;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final LaborService laborService;
    private final Javers javers;
    private final UserService userService;

    @Transactional
    public void createManagementCostMealFeeDetails(final ManagementCost managementCost,
            final List<ManagementCostMealFeeDetailCreateRequest> details) {
        if (details != null) {
            for (final ManagementCostMealFeeDetailCreateRequest detailReq : details) {
                final ManagementCostMealFeeDetail detail = ManagementCostMealFeeDetail.builder()
                        .managementCost(managementCost)
                        .workType(detailReq.workType())
                        .labor(detailReq.laborId() != null ? laborService.getLaborByIdOrThrow(detailReq.laborId())
                                : null)
                        .name(detailReq.name())
                        .breakfastCount(detailReq.breakfastCount())
                        .lunchCount(detailReq.lunchCount())
                        .unitPrice(detailReq.unitPrice())
                        .amount(detailReq.amount())
                        .memo(detailReq.memo())
                        .build();
                managementCostMealFeeDetailRepository.save(detail);
            }
        }
    }

    @Transactional
    public void updateManagementCostMealFeeDetails(final ManagementCost managementCost,
            final List<ManagementCostMealFeeDetailUpdateRequest> requests, final Long userId) {

        managementCost.getMealFeeDetails().forEach(detail -> {
            detail.syncTransientFields();
        });

        // 수정 전 스냅샷 생성
        final List<ManagementCostMealFeeDetail> beforeDetails = managementCost.getMealFeeDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, ManagementCostMealFeeDetail.class))
                .toList();

        // 식대 상세 정보 업데이트
        EntitySyncUtils.syncList(
                managementCost.getMealFeeDetails(),
                requests,
                (final ManagementCostMealFeeDetailUpdateRequest dto) -> {
                    final Labor labor = (dto.laborId() != null)
                            ? laborService.getLaborByIdOrThrow(dto.laborId())
                            : null;
                    final ManagementCostMealFeeDetail detail = ManagementCostMealFeeDetail.builder()
                            .managementCost(managementCost)
                            .workType(dto.workType())
                            .labor(labor)
                            .name(dto.name())
                            .breakfastCount(dto.breakfastCount())
                            .lunchCount(dto.lunchCount())
                            .unitPrice(dto.unitPrice())
                            .amount(dto.amount())
                            .memo(dto.memo())
                            .build();
                    // 새로 생성된 엔티티의 laborId를 명시적으로 설정
                    detail.updateFrom(dto);
                    detail.syncTransientFields(); // transient 필드 동기화
                    return detail;
                });

        // 수정된 항목들의 Labor 엔티티 재설정
        for (final ManagementCostMealFeeDetail detail : managementCost.getMealFeeDetails()) {
            if (detail.getLaborId() != null) {
                final Labor labor = laborService.getLaborByIdOrThrow(detail.getLaborId());
                detail.setEntities(labor);
            } else {
                // laborId가 null인 경우 labor도 null로 설정
                detail.setEntities(null);
            }
        }

        // 변경 이력 추적 및 저장
        final List<ManagementCostMealFeeDetail> afterDetails = new ArrayList<>(managementCost.getMealFeeDetails());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 식대 상세 항목
        final Set<Long> beforeIds = beforeDetails.stream()
                .map(ManagementCostMealFeeDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final ManagementCostMealFeeDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                final Map<String, String> addedChange = JaversUtils.extractAddedEntityChange(javers, after);
                if (addedChange != null) {
                    allChanges.add(addedChange);
                }
            }
        }

        // 수정된 식대 상세 항목
        final Map<Long, ManagementCostMealFeeDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ManagementCostMealFeeDetail::getId, d -> d));

        for (final ManagementCostMealFeeDetail before : beforeDetails) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                final ManagementCostMealFeeDetail after = afterMap.get(before.getId());
                final Diff diff = javers.compare(before, after);
                final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
                allChanges.addAll(simpleChanges);
            }
        }

        // 변경 이력이 있으면 저장
        if (!allChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(allChanges);
            final ManagementCostChangeHistory changeHistory = ManagementCostChangeHistory.builder()
                    .managementCost(managementCost)
                    .type(ManagementCostChangeHistoryType.MEAL_FEE_REGULAR_EMPLOYEE)
                    .changes(changesJson)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }
    }
}
