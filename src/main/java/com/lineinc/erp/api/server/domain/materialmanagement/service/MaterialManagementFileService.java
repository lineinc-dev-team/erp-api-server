package com.lineinc.erp.api.server.domain.materialmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());
    }
}
