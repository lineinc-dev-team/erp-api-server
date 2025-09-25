package com.lineinc.erp.api.server.domain.fuelaggregation.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 유류집계 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuelAggregationV2Service {

    private final FuelAggregationChangeHistoryRepository fuelAggregationChangeHistoryRepository;

    /**
     * 유류집계 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param fuelAggregationId 유류집계 ID
     * @param pageable          페이징 정보
     * @return 유류집계 변경 이력 페이지
     */
    public Page<FuelAggregationChangeHistoryResponse> getFuelAggregationChangeHistoriesWithPaging(
            final Long fuelAggregationId,
            final Pageable pageable, final Long userId) {
        final Page<FuelAggregationChangeHistory> historyPage = fuelAggregationChangeHistoryRepository
                .findByFuelAggregationWithPaging(fuelAggregationId, pageable);
        return historyPage.map(history -> FuelAggregationChangeHistoryResponse.from(history, userId));
    }
}
