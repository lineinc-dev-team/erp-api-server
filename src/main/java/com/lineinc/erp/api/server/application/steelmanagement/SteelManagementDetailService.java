package com.lineinc.erp.api.server.application.steelmanagement;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementDetailRepository;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementDetailCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementDetailUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SteelManagementDetailService {
    private final SteelManagementDetailRepository steelManagementDetailRepository;

    @Transactional
    public void createSteelManagementDetail(SteelManagement steelManagement, List<SteelManagementDetailCreateRequest> requests) {
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
    public void updateSteelManagementDetails(SteelManagement steelManagement, List<SteelManagementDetailUpdateRequest> requests) {
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
                        .build()
        );
    }
}
