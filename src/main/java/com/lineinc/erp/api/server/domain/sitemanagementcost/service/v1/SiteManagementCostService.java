package com.lineinc.erp.api.server.domain.sitemanagementcost.service.v1;

import java.math.BigDecimal;
import java.text.NumberFormat;
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

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.sitemanagementcost.enums.SiteManagementCostChangeHistoryType;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 현장관리비 Service
 */
@Slf4j
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
    private final LaborPayrollRepository laborPayrollRepository;
    private final Javers javers;

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
                .employeeSalarySupplyPrice(request.employeeSalarySupplyPrice())
                .employeeSalaryVat(request.employeeSalaryVat())
                .employeeSalaryDeduction(request.employeeSalaryDeduction())
                .employeeSalaryMemo(request.employeeSalaryMemo())
                .regularRetirementPension(request.regularRetirementPension())
                .regularRetirementPensionSupplyPrice(request.regularRetirementPensionSupplyPrice())
                .regularRetirementPensionVat(request.regularRetirementPensionVat())
                .regularRetirementPensionDeduction(request.regularRetirementPensionDeduction())
                .regularRetirementPensionMemo(request.regularRetirementPensionMemo())
                .retirementDeduction(request.retirementDeduction())
                .retirementDeductionSupplyPrice(request.retirementDeductionSupplyPrice())
                .retirementDeductionVat(request.retirementDeductionVat())
                .retirementDeductionDeduction(request.retirementDeductionDeduction())
                .retirementDeductionMemo(request.retirementDeductionMemo())
                .majorInsuranceRegular(request.majorInsuranceRegular())
                .majorInsuranceRegularSupplyPrice(request.majorInsuranceRegularSupplyPrice())
                .majorInsuranceRegularVat(request.majorInsuranceRegularVat())
                .majorInsuranceRegularDeduction(request.majorInsuranceRegularDeduction())
                .majorInsuranceRegularMemo(request.majorInsuranceRegularMemo())
                .majorInsuranceDaily(request.majorInsuranceDaily())
                .majorInsuranceDailySupplyPrice(request.majorInsuranceDailySupplyPrice())
                .majorInsuranceDailyVat(request.majorInsuranceDailyVat())
                .majorInsuranceDailyDeduction(request.majorInsuranceDailyDeduction())
                .majorInsuranceDailyMemo(request.majorInsuranceDailyMemo())
                .contractGuaranteeFee(request.contractGuaranteeFee())
                .contractGuaranteeFeeSupplyPrice(request.contractGuaranteeFeeSupplyPrice())
                .contractGuaranteeFeeVat(request.contractGuaranteeFeeVat())
                .contractGuaranteeFeeDeduction(request.contractGuaranteeFeeDeduction())
                .contractGuaranteeFeeMemo(request.contractGuaranteeFeeMemo())
                .equipmentGuaranteeFee(request.equipmentGuaranteeFee())
                .equipmentGuaranteeFeeSupplyPrice(request.equipmentGuaranteeFeeSupplyPrice())
                .equipmentGuaranteeFeeVat(request.equipmentGuaranteeFeeVat())
                .equipmentGuaranteeFeeDeduction(request.equipmentGuaranteeFeeDeduction())
                .equipmentGuaranteeFeeMemo(request.equipmentGuaranteeFeeMemo())
                .nationalTaxPayment(request.nationalTaxPayment())
                .nationalTaxPaymentSupplyPrice(request.nationalTaxPaymentSupplyPrice())
                .nationalTaxPaymentVat(request.nationalTaxPaymentVat())
                .nationalTaxPaymentDeduction(request.nationalTaxPaymentDeduction())
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

        // 노무명세서 기준으로 4대보험(일용) 동기화
        syncMajorInsuranceDailyFromLaborPayroll(site, siteProcess, request.yearMonth(), userDetails.getUserId());
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
     * 현장관리비 수정
     */
    @Transactional
    public void updateSiteManagementCost(
            final Long id,
            final SiteManagementCostUpdateRequest request,
            final CustomUserDetails userDetails) {

        // 현장관리비 조회
        final SiteManagementCost siteManagementCost = siteManagementCostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.SITE_MANAGEMENT_COST_NOT_FOUND));

        // 수정 전 스냅샷 생성
        final SiteManagementCost oldSnapshot = JaversUtils.createSnapshot(javers, siteManagementCost,
                SiteManagementCost.class);
        siteManagementCost.updateFrom(request);

        siteManagementCostRepository.save(siteManagementCost);

        // 변경 이력 저장
        final Diff diff = javers.compare(oldSnapshot, siteManagementCost);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);

        if (!simpleChanges.isEmpty()) {
            final User user = userService.getUserByIdOrThrow(userDetails.getUserId());

            // 현장관리비 관련 필드
            final List<String> siteManagementFields = List.of(
                    "employeeSalary", "employeeSalaryMemo",
                    "regularRetirementPension", "regularRetirementPensionMemo",
                    "retirementDeduction", "retirementDeductionMemo",
                    "majorInsuranceRegular", "majorInsuranceRegularMemo",
                    "majorInsuranceDaily", "majorInsuranceDailyMemo",
                    "contractGuaranteeFee", "contractGuaranteeFeeMemo",
                    "equipmentGuaranteeFee", "equipmentGuaranteeFeeMemo",
                    "nationalTaxPayment", "nationalTaxPaymentMemo");

            // 본사관리비 관련 필드
            final List<String> headquartersManagementFields = List.of(
                    "headquartersManagementCost", "headquartersManagementCostMemo");

            // 현장관리비 변경 내역 필터링
            final List<Map<String, String>> siteManagementChanges = simpleChanges.stream()
                    .filter(change -> {
                        final String property = change.get("property");
                        return property != null && siteManagementFields.contains(property);
                    })
                    .toList();

            // 본사관리비 변경 내역 필터링
            final List<Map<String, String>> headquartersManagementChanges = simpleChanges.stream()
                    .filter(change -> {
                        final String property = change.get("property");
                        return property != null && headquartersManagementFields.contains(property);
                    })
                    .toList();

            // 현장관리비 변경 이력 생성
            if (!siteManagementChanges.isEmpty()) {
                final String siteManagementChangesJson = javers.getJsonConverter().toJson(siteManagementChanges);
                final SiteManagementCostChangeHistory siteChangeHistory = SiteManagementCostChangeHistory.builder()
                        .siteManagementCost(siteManagementCost)
                        .type(SiteManagementCostChangeHistoryType.SITE_MANAGEMENT_COST)
                        .changes(siteManagementChangesJson)
                        .user(user)
                        .build();
                siteManagementCostChangeHistoryRepository.save(siteChangeHistory);
            }

            // 본사관리비 변경 이력 생성
            if (!headquartersManagementChanges.isEmpty()) {
                final String headquartersManagementChangesJson = javers.getJsonConverter()
                        .toJson(headquartersManagementChanges);
                final SiteManagementCostChangeHistory headquartersChangeHistory = SiteManagementCostChangeHistory
                        .builder()
                        .siteManagementCost(siteManagementCost)
                        .type(SiteManagementCostChangeHistoryType.HEADQUARTERS_MANAGEMENT_COST)
                        .changes(headquartersManagementChangesJson)
                        .user(user)
                        .build();
                siteManagementCostChangeHistoryRepository.save(headquartersChangeHistory);
            }
        }

        // 변경이력 메모 수정 처리
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final SiteManagementCostUpdateRequest.ChangeHistoryRequest historyRequest : request
                    .changeHistories()) {
                siteManagementCostChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getSiteManagementCost().getId().equals(siteManagementCost.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }
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

    /**
     * 현장관리비 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param siteManagementCostId 현장관리비 ID
     * @param loginUser            로그인 사용자
     * @param pageable             페이징 정보
     * @return 현장관리비 변경 이력 페이지
     */
    public Page<SiteManagementCostChangeHistoryResponse> getSiteManagementCostChangeHistoriesWithPaging(
            final Long siteManagementCostId,
            final CustomUserDetails loginUser,
            final Pageable pageable) {
        final Page<SiteManagementCostChangeHistory> historyPage = siteManagementCostChangeHistoryRepository
                .findBySiteManagementCostIdWithPaging(siteManagementCostId, pageable);
        return historyPage.map(history -> SiteManagementCostChangeHistoryResponse.from(history, loginUser.getUserId()));
    }

    /**
     * 노무명세서 기준으로 4대보험(일용) 동기화
     * 직영, 용역, 기타(정직원 제외) 총 공제액 합계를 현장관리비 4대보험(일용)에 반영
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param yearMonth   년월
     */
    @Transactional
    public void syncMajorInsuranceDailyFromLaborPayroll(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth,
            final Long userId) {

        log.info("현장/공정({}/{})의 {}월 4대보험(일용) 동기화 시작",
                site.getName(), siteProcess.getName(), yearMonth);

        // 1. 해당 현장/공정/년월의 노무명세서 조회
        final List<LaborPayroll> payrolls = laborPayrollRepository
                .findBySiteAndSiteProcessAndYearMonth(site, siteProcess, yearMonth);

        // 2. 직영, 용역 총 공제액 합계 계산
        final BigDecimal totalDeductions = payrolls.stream()
                .filter(payroll -> payroll.getLabor() != null)
                .filter(payroll -> {
                    final LaborType type = payroll.getLabor().getType();
                    return type == LaborType.DIRECT_CONTRACT || type == LaborType.OUTSOURCING;
                })
                .map(LaborPayroll::getTotalDeductions)
                .filter(deductions -> deductions != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 현장관리비 조회
        final SiteManagementCost siteManagementCost = siteManagementCostRepository
                .findByYearMonthAndSiteAndSiteProcess(yearMonth, site, siteProcess)
                .orElse(null);

        // 현장관리비가 없으면 동기화 건너뜀
        if (siteManagementCost == null) {
            return;
        }

        // 4. 4대보험(일용) 업데이트
        final Long oldValue = siteManagementCost.getMajorInsuranceDaily();
        final Long newValue = totalDeductions.longValue();

        // 값이 변경되지 않았으면 업데이트 건너뜀
        if (oldValue != null && oldValue.equals(newValue)) {
            log.debug("4대보험(일용) 값이 동일하여 업데이트를 건너뜁니다: {} (현장={}, 공정={}, 년월={})",
                    oldValue, site.getName(), siteProcess.getName(), yearMonth);
            return;
        }

        // 변경 전 스냅샷 생성
        final SiteManagementCost oldSnapshot = JaversUtils.createSnapshot(javers, siteManagementCost,
                SiteManagementCost.class);

        // 값 업데이트
        siteManagementCost.setMajorInsuranceDaily(newValue);
        siteManagementCostRepository.save(siteManagementCost);

        // 변경 이력 저장
        final Diff diff = javers.compare(oldSnapshot, siteManagementCost);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);

        if (!simpleChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(simpleChanges);
            final SiteManagementCostChangeHistory changeHistory = SiteManagementCostChangeHistory.builder()
                    .siteManagementCost(siteManagementCost)
                    .type(SiteManagementCostChangeHistoryType.SITE_MANAGEMENT_COST)
                    .changes(changesJson)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            siteManagementCostChangeHistoryRepository.save(changeHistory);
        }

        log.info("4대보험(일용) 업데이트 완료: {} -> {} (현장={}, 공정={}, 년월={})",
                oldValue, newValue, site.getName(), siteProcess.getName(), yearMonth);
    }
}
