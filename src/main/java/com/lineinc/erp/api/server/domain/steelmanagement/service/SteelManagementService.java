package com.lineinc.erp.api.server.domain.steelmanagement.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.ApproveSteelManagementRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.DeleteSteelManagementRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.ReleaseSteelManagementRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteelManagementService {

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final SteelManagementRepository steelManagementRepository;
    private final SteelManagementDetailService steelManagementDetailService;
    private final SteelManagementFileService steelManagementFileService;
    private final OutsourcingCompanyService outsourcingCompanyService;

    @Transactional
    public void createSteelManagement(SteelManagementCreateRequest request) {
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        OutsourcingCompany outsourcingCompany = null;
        if (request.outsourcingCompanyId() != null) {
            outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
        }

        validateCreatableSteelType(request.type());
        SteelManagement steelManagement = SteelManagement.builder()
                .site(site)
                .siteProcess(siteProcess)
                .outsourcingCompany(outsourcingCompany)
                .usage(request.usage())
                .memo(request.memo())
                .startDate(DateTimeFormatUtils.toOffsetDateTime(request.startDate()))
                .endDate(DateTimeFormatUtils.toOffsetDateTime(request.endDate()))
                .orderDate(OffsetDateTime.now())
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

    private void validateCreatableSteelType(SteelManagementType type) {
        if (!(type == SteelManagementType.ORDER
                || type == SteelManagementType.PURCHASE
                || type == SteelManagementType.LEASE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.INVALID_INITIAL_STEEL_TYPE);
        }
    }

    @Transactional
    public void approveSteelManagements(ApproveSteelManagementRequest request) {
        List<SteelManagement> steelManagements = steelManagementRepository.findAllById(request.steelManagementIds());
        if (steelManagements.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND);
        }

        for (SteelManagement steelManagement : steelManagements) {
            if (steelManagement.getType() == SteelManagementType.RELEASE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        ValidationMessages.CANNOT_APPROVE_RELEASED_STEEL);
            }
            steelManagement.changeType(SteelManagementType.APPROVAL);
        }

        steelManagementRepository.saveAll(steelManagements);
    }

    @Transactional
    public void releaseSteelManagements(ReleaseSteelManagementRequest request) {
        List<SteelManagement> steelManagements = steelManagementRepository.findAllById(request.steelManagementIds());
        if (steelManagements.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND);
        }

        for (SteelManagement steelManagement : steelManagements) {
            if (steelManagement.getType() != SteelManagementType.APPROVAL) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        ValidationMessages.CANNOT_RELEASE_NON_APPROVED_STEEL);
            }
            steelManagement.changeType(SteelManagementType.RELEASE);
        }

        steelManagementRepository.saveAll(steelManagements);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(SteelManagementListRequest request, Sort sort, List<String> fields) {
        List<SteelManagementResponse> steelManagementResponses = steelManagementRepository
                .findAllWithoutPaging(request, sort)
                .stream()
                .map(SteelManagementResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                steelManagementResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "standard" -> "규격";
            case "name" -> "품명";
            case "unit" -> "단위";
            case "count" -> "본";
            case "length" -> "길이";
            case "totalLength" -> "총 길이";
            case "unitWeight" -> "단위중량";
            case "quantity" -> "수량";
            case "unitPrice" -> "단가";
            case "supplyPrice" -> "공급가";
            case "usage" -> "용도";
            case "hasFile" -> "첨부";
            case "type" -> "구분";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(SteelManagementResponse steelManagement, String field) {
        // boolean hasNoDetails = steelManagement.details().isEmpty();
        // var firstDetail = hasNoDetails ? null : steelManagement.details().get(0);

        // return switch (field) {
        // case "id" -> String.valueOf(steelManagement.id());
        // case "siteName" -> steelManagement.site().name();
        // case "processName" -> steelManagement.process().name();
        // case "standard" -> hasNoDetails ? "" : firstDetail.standard();
        // case "name" -> hasNoDetails ? "" : firstDetail.name();
        // case "unit" -> hasNoDetails ? "" : firstDetail.unit();
        // case "count" -> hasNoDetails ? "" : firstDetail.count().toString();
        // case "length" -> hasNoDetails ? "" : firstDetail.length().toString();
        // case "totalLength" -> hasNoDetails ? "" :
        // firstDetail.totalLength().toString();
        // case "unitWeight" -> hasNoDetails ? "" : firstDetail.unitWeight().toString();
        // case "quantity" -> hasNoDetails ? "" : firstDetail.quantity().toString();
        // case "unitPrice" -> hasNoDetails ? "" : firstDetail.unitPrice().toString();
        // case "supplyPrice" -> hasNoDetails ? "" :
        // firstDetail.supplyPrice().toString();
        // case "usage" -> steelManagement.usage();
        // case "hasFile" -> steelManagement.hasFile() ? "Y" : "N";
        // case "type" -> steelManagement.type();
        // case "memo" -> steelManagement.memo();
        // default -> null;
        // };

        return null;
    }

    @Transactional(readOnly = true)
    public SteelManagementDetailViewResponse getSteelManagementById(Long siteId) {
        SteelManagement steelManagement = steelManagementRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND));
        return SteelManagementDetailViewResponse.from(steelManagement);
    }

    @Transactional
    public void updateSteelManagement(Long id, SteelManagementUpdateRequest request) {
        SteelManagement steelManagement = steelManagementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND));

        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        steelManagement.changeSite(site);
        steelManagement.changeSiteProcess(siteProcess);
        steelManagement.updateFrom(request);

        steelManagementDetailService.updateSteelManagementDetails(steelManagement, request.details());
        steelManagementFileService.updateSteelManagementFiles(steelManagement, request.files());
    }
}
