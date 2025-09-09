package com.lineinc.erp.api.server.domain.laborpayroll.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollSummaryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollSummaryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollDetailResponse;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
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
                request.siteId(),
                request.siteProcessId(),
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
                .findAllWithoutPaging(request.siteId(), request.siteProcessId(), request.yearMonth(), sort)
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
     * 특정 년월의 모든 노무명세서 상세 정보를 조회
     */
    @Transactional(readOnly = true)
    public List<LaborPayrollDetailResponse> getLaborPayrollDetails(String yearMonth) {
        List<LaborPayroll> laborPayrolls = laborPayrollRepository.findByYearMonth(yearMonth);

        return laborPayrolls.stream()
                .map(LaborPayrollDetailResponse::from)
                .toList();
    }

}
