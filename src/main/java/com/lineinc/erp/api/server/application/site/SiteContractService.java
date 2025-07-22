package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.site.repository.SiteContractRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteContractCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteFileCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteContractService {

    private final SiteContractRepository siteContractRepository;
    private final SiteFileService siteFileService;

    public void createContracts(Site site, List<SiteContractCreateRequest> requests) {
        for (SiteContractCreateRequest contractReq : requests) {
            SiteContract contract = siteContractRepository.save(SiteContract.builder()
                    .site(site)
                    .name(contractReq.name())
                    .amount(contractReq.amount())
                    .memo(contractReq.memo())
                    .build()
            );

            if (contractReq.files() != null && !contractReq.files().isEmpty()) {
                siteFileService.createFiles(contract, contractReq.files());
            }
        }
    }
}
