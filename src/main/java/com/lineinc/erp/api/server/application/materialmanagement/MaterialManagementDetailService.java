package com.lineinc.erp.api.server.application.materialmanagement;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementDetailCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
}
