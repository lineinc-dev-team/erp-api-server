package com.lineinc.erp.api.server.domain.sitemanagementcost.service.v1;

import java.text.NumberFormat;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;

import lombok.RequiredArgsConstructor;

/**
 * 현장관리비 Service
 */
@Service
@RequiredArgsConstructor
public class SiteManagementCostService {

    private final SiteManagementCostRepository siteManagementCostRepository;
    private final SiteManagementCostChangeHistoryRepository siteManagementCostChangeHistoryRepository;

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final UserService userService;
    private final S3FileService s3FileService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;

    /**
     * 현장관리비 생성
     */
    @Transactional
    public void createSiteManagementCost(
            final SiteManagementCostCreateRequest request,
            final CustomUserDetails userDetails) {

        // 현장 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());

        // 공정 조회
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 중복 체크: 동일한 년월, 현장, 공정에 대한 데이터가 이미 존재하는지 확인
        siteManagementCostRepository.findByYearMonthAndSiteAndSiteProcess(
                request.yearMonth(),
                site,
                siteProcess)
                .ifPresent(_ -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            ValidationMessages.SITE_MANAGEMENT_COST_ALREADY_EXISTS);
                });

        // 현장관리비 생성
        final SiteManagementCost siteManagementCost = SiteManagementCost.builder()
                .yearMonth(request.yearMonth())
                .site(site)
                .siteProcess(siteProcess)
                .employeeSalary(request.employeeSalary())
                .employeeSalaryMemo(request.employeeSalaryMemo())
                .regularRetirementPension(request.regularRetirementPension())
                .regularRetirementPensionMemo(request.regularRetirementPensionMemo())
                .retirementDeduction(request.retirementDeduction())
                .retirementDeductionMemo(request.retirementDeductionMemo())
                .majorInsuranceRegular(request.majorInsuranceRegular())
                .majorInsuranceRegularMemo(request.majorInsuranceRegularMemo())
                .majorInsuranceDaily(request.majorInsuranceDaily())
                .majorInsuranceDailyMemo(request.majorInsuranceDailyMemo())
                .contractGuaranteeFee(request.contractGuaranteeFee())
                .contractGuaranteeFeeMemo(request.contractGuaranteeFeeMemo())
                .equipmentGuaranteeFee(request.equipmentGuaranteeFee())
                .equipmentGuaranteeFeeMemo(request.equipmentGuaranteeFeeMemo())
                .nationalTaxPayment(request.nationalTaxPayment())
                .nationalTaxPaymentMemo(request.nationalTaxPaymentMemo())
                .headquartersManagementCost(request.headquartersManagementCost())
                .headquartersManagementCostMemo(request.headquartersManagementCostMemo())
                .build();

        final SiteManagementCost savedEntity = siteManagementCostRepository.save(siteManagementCost);

        // 변경 이력 저장
        final User user = userService.getUserByIdOrThrow(userDetails.getUserId());
        final SiteManagementCostChangeHistory changeHistory = SiteManagementCostChangeHistory.builder()
                .siteManagementCost(savedEntity)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(user)
                .build();
        siteManagementCostChangeHistoryRepository.save(changeHistory);

    }

    /**
     * 현장관리비 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<SiteManagementCostResponse> getSiteManagementCostList(
            final SiteManagementCostListRequest request,
            final Pageable pageable) {
        return siteManagementCostRepository.findAll(request, pageable);
    }

    /**
     * 현장관리비 상세 조회
     */
    @Transactional(readOnly = true)
    public SiteManagementCostDetailResponse getSiteManagementCostDetail(final Long id) {
        final SiteManagementCost siteManagementCost = siteManagementCostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.SITE_MANAGEMENT_COST_NOT_FOUND));

        return SiteManagementCostDetailResponse.from(siteManagementCost);
    }

    /**
     * 현장관리비 삭제
     */
    @Transactional
    public void deleteSiteManagementCosts(final List<Long> siteManagementCostIds) {
        // 현장관리비들이 존재하는지 확인
        final List<SiteManagementCost> siteManagementCosts = siteManagementCostRepository
                .findAllById(siteManagementCostIds);

        if (siteManagementCosts.size() != siteManagementCostIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ValidationMessages.SITE_MANAGEMENT_COST_NOT_FOUND);
        }

        // 각 현장관리비에 대해 소프트 삭제 처리
        for (final SiteManagementCost siteManagementCost : siteManagementCosts) {
            siteManagementCost.markAsDeleted();
        }
    }

    /**
     * 현장관리비 엑셀 다운로드
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(
            final CustomUserDetails user,
            final SiteManagementCostListRequest request,
            final Sort sort,
            final List<String> fields) {
        final List<SiteManagementCostResponse> responses = siteManagementCostRepository
                .findAllWithoutPaging(request, sort)
                .stream()
                .map(SiteManagementCostResponse::from)
                .toList();

        final Workbook workbook = ExcelExportUtils.generateWorkbook(
                responses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);

        final String fileUrl = s3FileService.uploadExcelToS3(workbook,
                ExcelDownloadHistoryType.SITE_MANAGEMENT_COST.name());

        excelDownloadHistoryService.recordDownload(
                ExcelDownloadHistoryType.SITE_MANAGEMENT_COST,
                userService.getUserByIdOrThrow(user.getUserId()),
                fileUrl);

        return workbook;
    }

    /**
     * 엑셀 헤더명 조회
     */
    private String getExcelHeaderName(final String field) {
        return switch (field) {
            case "id" -> "No.";
            case "yearMonth" -> "연월";
            case "siteName" -> "현장명";
            case "siteProcessName" -> "공정명";
            case "employeeSalary" -> "직원급여";
            case "regularRetirementPension" -> "퇴직연금(정규직)";
            case "retirementDeduction" -> "퇴직공제부금";
            case "majorInsurance" -> "4대보험";
            case "contractGuaranteeFee" -> "보증수수료(계약보증)";
            case "equipmentGuaranteeFee" -> "보증수수료(현장별건설기계)";
            case "nationalTaxPayment" -> "국세납부";
            case "siteManagementTotal" -> "계";
            case "headquartersManagementCost" -> "본사관리비";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값 조회
     */
    private String getExcelCellValue(final SiteManagementCostResponse response, final String field) {
        return switch (field) {
            case "id" -> String.valueOf(response.id());
            case "yearMonth" -> response.yearMonth();
            case "siteName" -> response.site() != null ? response.site().name() : "";
            case "siteProcessName" -> response.siteProcess() != null ? response.siteProcess().name() : "";
            case "employeeSalary" -> {
                if (response.employeeSalary() != null) {
                    yield NumberFormat.getNumberInstance().format(response.employeeSalary());
                }
                yield "";
            }
            case "regularRetirementPension" -> {
                if (response.regularRetirementPension() != null) {
                    yield NumberFormat.getNumberInstance().format(response.regularRetirementPension());
                }
                yield "";
            }
            case "retirementDeduction" -> {
                if (response.retirementDeduction() != null) {
                    yield NumberFormat.getNumberInstance().format(response.retirementDeduction());
                }
                yield "";
            }
            case "majorInsurance" -> {
                if (response.majorInsuranceRegular() != null && response.majorInsuranceDaily() != null) {
                    yield NumberFormat.getNumberInstance()
                            .format(response.majorInsuranceRegular() + response.majorInsuranceDaily());
                }
                yield "";
            }
            case "contractGuaranteeFee" -> {
                if (response.contractGuaranteeFee() != null) {
                    yield NumberFormat.getNumberInstance().format(response.contractGuaranteeFee());
                }
                yield "";
            }
            case "equipmentGuaranteeFee" -> {
                if (response.equipmentGuaranteeFee() != null) {
                    yield NumberFormat.getNumberInstance().format(response.equipmentGuaranteeFee());
                }
                yield "";
            }
            case "nationalTaxPayment" -> {
                if (response.nationalTaxPayment() != null) {
                    yield NumberFormat.getNumberInstance().format(response.nationalTaxPayment());
                }
                yield "";
            }
            case "siteManagementTotal" -> {
                if (response.siteManagementTotal() != null) {
                    yield NumberFormat.getNumberInstance().format(response.siteManagementTotal());
                }
                yield "";
            }
            case "headquartersManagementCost" -> {
                if (response.headquartersManagementCost() != null) {
                    yield NumberFormat.getNumberInstance().format(response.headquartersManagementCost());
                }
                yield "";
            }
            default -> null;
        };
    }
}
