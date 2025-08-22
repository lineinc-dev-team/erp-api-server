package com.lineinc.erp.api.server.domain.managementcost.service;

import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostDetailRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDetailUpdateRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementCostDetailService {

    private final ManagementCostDetailRepository managementCostDetailRepository;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final Javers javers;

    @Transactional
    public void createManagementCostDetails(ManagementCost managementCost,
            List<ManagementCostDetailCreateRequest> details) {
        if (details != null) {
            for (ManagementCostDetailCreateRequest detailReq : details) {
                ManagementCostDetail detail = ManagementCostDetail.builder()
                        .managementCost(managementCost)
                        .name(detailReq.name())
                        .unitPrice(detailReq.unitPrice())
                        .supplyPrice(detailReq.supplyPrice())
                        .vat(detailReq.vat())
                        .total(detailReq.total())
                        .memo(detailReq.memo())
                        .build();
                managementCostDetailRepository.save(detail);
            }
        }
    }

    @Transactional
    public void updateManagementCostDetails(ManagementCost managementCost,
            List<ManagementCostDetailUpdateRequest> requests) {

        // 수정 전 스냅샷 생성
        List<ManagementCostDetail> beforeDetails = managementCost.getDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, ManagementCostDetail.class))
                .toList();

        // 상세 정보 업데이트
        EntitySyncUtils.syncList(
                managementCost.getDetails(),
                requests,
                (ManagementCostDetailUpdateRequest dto) -> ManagementCostDetail.builder()
                        .managementCost(managementCost)
                        .name(dto.name())
                        .unitPrice(dto.unitPrice())
                        .supplyPrice(dto.supplyPrice())
                        .vat(dto.vat())
                        .total(dto.total())
                        .memo(dto.memo())
                        .build());

        // 변경 이력 추적 및 저장
        List<ManagementCostDetail> afterDetails = new ArrayList<>(managementCost.getDetails());
        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 상세 항목
        Set<Long> beforeIds = beforeDetails.stream()
                .map(ManagementCostDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ManagementCostDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 상세 항목
        Map<Long, ManagementCostDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(ManagementCostDetail::getId, d -> d));

        for (ManagementCostDetail before : beforeDetails) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                ManagementCostDetail after = afterMap.get(before.getId());
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
                    .type(ManagementCostChangeType.ITEM_DETAIL)
                    .changes(changesJson)
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }
    }
}