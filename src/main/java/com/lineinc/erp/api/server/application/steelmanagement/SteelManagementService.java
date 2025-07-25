package com.lineinc.erp.api.server.application.steelmanagement;

import com.lineinc.erp.api.server.application.site.SiteProcessService;
import com.lineinc.erp.api.server.application.site.SiteService;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementRepository;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.DeleteSteelManagementRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.response.SteelManagementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SteelManagementService {

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final SteelManagementRepository steelManagementRepository;
    private final SteelManagementDetailService steelManagementDetailService;
    private final SteelManagementFileService steelManagementFileService;

    @Transactional
    public void createSteelManagement(SteelManagementCreateRequest request) {
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }
        SteelManagement steelManagement = SteelManagement.builder()
                .site(site)
                .siteProcess(siteProcess)
                .usage(request.usage())
                .memo(request.memo())
                .paymentDate(DateTimeFormatUtils.toOffsetDateTime(request.paymentDate()))
                .type(request.type())
                .build();

        steelManagement = steelManagementRepository.save(steelManagement);
        steelManagementDetailService.createSteelManagementDetail(steelManagement, request.details());
        steelManagementFileService.createSteelManagementFiles(steelManagement, request.files());
    }

    @Transactional(readOnly = true)
    public Page<SteelManagementResponse> getSteelManagementList(SteelManagementListRequest request, Pageable pageable) {
        return steelManagementRepository.findAll(request, pageable);
    }

    @Transactional
    public void deleteSteelManagements(DeleteSteelManagementRequest request) {
        List<SteelManagement> steelManagements = steelManagementRepository.findAllById(request.steelManagementIds());
        if (steelManagements.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND);
        }

        for (SteelManagement steelManagement : steelManagements) {
            steelManagement.markAsDeleted();
        }

        steelManagementRepository.saveAll(steelManagements);
    }
}
