package com.lineinc.erp.api.server.domain.laborpayroll.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollSummaryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollSummaryResponse;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
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

}
