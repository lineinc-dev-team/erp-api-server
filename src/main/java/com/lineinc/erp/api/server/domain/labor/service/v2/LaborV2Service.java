package com.lineinc.erp.api.server.domain.labor.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labor.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labor.repository.LaborChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 노무 관리 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaborV2Service {

    private final LaborChangeHistoryRepository laborChangeHistoryRepository;

    /**
     * 노무 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param laborId  노무 ID
     * @param pageable 페이징 정보
     * @return 노무 변경 이력 페이지
     */
    public Page<LaborChangeHistoryResponse> getLaborChangeHistoriesWithPaging(
            final Long laborId,
            final Pageable pageable,
            final Long userId) {
        final Page<LaborChangeHistory> historyPage = laborChangeHistoryRepository
                .findByLaborWithPaging(laborId, pageable);
        return historyPage.map(history -> LaborChangeHistoryResponse.from(history, userId));
    }
}
