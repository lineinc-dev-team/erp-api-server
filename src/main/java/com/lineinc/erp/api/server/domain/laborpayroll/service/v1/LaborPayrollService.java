package com.lineinc.erp.api.server.domain.laborpayroll.service.v1;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.laborpayroll.enums.LaborPayrollChangeType;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollSummaryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollChangeHistoryUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollInfo;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSummaryUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollSummaryResponse;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import lombok.RequiredArgsConstructor;

/**
 * 노무명세서 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaborPayrollService {

    private final LaborPayrollSummaryRepository laborPayrollSummaryRepository;
    private final LaborPayrollRepository laborPayrollRepository;
    private final LaborPayrollChangeHistoryRepository laborPayrollChangeHistoryRepository;
    private final Javers javers;

    /**
     * 노무명세서 월별 집계 목록 조회 (페이징)
     * 사전에 계산된 집계 테이블에서 현장, 공정별 통계 정보 조회
     */
    public PagingResponse<LaborPayrollSummaryResponse> getLaborPayrollMonthlyList(
            final LaborPayrollSearchRequest request, final PageRequest pageRequest, final SortRequest sortRequest) {

        // 페이징 및 정렬 처리
        final Pageable pageable = PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                sortRequest.sort());

        // 집계 테이블에서 조건에 맞는 데이터 조회
        final Page<LaborPayrollSummary> summaryPage = laborPayrollSummaryRepository.findBySearchCondition(
                request.siteName(),
                request.processName(),
                request.yearMonth(),
                pageable);

        // 엔티티를 DTO로 변환
        final List<LaborPayrollSummaryResponse> responseList = summaryPage.getContent().stream()
                .map(LaborPayrollSummaryResponse::from)
                .collect(Collectors.toList());

        return new PagingResponse<>(PagingInfo.from(summaryPage), responseList);
    }

    /**
     * 노무명세서 엑셀 다운로드
     * 검색 조건에 맞는 노무명세서 목록을 엑셀로 내보내기
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(final LaborPayrollSearchRequest request, final Sort sort, final List<String> fields) {
        final List<LaborPayrollSummaryResponse> responses = laborPayrollSummaryRepository
                .findAllWithoutPaging(request.siteName(), request.processName(), request.yearMonth(), sort)
                .stream()
                .map(LaborPayrollSummaryResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                responses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    /**
     * 엑셀 헤더명 매핑
     */
    private String getExcelHeaderName(final String field) {
        return switch (field) {
            case "id" -> "No.";
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "regularEmployeeCount" -> "정직원 수";
            case "directContractCount" -> "직영/계약직 수";
            case "etcCount" -> "기타 수";
            case "totalLaborCost" -> "노무비 합계";
            case "totalDeductions" -> "공제금 합계";
            case "totalNetPayment" -> "차감지급 합계";
            case "yearMonth" -> "년월";
            case "memo" -> "비고";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값 추출
     */
    private String getExcelCellValue(final LaborPayrollSummaryResponse response, final String field) {
        return switch (field) {
            case "id" -> response.id() != null ? response.id().toString() : "";
            case "siteName" -> response.site() != null ? response.site().name() : "";
            case "processName" -> response.siteProcess() != null ? response.siteProcess().name() : "";
            case "regularEmployeeCount" -> {
                if (response.regularEmployeeCount() != null) {
                    yield NumberFormat.getNumberInstance().format(response.regularEmployeeCount());
                }
                yield "";
            }
            case "directContractCount" -> {
                if (response.directContractCount() != null) {
                    yield NumberFormat.getNumberInstance().format(response.directContractCount());
                }
                yield "";
            }
            case "etcCount" -> {
                if (response.etcCount() != null) {
                    yield NumberFormat.getNumberInstance().format(response.etcCount());
                }
                yield "";
            }
            case "totalLaborCost" -> {
                if (response.totalLaborCost() != null) {
                    yield NumberFormat.getNumberInstance().format(response.totalLaborCost());
                }
                yield "";
            }
            case "totalDeductions" -> {
                if (response.totalDeductions() != null) {
                    yield NumberFormat.getNumberInstance().format(response.totalDeductions());
                }
                yield "";
            }
            case "totalNetPayment" -> {
                if (response.totalNetPayment() != null) {
                    yield NumberFormat.getNumberInstance().format(response.totalNetPayment());
                }
                yield "";
            }
            case "yearMonth" -> response.yearMonth() != null ? response.yearMonth() : "";
            case "memo" -> response.memo() != null ? response.memo() : "";
            default -> "";
        };
    }

    /**
     * 노무명세서 상세 조회
     * 현장 ID, 공정 ID, 년월, 노무인력 타입으로 필터링하여 노무명세서 상세 정보를 조회
     */
    @Transactional(readOnly = true)
    public List<LaborPayrollDetailResponse> getLaborPayrollDetails(final Long siteId, final Long siteProcessId,
            final String yearMonth,
            final LaborType type) {
        List<LaborPayroll> laborPayrolls;

        if (type == null) {
            // type이 null이면 전체 조회
            laborPayrolls = laborPayrollRepository.findBySiteIdAndSiteProcessIdAndYearMonth(siteId, siteProcessId,
                    yearMonth);
        } else {
            // type이 지정되면 해당 타입만 조회
            laborPayrolls = laborPayrollRepository.findBySiteIdAndSiteProcessIdAndYearMonthAndLaborType(siteId,
                    siteProcessId, yearMonth, type);
        }

        return laborPayrolls.stream()
                .map(LaborPayrollDetailResponse::from)
                .toList();
    }

    /**
     * 노무명세서 집계 상세 조회
     * 특정 집계 ID의 상세 정보를 조회
     */
    @Transactional(readOnly = true)
    public LaborPayrollSummaryResponse getLaborPayrollSummaryDetail(final Long id) {
        final LaborPayrollSummary summary = laborPayrollSummaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_SUMMARY_NOT_FOUND));

        return LaborPayrollSummaryResponse.from(summary);
    }

    /**
     * 노무명세서 집계 테이블 memo 수정
     * 집계 테이블의 비고 필드만 수정 가능
     */
    @Transactional
    public void updateLaborPayrollSummary(final Long id, final LaborPayrollSummaryUpdateRequest request) {
        final LaborPayrollSummary summary = laborPayrollSummaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_SUMMARY_NOT_FOUND));

        // 변경 전 스냅샷 생성
        final LaborPayrollSummary oldSnapshot = JaversUtils.createSnapshot(javers, summary, LaborPayrollSummary.class);

        // memo 필드 수정
        summary.setMemo(request.memo());
        laborPayrollSummaryRepository.save(summary);

        // 변경 이력 저장
        final Diff diff = javers.compare(oldSnapshot, summary);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final LaborPayrollChangeHistory changeHistory = LaborPayrollChangeHistory.builder()
                    .type(LaborPayrollChangeType.BASIC)
                    .changes(changesJson)
                    .laborPayrollSummary(summary)
                    .build();
            laborPayrollChangeHistoryRepository.save(changeHistory);
        }
    }

    /**
     * 노무명세서 수정
     * 개별 노무명세서의 모든 필드 수정 가능
     */
    @Transactional
    public void updateLaborPayroll(final Long id, final LaborPayrollInfo info) {
        final LaborPayroll laborPayroll = laborPayrollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_NOT_FOUND));

        // 변경 전 스냅샷 생성
        final LaborPayroll oldSnapshot = JaversUtils.createSnapshot(javers, laborPayroll, LaborPayroll.class);

        // 엔티티의 updateFrom 메서드로 필드 업데이트
        laborPayroll.updateFrom(info);

        laborPayrollRepository.save(laborPayroll);

        // 변경 이력 저장
        final Diff diff = javers.compare(oldSnapshot, laborPayroll);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            // 해당하는 LaborPayrollSummary 찾기
            final LaborPayrollSummary summary = laborPayrollSummaryRepository
                    .findBySiteAndSiteProcessAndYearMonth(
                            laborPayroll.getSite(),
                            laborPayroll.getSiteProcess(),
                            laborPayroll.getYearMonth())
                    .orElse(null);

            final LaborPayrollChangeHistory changeHistory = LaborPayrollChangeHistory.builder()
                    .type(LaborPayrollChangeType.LABOR_PAYROLL)
                    .changes(changesJson)
                    .laborPayrollSummary(summary)
                    .build();
            laborPayrollChangeHistoryRepository.save(changeHistory);
        }
    }

    /**
     * 노무명세서들 수정
     * 여러 노무명세서를 한 번에 수정
     */
    @Transactional
    public void updateLaborPayrolls(final LaborPayrollUpdateRequest request) {
        if (request.laborPayrollInfos() == null || request.laborPayrollInfos().isEmpty()) {
            return;
        }

        for (final var laborPayrollInfo : request.laborPayrollInfos()) {
            final LaborPayroll laborPayroll = laborPayrollRepository.findById(laborPayrollInfo.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            ValidationMessages.LABOR_PAYROLL_NOT_FOUND));

            // 변경 전 스냅샷 생성
            final LaborPayroll oldSnapshot = JaversUtils.createSnapshot(javers, laborPayroll, LaborPayroll.class);

            // 엔티티의 updateFrom 메서드로 필드 업데이트
            laborPayroll.updateFrom(laborPayrollInfo);

            laborPayrollRepository.save(laborPayroll);

            // 변경 이력 저장
            final Diff diff = javers.compare(oldSnapshot, laborPayroll);
            final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
            final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

            if (!simpleChanges.isEmpty()) {
                // 해당하는 LaborPayrollSummary 찾기
                final LaborPayrollSummary summary = laborPayrollSummaryRepository
                        .findBySiteAndSiteProcessAndYearMonth(
                                laborPayroll.getSite(),
                                laborPayroll.getSiteProcess(),
                                laborPayroll.getYearMonth())
                        .orElse(null);

                final LaborPayrollChangeHistory changeHistory = LaborPayrollChangeHistory.builder()
                        .type(LaborPayrollChangeType.LABOR_PAYROLL)
                        .changes(changesJson)
                        .laborPayrollSummary(summary)
                        .description(generateLaborDescription(laborPayroll))
                        .build();
                laborPayrollChangeHistoryRepository.save(changeHistory);
            }
        }

        // 노무명세서 수정 후 집계 테이블 업데이트
        updateSummaryAfterPayrollChanges(request.laborPayrollInfos());
    }

    /**
     * 노무명세서 수정 후 집계 테이블 업데이트
     */
    private void updateSummaryAfterPayrollChanges(final List<LaborPayrollInfo> laborPayrollInfos) {
        if (laborPayrollInfos.isEmpty()) {
            return;
        }

        // 첫 번째 노무명세서 정보로 현장/공정/년월 파악
        final LaborPayrollInfo firstInfo = laborPayrollInfos.get(0);
        final LaborPayroll firstPayroll = laborPayrollRepository.findById(firstInfo.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_NOT_FOUND));

        // 해당 현장/공정/년월의 모든 노무명세서 조회
        final List<LaborPayroll> allPayrolls = laborPayrollRepository.findBySiteAndSiteProcessAndYearMonth(
                firstPayroll.getSite(),
                firstPayroll.getSiteProcess(),
                firstPayroll.getYearMonth());

        // 집계 계산
        final BigDecimal totalLaborCost = allPayrolls.stream()
                .map(LaborPayroll::getTotalLaborCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalDeductions = allPayrolls.stream()
                .map(LaborPayroll::getTotalDeductions)
                .filter(deductions -> deductions != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalNetPayment = allPayrolls.stream()
                .map(LaborPayroll::getNetPayment)
                .filter(payment -> payment != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 집계 테이블 업데이트
        final var existingSummary = laborPayrollSummaryRepository.findBySiteAndSiteProcessAndYearMonth(
                firstPayroll.getSite(),
                firstPayroll.getSiteProcess(),
                firstPayroll.getYearMonth());

        if (existingSummary.isPresent()) {
            final LaborPayrollSummary summary = existingSummary.get();
            summary.updateSummary(
                    summary.getRegularEmployeeCount(), // 기존 값 유지
                    summary.getDirectContractCount(), // 기존 값 유지
                    summary.getEtcCount(), // 기존 값 유지
                    totalLaborCost,
                    totalDeductions,
                    totalNetPayment,
                    summary.getMemo()); // 기존 비고 유지
            laborPayrollSummaryRepository.save(summary);
        }
    }

    /**
     * 노무명세서 변경이력 조회
     * 특정 노무명세서 집계와 관련된 변경이력을 조회
     */
    @Transactional(readOnly = true)
    public Slice<LaborPayrollChangeHistoryResponse> getLaborPayrollChangeHistories(
            final Long laborPayrollSummaryId, final Pageable pageable) {
        final Slice<LaborPayrollChangeHistory> changeHistories = laborPayrollChangeHistoryRepository
                .findBySummaryId(laborPayrollSummaryId, pageable);
        return changeHistories.map(LaborPayrollChangeHistoryResponse::from);
    }

    /**
     * 노무명세서 변경이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<LaborPayrollChangeHistoryResponse> getLaborPayrollChangeHistoriesWithPaging(
            final Long laborPayrollSummaryId, final Pageable pageable) {
        final LaborPayrollSummary laborPayrollSummary = getLaborPayrollSummaryByIdOrThrow(laborPayrollSummaryId);
        final Page<LaborPayrollChangeHistory> changeHistoryPage = laborPayrollChangeHistoryRepository
                .findBySummaryIdWithPaging(laborPayrollSummary, pageable);
        return changeHistoryPage.map(LaborPayrollChangeHistoryResponse::from);
    }

    /**
     * 노무명세서 변경이력 수정
     */
    @Transactional
    public void updateLaborPayrollChangeHistory(final Long changeHistoryId,
            final LaborPayrollChangeHistoryUpdateRequest request) {
        final LaborPayrollChangeHistory changeHistory = laborPayrollChangeHistoryRepository.findById(changeHistoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.CHANGE_HISTORY_NOT_FOUND));

        // memo 필드 수정
        changeHistory.setMemo(request.memo());
        laborPayrollChangeHistoryRepository.save(changeHistory);
    }

    /**
     * 노무인력 ID로 명세서 이력 조회
     * 특정 노무인력의 모든 명세서를 조회 (연월, 현장, 공정 정보 포함)
     */
    @Transactional(readOnly = true)
    public List<LaborPayrollHistoryResponse> getLaborPayrollsByLaborId(final Long laborId) {
        final List<LaborPayroll> laborPayrolls = laborPayrollRepository.findByLaborIdOrderByYearMonthDesc(laborId);

        return laborPayrolls.stream()
                .map(LaborPayrollHistoryResponse::from)
                .toList();
    }

    /**
     * 노무인력 ID로 명세서 이력 조회 (페이징)
     * 특정 노무인력의 명세서를 페이징하여 조회 (연월, 현장, 공정 정보 포함)
     */
    @Transactional(readOnly = true)
    public Page<LaborPayrollHistoryResponse> getLaborPayrollsByLaborId(final Long laborId, final Pageable pageable) {
        final Page<LaborPayroll> laborPayrollsPage = laborPayrollRepository.findByLaborIdOrderByYearMonthDesc(laborId,
                pageable);

        final List<LaborPayrollHistoryResponse> responses = laborPayrollsPage.getContent().stream()
                .map(LaborPayrollHistoryResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, laborPayrollsPage.getTotalElements());
    }

    /**
     * 노무명세서의 인력 이름과 단가 정보를 생성합니다.
     * 
     * @param laborPayroll 노무명세서
     * @return 인력 이름과 단가 정보 문자열
     */
    private String generateLaborDescription(final LaborPayroll laborPayroll) {
        if (laborPayroll.getLabor() != null) {
            final String name = laborPayroll.getLabor().getName();
            final Integer dailyWage = laborPayroll.getDailyWage();

            if (dailyWage != null) {
                final String formattedWage = String.format("%,d", dailyWage);
                return String.format("%s(%s원)", name, formattedWage);
            }
            return name;
        }
        return "인력 정보 없음";
    }

    @Transactional(readOnly = true)
    public LaborPayrollSummary getLaborPayrollSummaryByIdOrThrow(final Long laborPayrollSummaryId) {
        return laborPayrollSummaryRepository.findById(laborPayrollSummaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_SUMMARY_NOT_FOUND));
    }
}
