package com.lineinc.erp.api.server.domain.laborpayroll.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;
import com.lineinc.erp.api.server.domain.laborpayroll.enums.LaborPayrollChangeType;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollSummaryRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSummaryUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollChangeHistoryUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollSummaryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollChangeHistoryResponse;
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
            LaborPayrollSearchRequest request, PageRequest pageRequest, SortRequest sortRequest) {

        // 페이징 및 정렬 처리
        Pageable pageable = PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort());

        // 집계 테이블에서 조건에 맞는 데이터 조회
        Page<LaborPayrollSummary> summaryPage = laborPayrollSummaryRepository.findBySearchCondition(
                request.siteName(),
                request.processName(),
                request.yearMonth(),
                pageable);

        // 엔티티를 DTO로 변환
        List<LaborPayrollSummaryResponse> responseList = summaryPage.getContent().stream()
                .map(LaborPayrollSummaryResponse::from)
                .collect(Collectors.toList());

        return new PagingResponse<>(PagingInfo.from(summaryPage), responseList);
    }

    /**
     * 노무명세서 엑셀 다운로드
     * 검색 조건에 맞는 노무명세서 목록을 엑셀로 내보내기
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(LaborPayrollSearchRequest request, Sort sort, List<String> fields) {
        List<LaborPayrollSummaryResponse> responses = laborPayrollSummaryRepository
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
    private String getExcelHeaderName(String field) {
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
            case "memo" -> "비고";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값 추출
     */
    private String getExcelCellValue(LaborPayrollSummaryResponse response, String field) {
        return switch (field) {
            case "id" -> response.id() != null ? response.id().toString() : "";
            case "siteName" -> response.site() != null ? response.site().name() : "";
            case "processName" -> response.siteProcess() != null ? response.siteProcess().name() : "";
            case "regularEmployeeCount" ->
                response.regularEmployeeCount() != null ? response.regularEmployeeCount().toString() : "";
            case "directContractCount" ->
                response.directContractCount() != null ? response.directContractCount().toString() : "";
            case "etcCount" -> response.etcCount() != null ? response.etcCount().toString() : "";
            case "totalLaborCost" ->
                response.totalLaborCost() != null ? response.totalLaborCost().toBigInteger().toString() : "";
            case "totalDeductions" ->
                response.totalDeductions() != null ? response.totalDeductions().toBigInteger().toString() : "";
            case "totalNetPayment" ->
                response.totalNetPayment() != null ? response.totalNetPayment().toBigInteger().toString() : "";
            case "memo" -> response.memo() != null ? response.memo() : "";
            default -> "";
        };
    }

    /**
     * 노무명세서 상세 조회
     * 현장 ID, 공정 ID, 년월, 노무인력 타입으로 필터링하여 노무명세서 상세 정보를 조회
     */
    @Transactional(readOnly = true)
    public List<LaborPayrollDetailResponse> getLaborPayrollDetails(Long siteId, Long siteProcessId, String yearMonth,
            LaborType type) {
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
    public LaborPayrollSummaryResponse getLaborPayrollSummaryDetail(Long id) {
        LaborPayrollSummary summary = laborPayrollSummaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_SUMMARY_NOT_FOUND));

        return LaborPayrollSummaryResponse.from(summary);
    }

    /**
     * 노무명세서 집계 테이블 memo 수정
     * 집계 테이블의 비고 필드만 수정 가능
     */
    @Transactional
    public void updateLaborPayrollSummary(Long id, LaborPayrollSummaryUpdateRequest request) {
        LaborPayrollSummary summary = laborPayrollSummaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_SUMMARY_NOT_FOUND));

        // 변경 전 스냅샷 생성
        LaborPayrollSummary oldSnapshot = JaversUtils.createSnapshot(javers, summary, LaborPayrollSummary.class);

        // memo 필드 수정
        summary.setMemo(request.memo());
        laborPayrollSummaryRepository.save(summary);

        // 변경 이력 저장
        Diff diff = javers.compare(oldSnapshot, summary);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            LaborPayrollChangeHistory changeHistory = LaborPayrollChangeHistory.builder()
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
    public void updateLaborPayroll(Long id, LaborPayrollUpdateRequest request) {
        LaborPayroll laborPayroll = laborPayrollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.LABOR_PAYROLL_NOT_FOUND));

        // 변경 전 스냅샷 생성
        LaborPayroll oldSnapshot = JaversUtils.createSnapshot(javers, laborPayroll, LaborPayroll.class);

        // 엔티티의 updateFrom 메서드로 필드 업데이트
        laborPayroll.updateFrom(request);

        laborPayrollRepository.save(laborPayroll);

        // 변경 이력 저장
        Diff diff = javers.compare(oldSnapshot, laborPayroll);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            // 해당하는 LaborPayrollSummary 찾기
            LaborPayrollSummary summary = laborPayrollSummaryRepository
                    .findBySiteAndSiteProcessAndYearMonth(
                            laborPayroll.getSite(),
                            laborPayroll.getSiteProcess(),
                            laborPayroll.getYearMonth())
                    .orElse(null);

            LaborPayrollChangeHistory changeHistory = LaborPayrollChangeHistory.builder()
                    .type(LaborPayrollChangeType.LABOR_PAYROLL)
                    .changes(changesJson)
                    .laborPayrollSummary(summary)
                    .build();
            laborPayrollChangeHistoryRepository.save(changeHistory);
        }
    }

    /**
     * 노무명세서 변경이력 조회
     * 특정 노무명세서 집계와 관련된 변경이력을 조회
     */
    @Transactional(readOnly = true)
    public Slice<LaborPayrollChangeHistoryResponse> getLaborPayrollChangeHistories(
            Long laborPayrollSummaryId, Pageable pageable) {
        Slice<LaborPayrollChangeHistory> changeHistories = laborPayrollChangeHistoryRepository
                .findBySummaryId(laborPayrollSummaryId, pageable);
        return changeHistories.map(LaborPayrollChangeHistoryResponse::from);
    }

    /**
     * 노무명세서 변경이력 수정
     */
    @Transactional
    public void updateLaborPayrollChangeHistory(Long changeHistoryId, LaborPayrollChangeHistoryUpdateRequest request) {
        LaborPayrollChangeHistory changeHistory = laborPayrollChangeHistoryRepository.findById(changeHistoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.CHANGE_HISTORY_NOT_FOUND));

        // memo 필드 수정
        changeHistory.setMemo(request.memo());
        laborPayrollChangeHistoryRepository.save(changeHistory);
    }

}
