package com.lineinc.erp.api.server.application.managementcost;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostFileRepository;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostFileCreateRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementCostFileService {

    private final ManagementCostFileRepository managementCostFileRepository;

    public void createManagementCostFiles(List<ManagementCostFileCreateRequest> files, ManagementCost managementCost) {
        if (files != null) {
            for (ManagementCostFileCreateRequest fileReq : files) {
                ManagementCostFile file = ManagementCostFile.builder()
                        .managementCost(managementCost)
                        .name(fileReq.name())
                        .fileUrl(fileReq.fileUrl())
                        .originalFileName(fileReq.originalFileName())
                        .memo(fileReq.memo())
                        .build();
                managementCostFileRepository.save(file);
            }
        }
    }
}