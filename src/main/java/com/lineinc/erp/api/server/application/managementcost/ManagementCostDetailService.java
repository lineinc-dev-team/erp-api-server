package com.lineinc.erp.api.server.application.managementcost;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostDetailRepository;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostDetailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementCostDetailService {

    private final ManagementCostDetailRepository managementCostDetailRepository;

    @Transactional
    public void createManagementCostDetails(ManagementCost managementCost, List<ManagementCostDetailCreateRequest> details) {
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
}