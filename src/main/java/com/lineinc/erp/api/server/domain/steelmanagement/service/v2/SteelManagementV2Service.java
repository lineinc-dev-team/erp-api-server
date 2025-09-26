package com.lineinc.erp.api.server.domain.steelmanagement.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 강재수불부 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SteelManagementV2Service {

    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;

    /**
     * 강재수불부 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param steelManagementId 강재수불부 ID
     * @param pageable          페이징 정보
     * @return 강재수불부 변경 이력 페이지
     */
    public Page<SteelManagementChangeHistoryResponse> getSteelManagementChangeHistoriesWithPaging(
            final Long steelManagementId,
            final CustomUserDetails loginUser,
            final Pageable pageable) {
        final Page<SteelManagementChangeHistory> historyPage = steelManagementChangeHistoryRepository
                .findBySteelManagementIdWithPaging(steelManagementId, pageable);
        return historyPage.map(history -> SteelManagementChangeHistoryResponse.from(history, loginUser.getUserId()));
    }
}
