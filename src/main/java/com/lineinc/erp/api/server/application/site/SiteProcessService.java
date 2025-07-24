package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteProcessCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteProcessUpdateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteProcessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public SiteProcess getSiteProcessByIdOrThrow(Long id) {
        return siteProcessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_PROCESS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Slice<SiteProcessResponse.SiteProcessSimpleResponse> searchSiteProcessByName(String keyword, Pageable pageable) {
        Slice<SiteProcess> siteProcessesSlice = siteProcessRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return siteProcessesSlice.map(SiteProcessResponse.SiteProcessSimpleResponse::from);
    }
}