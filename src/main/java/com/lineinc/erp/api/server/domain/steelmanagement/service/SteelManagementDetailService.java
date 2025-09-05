package com.lineinc.erp.api.server.domain.steelmanagement.service;

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

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeHistoryType;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementDetailRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteelManagementDetailService {
    private final SteelManagementDetailRepository steelManagementDetailRepository;
    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;
    private final Javers javers;

    @Transactional
    public void createSteelManagementDetail(SteelManagement steelManagement,
            List<SteelManagementDetailCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<SteelManagementDetail> details = requests.stream()
                .map(request -> SteelManagementDetail.builder()
                        .steelManagement(steelManagement)
                        .standard(request.standard())
                        .name(request.name())
                        .unit(request.unit())
                        .count(request.count())
                        .length(request.length())
                        .totalLength(request.totalLength())
                        .unitWeight(request.unitWeight())
                        .quantity(request.quantity())
                        .unitPrice(request.unitPrice())
                        .supplyPrice(request.supplyPrice())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        steelManagementDetailRepository.saveAll(details);
    }

    @Transactional
    public void updateSteelManagementDetails(SteelManagement steelManagement,
            List<SteelManagementDetailUpdateRequest> requests) {
        // 1. 현재 상세 목록을 복사해 변경 전 상태(snapshot) 보관
        List<SteelManagementDetail> beforeDetails = steelManagement.getDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, SteelManagementDetail.class))
                .toList();

        EntitySyncUtils.syncList(
                steelManagement.getDetails(),
                requests,
                (SteelManagementDetailUpdateRequest dto) -> SteelManagementDetail.builder()
                        .steelManagement(steelManagement)
                        .standard(dto.standard())
                        .name(dto.name())
                        .unit(dto.unit())
                        .count(dto.count())
                        .length(dto.length())
                        .totalLength(dto.totalLength())
                        .unitWeight(dto.unitWeight())
                        .quantity(dto.quantity())
                        .unitPrice(dto.unitPrice())
                        .supplyPrice(dto.supplyPrice())
                        .memo(dto.memo())
                        .build());

        // 2. 변경 후 상태와 비교하여 변경 이력 생성
        List<SteelManagementDetail> afterDetails = new ArrayList<>(steelManagement.getDetails());
        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 상세 항목
        Set<Long> beforeIds = beforeDetails.stream()
                .map(SteelManagementDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (SteelManagementDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 상세 항목
        Map<Long, SteelManagementDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(SteelManagementDetail::getId, d -> d));

        for (SteelManagementDetail before : beforeDetails) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            SteelManagementDetail after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 3. 변경된 이력이 있다면 SteelManagementChangeHistory 엔티티로 저장
        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            SteelManagementChangeHistory changeHistory = SteelManagementChangeHistory.builder()
                    .steelManagement(steelManagement)
                    .type(SteelManagementChangeHistoryType.DETAIL)
                    .changes(json)
                    .build();
            steelManagementChangeHistoryRepository.save(changeHistory);
        }
    }
}
