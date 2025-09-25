package com.lineinc.erp.api.server.domain.laborpayroll.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response.LaborPayrollChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 노무명세서 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaborPayrollV2Service {

    private final LaborPayrollChangeHistoryRepository laborPayrollChangeHistoryRepository;

    /**
     * 노무명세서 집계 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param laborPayrollSummaryId 노무명세서 집계 ID
     * @param pageable              페이징 정보
     * @return 노무명세서 변경 이력 페이지
     */
    public Page<LaborPayrollChangeHistoryResponse> getLaborPayrollChangeHistoriesWithPaging(
            final Long laborPayrollSummaryId,
            final Pageable pageable,
            final Long userId) {
        final Page<LaborPayrollChangeHistory> historyPage = laborPayrollChangeHistoryRepository
                .findBySummaryIdWithPaging(laborPayrollSummaryId, pageable);
        return historyPage.map(history -> LaborPayrollChangeHistoryResponse.from(history, userId));
    }
}
