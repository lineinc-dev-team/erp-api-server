package com.lineinc.erp.api.server.domain.materialmanagement.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementChangeHistoryType;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialManagementDetailService {

    private final Javers javers;
    private final MaterialManagementChangeHistoryRepository changeHistoryRepository;

    @Transactional
    public void createMaterialDetailManagement(
            final MaterialManagement materialManagement,
            final List<MaterialManagementDetailCreateRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            return;
        }

        requests.stream()
                .filter(detail -> detail.name() != null && !detail.name().isBlank())
                .map(detail -> MaterialManagementDetail.builder()
                        .materialManagement(materialManagement)
                        .name(detail.name())
                        .standard(detail.standard())
                        .usage(detail.usage())
                        .quantity(detail.quantity())
                        .unitPrice(detail.unitPrice())
                        .supplyPrice(detail.supplyPrice())
                        .vat(detail.vat())
                        .total(detail.total())
                        .memo(detail.memo())
                        .build())
                .forEach(materialManagement.getDetails()::add);
    }

    @Transactional
    public void updateMaterialManagementDetails(final MaterialManagement materialManagement,
            final List<MaterialManagementDetailUpdateRequest> requests) {
        // 변경 전 상태 저장 (Javers 스냅샷)
        final List<MaterialManagementDetail> beforeDetails = materialManagement.getDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, MaterialManagementDetail.class))
                .toList();

        EntitySyncUtils.syncList(
                materialManagement.getDetails(),
                requests,
                (final MaterialManagementDetailUpdateRequest dto) -> MaterialManagementDetail.builder()
                        .materialManagement(materialManagement)
                        .name(dto.name())
                        .standard(dto.standard())
                        .usage(dto.usage())
                        .quantity(dto.quantity())
                        .unitPrice(dto.unitPrice())
                        .supplyPrice(dto.supplyPrice())
                        .vat(dto.vat())
                        .total(dto.total())
                        .memo(dto.memo())
                        .build());

        // 변경사항 추적 및 수정이력 생성
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 자재
        final Set<Long> beforeIds = beforeDetails.stream()
                .map(MaterialManagementDetail::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (final MaterialManagementDetail after : materialManagement.getDetails()) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 자재
        final Map<Long, MaterialManagementDetail> afterMap = materialManagement.getDetails().stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(MaterialManagementDetail::getId, d -> d));

        for (final MaterialManagementDetail before : beforeDetails) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            final MaterialManagementDetail after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 변경사항이 있을 때만 수정이력 생성
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final MaterialManagementChangeHistory history = MaterialManagementChangeHistory.builder()
                    .materialManagement(materialManagement)
                    .type(MaterialManagementChangeHistoryType.MATERIAL)
                    .changes(json)
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
