package com.lineinc.erp.api.server.domain.materialmanagement.service;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialManagementDetailService {


    @Transactional
    public void createMaterialDetailManagement(
            MaterialManagement materialManagement,
            List<MaterialManagementDetailCreateRequest> requests) {

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
    public void updateMaterialManagementDetails(MaterialManagement materialManagement, List<MaterialManagementDetailUpdateRequest> requests) {
        EntitySyncUtils.syncList(
                materialManagement.getDetails(),
                requests,
                (MaterialManagementDetailUpdateRequest dto) -> MaterialManagementDetail.builder()
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
                        .build()
        );
    }
}


