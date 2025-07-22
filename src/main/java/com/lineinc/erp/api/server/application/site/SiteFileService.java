package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.site.repository.SiteFileRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteFileCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteFileService {

    private final SiteFileRepository siteFileRepository;

    public void createFiles(SiteContract contract, List<SiteFileCreateRequest> fileRequests) {
        for (SiteFileCreateRequest fileReq : fileRequests) {
            siteFileRepository.save(SiteFile.builder()
                    .siteContract(contract)
                    .name(fileReq.name())
                    .fileUrl(fileReq.fileUrl())
                    .originalFileName(fileReq.originalFileName())
                    .memo(fileReq.memo())
                    .type(fileReq.type())
                    .build()
            );
        }
    }
}