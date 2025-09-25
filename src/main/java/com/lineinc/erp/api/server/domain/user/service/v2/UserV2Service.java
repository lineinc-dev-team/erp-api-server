package com.lineinc.erp.api.server.domain.user.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.domain.user.repository.UserChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserV2Service {

    private final UserChangeHistoryRepository userChangeHistoryRepository;

    /**
     * 사용자 변경 이력을 페이징하여 조회 (V2)
     * 
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자 변경 이력 페이지
     */
    public Page<UserChangeHistoryResponse> getUserChangeHistoriesWithPaging(final Long userId,
            final Pageable pageable, final Long loggedInUserId) {
        final Page<UserChangeHistory> historyPage = userChangeHistoryRepository.findByUserIdWithPaging(
                userId, pageable);
        return historyPage.map(history -> UserChangeHistoryResponse.from(history, loggedInUserId));
    }

}
