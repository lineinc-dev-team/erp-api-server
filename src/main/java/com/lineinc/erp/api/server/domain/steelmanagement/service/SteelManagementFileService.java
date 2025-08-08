package com.lineinc.erp.api.server.domain.steelmanagement.service;

import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementFile;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementFileRepository;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementFileCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementFileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SteelManagementFileService {

    private final SteelManagementFileRepository steelManagementFileRepository;

    @Transactional
    public void createSteelManagementFiles(SteelManagement steelManagement, List<SteelManagementFileCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<SteelManagementFile> files = requests.stream()
                .map(request -> SteelManagementFile.builder()
                        .steelManagement(steelManagement)
                        .name(request.name())
                        .fileUrl(request.fileUrl())
                        .originalFileName(request.originalFileName())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        steelManagementFileRepository.saveAll(files);
    }

    @Transactional
    public void updateSteelManagementFiles(SteelManagement steelManagement, List<SteelManagementFileUpdateRequest> requests) {
        EntitySyncUtils.syncList(
                steelManagement.getFiles(),
                requests,
                (SteelManagementFileUpdateRequest dto) -> SteelManagementFile.builder()
                        .steelManagement(steelManagement)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build()
        );
    }
}
