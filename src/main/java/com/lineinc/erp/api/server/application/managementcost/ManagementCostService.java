package com.lineinc.erp.api.server.application.managementcost;

import com.lineinc.erp.api.server.application.site.SiteProcessService;
import com.lineinc.erp.api.server.application.site.SiteService;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostDetailRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostFileRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostDetailCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostFileCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementCostService {

    private final ManagementCostRepository managementCostRepository;

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final ManagementCostDetailService managementCostDetailService;
    private final ManagementCostFileService managementCostFileService;

    @Transactional
    public void createManagementCost(ManagementCostCreateRequest request) {
        // 1. 현장 및 공정 존재 확인
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        // 2. 관리비 엔티티 생성 및 저장
        ManagementCost managementCost = ManagementCost.builder()
                .site(site)
                .siteProcess(siteProcess)
                .type(request.type())
                .paymentDate(request.paymentDate())
                .businessNumber(request.businessNumber())
                .ceoName(request.ceoName())
                .accountNumber(request.accountNumber())
                .accountHolder(request.accountHolder())
                .memo(request.memo())
                .build();

        managementCost = managementCostRepository.save(managementCost);

        // 3. 상세 목록 저장
        managementCostDetailService.createManagementCostDetails(managementCost, request.details());

        // 4. 파일 목록 저장
        managementCostFileService.createManagementCostFiles(request.files(), managementCost);
    }


}