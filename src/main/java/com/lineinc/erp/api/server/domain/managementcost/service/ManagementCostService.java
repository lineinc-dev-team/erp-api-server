package com.lineinc.erp.api.server.domain.managementcost.service;

import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.DeleteManagementCostsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ItemDescriptionResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostCreateRequest.OutsourcingCompanyInfo;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostKeyMoneyDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.labormanagement.service.LaborService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagementCostService {

    private final ManagementCostRepository managementCostRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final LaborService laborService;

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

        // 2. 외주업체 처리
        OutsourcingCompany outsourcingCompany = null;
        if (request.outsourcingCompanyId() != null) {
            // 기존 외주업체 수정
            outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
            if (request.outsourcingCompanyInfo() != null) {
                OutsourcingCompanyInfo companyInfo = request.outsourcingCompanyInfo();
                outsourcingCompanyService.updateOutsourcingCompany(
                        outsourcingCompany.getId(),
                        new OutsourcingCompanyUpdateRequest(
                                companyInfo.name(),
                                companyInfo.businessNumber(),
                                null, // type
                                null, // typeDescription
                                companyInfo.ceoName(),
                                null, // address
                                null, // detailAddress
                                null, // landlineNumber
                                null, // phoneNumber
                                null, // email
                                null, // isActive
                                null, // defaultDeductions
                                null, // defaultDeductionsDescription
                                companyInfo.bankName(),
                                companyInfo.accountNumber(),
                                companyInfo.accountHolder(),
                                companyInfo.memo(),
                                null, // contacts
                                null, // files
                                null // changeHistories
                        ));
                outsourcingCompany = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(outsourcingCompany.getId());
            }
        } else if (request.outsourcingCompanyInfo() != null) {
            // 신규 외주업체 생성
            OutsourcingCompanyInfo companyInfo = request.outsourcingCompanyInfo();
            outsourcingCompany = OutsourcingCompany.builder()
                    .name(companyInfo.name())
                    .businessNumber(companyInfo.businessNumber())
                    .ceoName(companyInfo.ceoName())
                    .bankName(companyInfo.bankName())
                    .accountNumber(companyInfo.accountNumber())
                    .accountHolder(companyInfo.accountHolder())
                    .memo(companyInfo.memo())
                    .build();
            outsourcingCompany = outsourcingCompanyRepository.save(outsourcingCompany);
        }

        // 3. 관리비 엔티티 생성 및 저장
        ManagementCost managementCost = ManagementCost.builder()
                .site(site)
                .siteProcess(siteProcess)
                .outsourcingCompany(outsourcingCompany)
                .itemType(request.itemType())
                .itemTypeDescription(request.itemTypeDescription())
                .paymentDate(DateTimeFormatUtils.toOffsetDateTime(request.paymentDate()))
                .memo(request.memo())
                .build();

        managementCost = managementCostRepository.save(managementCost);

        // 4. 상세 목록 저장
        if (request.details() != null && !request.details().isEmpty()) {
            managementCostDetailService.createManagementCostDetails(managementCost, request.details());
        }

        // 5. 전도금 상세 목록 저장
        if (request.keyMoneyDetails() != null && !request.keyMoneyDetails().isEmpty()) {
            createKeyMoneyDetails(managementCost, request.keyMoneyDetails());
        }

        // 6. 식대 상세 목록 저장
        if (request.mealFeeDetails() != null && !request.mealFeeDetails().isEmpty()) {
            createMealFeeDetails(managementCost, request.mealFeeDetails());
        }

        // 7. 파일 목록 저장
        if (request.files() != null && !request.files().isEmpty()) {
            managementCostFileService.createManagementCostFiles(request.files(), managementCost);
        }
    }

    /**
     * 전도금 상세 목록 생성
     */
    private void createKeyMoneyDetails(ManagementCost managementCost,
            List<ManagementCostKeyMoneyDetailCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<ManagementCostKeyMoneyDetail> details = requests.stream()
                .map(request -> ManagementCostKeyMoneyDetail.builder()
                        .managementCost(managementCost)
                        .account(request.account())
                        .purpose(request.purpose())
                        .personnelCount(request.personnelCount())
                        .amount(request.amount())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        managementCost.getKeyMoneyDetails().addAll(details);
    }

    /**
     * 식대 상세 목록 생성
     */
    private void createMealFeeDetails(ManagementCost managementCost,
            List<ManagementCostMealFeeDetailCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<ManagementCostMealFeeDetail> details = requests.stream()
                .<ManagementCostMealFeeDetail>map(request -> ManagementCostMealFeeDetail.builder()
                        .managementCost(managementCost)
                        .workType(request.workType())
                        .labor(request.laborId() != null ? laborService.getLaborByIdOrThrow(request.laborId()) : null)
                        .name(request.name())
                        .breakfastCount(request.breakfastCount())
                        .lunchCount(request.lunchCount())
                        .unitPrice(request.unitPrice())
                        .amount(request.amount())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        managementCost.getMealFeeDetails().addAll(details);
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
            case "itemType" -> "항목";
            case "paymentDate" -> "일자";
            case "outsourcingCompanyName" -> "업체명";
            case "outsourcingCompanyBusinessNumber" -> "사업자번호";
            case "outsourcingCompanyCeoName" -> "대표자";
            case "outsourcingCompanyAccountNumber" -> "청구계좌";
            case "outsourcingCompanyAccountHolder" -> "계좌명";
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
            case "outsourcingCompanyName" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().name()
                    : "";
            case "outsourcingCompanyBusinessNumber" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().businessNumber()
                    : "";
            case "outsourcingCompanyCeoName" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().ceoName()
                    : "";
            case "outsourcingCompanyAccountNumber" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().bankName() + " "
                            + managementCost.outsourcingCompany().accountNumber()
                    : "";
            case "outsourcingCompanyAccountHolder" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().accountHolder()
                    : "";
            case "supplyPrice" -> String.valueOf(managementCost.supplyPrice());
            case "vat" -> String.valueOf(managementCost.vat());
            case "total" -> String.valueOf(managementCost.total());
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

    /**
     * ETC 항목의 description 목록 조회
     */
    public Slice<ItemDescriptionResponse> getEtcItemDescriptions(String keyword, Pageable pageable) {
        Slice<Object[]> resultSlice;

        if (keyword == null || keyword.isBlank()) {
            resultSlice = managementCostRepository.findAllDistinctItemDescriptions(ItemType.ETC, pageable);
        } else {
            resultSlice = managementCostRepository.findDistinctItemDescriptionsByKeyword(ItemType.ETC, keyword,
                    pageable);
        }

        return resultSlice.map(result -> new ItemDescriptionResponse((Long) result[1], (String) result[0]));
    }

    /**
     * 인력명 키워드 검색
     */
    public Slice<LaborNameResponse> getLaborNames(String keyword, Pageable pageable) {
        return laborService.getLaborNames(keyword, pageable);
    }
}