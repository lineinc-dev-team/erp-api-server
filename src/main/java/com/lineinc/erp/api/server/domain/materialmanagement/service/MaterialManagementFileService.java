package com.lineinc.erp.api.server.domain.materialmanagement.service;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementFileCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementFileUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialManagementFileService {


    @Transactional
    public void createMaterialFileManagement(
            MaterialManagement materialManagement,
            List<MaterialManagementFileCreateRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            return;
        }

        requests.stream()
                .filter(file -> file.name() != null && !file.name().isBlank())
                .map(file -> MaterialManagementFile.builder()
                        .materialManagement(materialManagement)
                        .name(file.name())
                        .fileUrl(file.fileUrl())
                        .originalFileName(file.originalFileName())
                        .memo(file.memo())
                        .build())
                .forEach(materialManagement.getFiles()::add);
    }

    @Transactional
    public void updateMaterialManagementFiles(
            MaterialManagement materialManagement,
            List<MaterialManagementFileUpdateRequest> requests) {
        EntitySyncUtils.syncList(
                materialManagement.getFiles(),
                requests,
                (MaterialManagementFileUpdateRequest dto) -> MaterialManagementFile.builder()
                        .materialManagement(materialManagement)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build()
        );
    }
}

