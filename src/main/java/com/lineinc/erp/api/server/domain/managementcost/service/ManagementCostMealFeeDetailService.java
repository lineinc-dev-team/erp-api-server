package com.lineinc.erp.api.server.domain.managementcost.service;

import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.service.LaborService;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostMealFeeDetailRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailUpdateRequest;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagementCostMealFeeDetailService {

    private final ManagementCostMealFeeDetailRepository managementCostMealFeeDetailRepository;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final LaborService laborService;
    private final Javers javers;

    @Transactional
    public void createManagementCostMealFeeDetails(ManagementCost managementCost,
            List<ManagementCostMealFeeDetailCreateRequest> details) {
        if (details != null) {
            for (ManagementCostMealFeeDetailCreateRequest detailReq : details) {
                ManagementCostMealFeeDetail detail = ManagementCostMealFeeDetail.builder()
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
    public void updateManagementCostMealFeeDetails(ManagementCost managementCost,
            List<ManagementCostMealFeeDetailUpdateRequest> requests) {

        managementCost.getMealFeeDetails().forEach(detail -> {
            detail.syncTransientFields();
        });

        // 수정 전 스냅샷 생성
        List<ManagementCostMealFeeDetail> beforeDetails = managementCost.getMealFeeDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, ManagementCostMealFeeDetail.class))
                .toList();

        // 식대 상세 정보 업데이트
        EntitySyncUtils.syncList(
                managementCost.getMealFeeDetails(),
                requests,
                (ManagementCostMealFeeDetailUpdateRequest dto) -> {
                    Labor labor = (dto.laborId() != null)
                            ? laborService.getLaborByIdOrThrow(dto.laborId())
                            : null;
                    ManagementCostMealFeeDetail detail = ManagementCostMealFeeDetail.builder()
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
        for (ManagementCostMealFeeDetail detail : managementCost.getMealFeeDetails()) {
            if (detail.getLaborId() != null) {
                Labor labor = laborService.getLaborByIdOrThrow(detail.getLaborId());
                detail.setEntities(labor);
            } else {
                // laborId가 null인 경우 labor도 null로 설정
                detail.setEntities(null);
            }
        }

        // 변경 이력 추적 및 저장
        List<ManagementCostMealFeeDetail> afterDetails = new ArrayList<>(managementCost.getMealFeeDetails());
        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 식대 상세 항목
        Set<Long> beforeIds = beforeDetails.stream()
                .map(ManagementCostMealFeeDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ManagementCostMealFeeDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                Map<String, String> addedChange = JaversUtils.extractAddedEntityChange(javers, after);
                if (addedChange != null) {
                    allChanges.add(addedChange);
                }
            }
        }

        // 수정된 식대 상세 항목
        Map<Long, ManagementCostMealFeeDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ManagementCostMealFeeDetail::getId, d -> d));

        for (ManagementCostMealFeeDetail before : beforeDetails) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                ManagementCostMealFeeDetail after = afterMap.get(before.getId());
                Diff diff = javers.compare(before, after);
                List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
                allChanges.addAll(simpleChanges);
            }
        }

        // 변경 이력이 있으면 저장
        if (!allChanges.isEmpty()) {
            String changesJson = javers.getJsonConverter().toJson(allChanges);
            ManagementCostChangeHistory changeHistory = ManagementCostChangeHistory.builder()
                    .managementCost(managementCost)
                    .type(ManagementCostChangeType.MEAL_FEE)
                    .changes(changesJson)
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }
    }
}
