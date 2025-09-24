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

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeHistoryType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostKeyMoneyDetailRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostKeyMoneyDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostKeyMoneyDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagementCostKeyMoneyDetailService {

    private final ManagementCostKeyMoneyDetailRepository managementCostKeyMoneyDetailRepository;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final Javers javers;

    @Transactional
    public void createManagementCostKeyMoneyDetails(final ManagementCost managementCost,
            final List<ManagementCostKeyMoneyDetailCreateRequest> details) {
        if (details != null) {
            for (final ManagementCostKeyMoneyDetailCreateRequest detailReq : details) {
                final ManagementCostKeyMoneyDetail detail = ManagementCostKeyMoneyDetail.builder()
                        .managementCost(managementCost)
                        .account(detailReq.account())
                        .purpose(detailReq.purpose())
                        .personnelCount(detailReq.personnelCount())
                        .amount(detailReq.amount())
                        .isDeductible(detailReq.isDeductible())
                        .memo(detailReq.memo())
                        .build();
                managementCostKeyMoneyDetailRepository.save(detail);
            }
        }
    }

    @Transactional
    public void updateManagementCostKeyMoneyDetails(final ManagementCost managementCost,
            final List<ManagementCostKeyMoneyDetailUpdateRequest> requests) {

        // 수정 전 스냅샷 생성
        final List<ManagementCostKeyMoneyDetail> beforeDetails = managementCost.getKeyMoneyDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, ManagementCostKeyMoneyDetail.class))
                .toList();

        // 전도금 상세 정보 업데이트
        EntitySyncUtils.syncList(
                managementCost.getKeyMoneyDetails(),
                requests,
                (final ManagementCostKeyMoneyDetailUpdateRequest dto) -> ManagementCostKeyMoneyDetail.builder()
                        .managementCost(managementCost)
                        .account(dto.account())
                        .purpose(dto.purpose())
                        .personnelCount(dto.personnelCount())
                        .amount(dto.amount())
                        .memo(dto.memo())
                        .isDeductible(dto.isDeductible())
                        .build());

        // 변경 이력 추적 및 저장
        final List<ManagementCostKeyMoneyDetail> afterDetails = new ArrayList<>(managementCost.getKeyMoneyDetails());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 전도금 상세 항목
        final Set<Long> beforeIds = beforeDetails.stream()
                .map(ManagementCostKeyMoneyDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final ManagementCostKeyMoneyDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 전도금 상세 항목
        final Map<Long, ManagementCostKeyMoneyDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ManagementCostKeyMoneyDetail::getId, d -> d));

        for (final ManagementCostKeyMoneyDetail before : beforeDetails) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                final ManagementCostKeyMoneyDetail after = afterMap.get(before.getId());
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
                    .type(ManagementCostChangeHistoryType.KEY_MONEY_DETAIL)
                    .changes(changesJson)
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }
    }
}
