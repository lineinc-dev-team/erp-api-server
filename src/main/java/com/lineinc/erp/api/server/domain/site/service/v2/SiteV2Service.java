package com.lineinc.erp.api.server.domain.site.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import com.lineinc.erp.api.server.domain.site.repository.SiteChangeHistoryRepository;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 현장 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteV2Service {

    private final SiteChangeHistoryRepository siteChangeHistoryRepository;

    /**
     * 현장 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param siteId   현장 ID
     * @param pageable 페이징 정보
     * @return 현장 변경 이력 페이지
     */
    public Page<SiteChangeHistoryResponse> getSiteChangeHistoriesWithPaging(
            final Long siteId,
            final CustomUserDetails loginUser,
            final Pageable pageable) {
        final Page<SiteChangeHistory> historyPage = siteChangeHistoryRepository
                .findBySiteIdWithPaging(siteId, pageable);
        return historyPage.map(history -> SiteChangeHistoryResponse.from(history, loginUser.getUserId()));
    }
}
