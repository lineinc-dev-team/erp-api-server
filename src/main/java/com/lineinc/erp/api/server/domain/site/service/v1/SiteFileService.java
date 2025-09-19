package com.lineinc.erp.api.server.domain.site.service.v1;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.site.repository.SiteFileRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.CreateSiteFileRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteFileService {

    private final SiteFileRepository siteFileRepository;

    public void createFiles(final SiteContract contract, final List<CreateSiteFileRequest> fileRequests) {
        for (final CreateSiteFileRequest fileReq : fileRequests) {
            siteFileRepository.save(SiteFile.builder()
                    .siteContract(contract)
                    .fileUrl(fileReq.fileUrl())
                    .originalFileName(fileReq.originalFileName())
                    .memo(fileReq.memo())
                    .type(fileReq.type())
                    .build());
        }
    }
}