package com.lineinc.erp.api.server.application.materialmanagement;

import com.lineinc.erp.api.server.application.site.SiteProcessService;
import com.lineinc.erp.api.server.application.site.SiteService;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response.MaterialManagementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MaterialManagementService {

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final MaterialManagementRepository materialManagementRepository;

    private final MaterialManagementDetailService materialManagementDetailService;
    private final MaterialManagementFileService materialManagementFileService;

    @Transactional
    public void createMaterialManagement(MaterialManagementCreateRequest request) {
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        MaterialManagement materialManagement = MaterialManagement.builder()
                .site(site)
                .siteProcess(siteProcess)
                .inputType(request.inputType())
                .inputTypeDescription(request.inputTypeDescription())
                .deliveryDate(request.deliveryDate().atStartOfDay().atOffset(OffsetDateTime.now().getOffset()))
                .memo(request.memo())
                .build();

        materialManagementDetailService.createMaterialDetailManagement(materialManagement, request.details());
        materialManagementFileService.createMaterialFileManagement(materialManagement, request.files());
        materialManagementRepository.save(materialManagement);
    }

    @Transactional(readOnly = true)
    public Page<MaterialManagementResponse> getAllMaterialManagements(MaterialManagementListRequest request, Pageable pageable) {
        return materialManagementRepository.findAll(request, pageable);
    }
}
