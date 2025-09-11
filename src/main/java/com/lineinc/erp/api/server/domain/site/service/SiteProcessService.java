package com.lineinc.erp.api.server.domain.site.service;

import java.util.List;
import java.util.Map;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.enums.SiteChangeType;
import com.lineinc.erp.api.server.domain.site.repository.SiteChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.CreateSiteProcessRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteProcessRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteProcessService {

    private final SiteProcessRepository siteProcessRepository;
    private final UserService userService;
    private final Javers javers;
    private final SiteChangeHistoryRepository siteChangeHistoryRepository;

    public void createProcess(Site site, CreateSiteProcessRequest request) {
        siteProcessRepository.save(SiteProcess.builder()
                .site(site)
                .name(request.name())
                .officePhone(request.officePhone())
                .status(request.status())
                .memo(request.memo())
                .manager(userService.getUserByIdOrThrow(request.managerId()))
                .build());
    }

    public void updateProcess(Site site, UpdateSiteProcessRequest request) {
        SiteProcess siteProcess = site.getProcesses().get(0);
        User user = userService.getUserByIdOrThrow(request.managerId());

        siteProcess.syncTransientFields();
        SiteProcess oldSnapshot = JaversUtils.createSnapshot(javers, siteProcess, SiteProcess.class);
        siteProcess.updateFrom(request, user);
        siteProcessRepository.save(siteProcess);

        Diff diff = javers.compare(oldSnapshot, siteProcess);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            SiteChangeHistory changeHistory = SiteChangeHistory.builder()
                    .site(site)
                    .type(SiteChangeType.PROCESS)
                    .changes(changesJson)
                    .build();
            siteChangeHistoryRepository.save(changeHistory);
        }
    }

    public SiteProcess getSiteProcessByIdOrThrow(Long id) {
        return siteProcessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.SITE_PROCESS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Slice<SiteProcessResponse.SiteProcessSimpleResponse> searchSiteProcessByName(Long siteId, String keyword,
            Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;
        return siteProcessRepository.findBySiteIdAndKeyword(siteId, searchKeyword, pageable)
                .map(SiteProcessResponse.SiteProcessSimpleResponse::from);
    }
}