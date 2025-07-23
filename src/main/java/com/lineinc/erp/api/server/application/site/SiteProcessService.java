package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteProcessCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteProcessUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteProcessService {

    private final SiteProcessRepository siteProcessRepository;

    public void createProcess(Site site, SiteProcessCreateRequest request) {
        siteProcessRepository.save(SiteProcess.builder()
                .site(site)
                .name(request.name())
                .officePhone(request.officePhone())
                .status(request.status())
                .memo(request.memo())
                .build()
        );
    }

    public void updateProcess(Site site, SiteProcessUpdateRequest request) {
        SiteProcess siteProcess = site.getProcesses().get(0);
        siteProcess.updateFrom(request);
        siteProcessRepository.save(siteProcess);
    }
}