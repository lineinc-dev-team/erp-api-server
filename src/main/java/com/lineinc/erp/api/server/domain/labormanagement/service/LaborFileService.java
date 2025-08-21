package com.lineinc.erp.api.server.domain.labormanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborFile;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaborFileService {

    /**
     * 인력정보 첨부파일 수정
     */
    @Transactional
    public void updateLaborFiles(Labor labor, List<LaborFileUpdateRequest> requests) {
        EntitySyncUtils.syncList(
                labor.getFiles(),
                requests,
                (LaborFileUpdateRequest dto) -> LaborFile.builder()
                        .labor(labor)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());
    }

}
