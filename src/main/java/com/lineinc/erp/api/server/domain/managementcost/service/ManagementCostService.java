package com.lineinc.erp.api.server.domain.managementcost.service;

import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.DeleteManagementCostsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                .itemType(request.itemType())
                .itemDescription(request.itemDescription())
                .paymentDate(DateTimeFormatUtils.toOffsetDateTime(request.paymentDate()))
                .memo(request.memo())
                .build();

        managementCost = managementCostRepository.save(managementCost);

        // 3. 상세 목록 저장
        managementCostDetailService.createManagementCostDetails(managementCost, request.details());

        // 4. 파일 목록 저장
        managementCostFileService.createManagementCostFiles(request.files(), managementCost);
    }

    @Transactional(readOnly = true)
    public Page<ManagementCostResponse> getAllManagementCosts(ManagementCostListRequest request, Pageable pageable) {
        return managementCostRepository.findAll(request, pageable);
    }

    @Transactional
    public void deleteManagementCosts(DeleteManagementCostsRequest request) {
        List<ManagementCost> managementCosts = managementCostRepository.findAllById(request.managementCostIds());
        if (managementCosts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.MANAGEMENT_COST_NOT_FOUND);
        }

        for (ManagementCost managementCost : managementCosts) {
            managementCost.markAsDeleted();
        }

        managementCostRepository.saveAll(managementCosts);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(ManagementCostListRequest request, Sort sort, List<String> fields) {
        List<ManagementCostResponse> managementCostResponses = managementCostRepository
                .findAllWithoutPaging(request, sort)
                .stream()
                .map(ManagementCostResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                managementCostResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "itemType" -> "품목";
            case "paymentDate" -> "일자";
            case "supplyPrice" -> "공급가";
            case "vat" -> "부가세";
            case "total" -> "합계";
            case "hasFile" -> "첨부파일";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(ManagementCostResponse managementCost, String field) {
        return switch (field) {
            case "id" -> String.valueOf(managementCost.id());
            case "siteName" -> managementCost.site().name();
            case "processName" -> managementCost.process().name();
            case "itemType" -> managementCost.itemType();
            case "paymentDate" -> DateTimeFormatUtils.formatKoreaLocalDate(managementCost.paymentDate());
            case "supplyPrice" -> String.valueOf(
                    managementCost.details().stream()
                            .filter(detail -> detail.supplyPrice() != null)
                            .mapToLong(ManagementCostDetailResponse::supplyPrice)
                            .sum());
            case "vat" -> String.valueOf(
                    managementCost.details().stream()
                            .filter(detail -> detail.vat() != null)
                            .mapToLong(ManagementCostDetailResponse::vat)
                            .sum());
            case "total" -> String.valueOf(
                    managementCost.details().stream()
                            .filter(detail -> detail.total() != null)
                            .mapToLong(ManagementCostDetailResponse::total)
                            .sum());
            case "hasFile" -> managementCost.hasFile() ? "Y" : "N";
            case "memo" -> managementCost.memo();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public ManagementCostDetailViewResponse getManagementCostById(Long siteId) {
        ManagementCost managementCost = managementCostRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MANAGEMENT_COST_NOT_FOUND));
        return ManagementCostDetailViewResponse.from(managementCost);
    }

    @Transactional(readOnly = true)
    public ManagementCost getManagementCostByIdOrThrow(Long id) {
        return managementCostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MANAGEMENT_COST_NOT_FOUND));
    }

    @Transactional
    public void updateManagementCost(Long managementCostId, ManagementCostUpdateRequest request) {
        ManagementCost managementCost = getManagementCostByIdOrThrow(managementCostId);
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }
        managementCost.changeSite(site);
        managementCost.changeSiteProcess(siteProcess);
        managementCost.updateFrom(request);

        managementCostDetailService.updateManagementCostDetails(managementCost, request.details());
        managementCostFileService.updateManagementCostFiles(managementCost, request.files());
    }
}