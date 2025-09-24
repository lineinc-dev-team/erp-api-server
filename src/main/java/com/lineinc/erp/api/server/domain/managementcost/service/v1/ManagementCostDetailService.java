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
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeHistoryType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostDetailRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagementCostDetailService {

    private final ManagementCostDetailRepository managementCostDetailRepository;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final Javers javers;

    @Transactional
    public void createManagementCostDetails(final ManagementCost managementCost,
            final List<ManagementCostDetailCreateRequest> details) {
        if (details != null) {
            for (final ManagementCostDetailCreateRequest detailReq : details) {
                final ManagementCostDetail detail = ManagementCostDetail.builder()
                        .managementCost(managementCost)
                        .name(detailReq.name())
                        .quantity(detailReq.quantity())
                        .unitPrice(detailReq.unitPrice())
                        .supplyPrice(detailReq.supplyPrice())
                        .vat(detailReq.vat())
                        .total(detailReq.total())
                        .isDeductible(detailReq.isDeductible())
                        .memo(detailReq.memo())
                        .build();
                managementCostDetailRepository.save(detail);
            }
        }
    }

    @Transactional
    public void updateManagementCostDetails(final ManagementCost managementCost,
            final List<ManagementCostDetailUpdateRequest> requests) {

        // 수정 전 스냅샷 생성
        final List<ManagementCostDetail> beforeDetails = managementCost.getDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, ManagementCostDetail.class))
                .toList();

        // 상세 정보 업데이트
        EntitySyncUtils.syncList(
                managementCost.getDetails(),
                requests,
                (final ManagementCostDetailUpdateRequest dto) -> ManagementCostDetail.builder()
                        .managementCost(managementCost)
                        .name(dto.name())
                        .quantity(dto.quantity())
                        .unitPrice(dto.unitPrice())
                        .supplyPrice(dto.supplyPrice())
                        .vat(dto.vat())
                        .total(dto.total())
                        .isDeductible(dto.isDeductible())
                        .memo(dto.memo())
                        .build());

        // 변경 이력 추적 및 저장
        final List<ManagementCostDetail> afterDetails = new ArrayList<>(managementCost.getDetails());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 상세 항목
        final Set<Long> beforeIds = beforeDetails.stream()
                .map(ManagementCostDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final ManagementCostDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 상세 항목
        final Map<Long, ManagementCostDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ManagementCostDetail::getId, d -> d));

        for (final ManagementCostDetail before : beforeDetails) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                final ManagementCostDetail after = afterMap.get(before.getId());
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
                    .type(ManagementCostChangeHistoryType.ITEM_DETAIL)
                    .changes(changesJson)
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }
    }
}