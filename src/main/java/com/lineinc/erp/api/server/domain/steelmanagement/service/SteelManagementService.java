package com.lineinc.erp.api.server.domain.steelmanagement.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
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
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeType;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
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
import com.lineinc.erp.api.server.shared.util.JaversUtils;

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
    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;
    private final Javers javers;

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
            case "type" -> "구분";
            case "orderDate" -> "발주일";
            case "approvalDate" -> "승인일";
            case "releaseDate" -> "반출일";
            case "startDateAndEndDate" -> "기간";
            case "outsourcingCompanyName" -> "업체명";
            case "businessNumber" -> "사업자등록번호";
            case "totalAmount" -> "총금액";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(SteelManagementResponse steelManagement, String field) {
        return switch (field) {
            case "id" -> String.valueOf(steelManagement.id());
            case "siteName" -> steelManagement.site().name();
            case "processName" -> steelManagement.process().name();
            case "type" -> steelManagement.type();
            case "orderDate" -> steelManagement.orderDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(steelManagement.orderDate())
                    : "";
            case "approvalDate" -> steelManagement.approvalDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(steelManagement.approvalDate())
                    : "";
            case "releaseDate" -> steelManagement.releaseDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(steelManagement.releaseDate())
                    : "";
            case "startDateAndEndDate" -> {
                String startDate = steelManagement.startDate() != null
                        ? DateTimeFormatUtils.formatKoreaLocalDate(steelManagement.startDate())
                        : "";
                String endDate = steelManagement.endDate() != null
                        ? DateTimeFormatUtils.formatKoreaLocalDate(steelManagement.endDate())
                        : "";
                yield startDate + " ~ " + endDate;
            }
            case "outsourcingCompanyName" ->
                steelManagement.outsourcingCompany() != null ? steelManagement.outsourcingCompany().name() : "";
            case "businessNumber" ->
                steelManagement.outsourcingCompany() != null ? steelManagement.outsourcingCompany().businessNumber()
                        : "";
            case "totalAmount" ->
                steelManagement.totalAmount() != null ? String.valueOf(steelManagement.totalAmount()) : "";
            case "memo" -> steelManagement.memo();
            default -> null;
        };
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

        Site site = null;
        if (request.siteId() != null) {
            site = siteService.getSiteByIdOrThrow(request.siteId());
        }

        SiteProcess siteProcess = null;
        if (request.siteProcessId() != null) {
            siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        }
        if (siteProcess != null && site != null && !siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        OutsourcingCompany outsourcingCompany = null;
        if (request.outsourcingCompanyId() != null) {
            outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
        }

        steelManagement.syncTransientFields();
        SteelManagement oldSnapshot = JaversUtils.createSnapshot(javers, steelManagement, SteelManagement.class);

        steelManagement.updateFrom(request, site, siteProcess, outsourcingCompany);
        steelManagementRepository.save(steelManagement);

        Diff diff = javers.compare(oldSnapshot, steelManagement);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);

        // outsourcingCompanyName 변경사항 분리
        List<Map<String, String>> outsourcingChanges = new ArrayList<>();
        List<Map<String, String>> otherChanges = new ArrayList<>();

        for (Map<String, String> change : simpleChanges) {
            if ("outsourcingCompanyName".equals(change.get("property"))) {
                outsourcingChanges.add(change);
            } else {
                otherChanges.add(change);
            }
        }

        // 기본 변경사항 저장 (outsourcingCompanyName 제외)
        if (!otherChanges.isEmpty()) {
            String otherChangesJson = javers.getJsonConverter().toJson(otherChanges);
            SteelManagementChangeHistory changeHistory = SteelManagementChangeHistory.builder()
                    .steelManagement(steelManagement)
                    .type(SteelManagementChangeType.BASIC)
                    .changes(otherChangesJson)
                    .build();
            steelManagementChangeHistoryRepository.save(changeHistory);
        }

        // 외주업체 변경사항 별도 저장
        if (!outsourcingChanges.isEmpty()) {
            String outsourcingChangesJson = javers.getJsonConverter().toJson(outsourcingChanges);
            SteelManagementChangeHistory outsourcingChangeHistory = SteelManagementChangeHistory.builder()
                    .steelManagement(steelManagement)
                    .type(SteelManagementChangeType.OUTSOURCING_COMPANY)
                    .changes(outsourcingChangesJson)
                    .build();
            steelManagementChangeHistoryRepository.save(outsourcingChangeHistory);
        }

        if (request.changeHistories() != null &&
                !request.changeHistories().isEmpty()) {
            for (SteelManagementUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                steelManagementChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getSteelManagement().getId().equals(steelManagement.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        steelManagementDetailService.updateSteelManagementDetails(steelManagement, request.details());
        steelManagementFileService.updateSteelManagementFiles(steelManagement, request.files());
    }
}
