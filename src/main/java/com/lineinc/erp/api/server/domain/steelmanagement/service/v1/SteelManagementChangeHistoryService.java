package com.lineinc.erp.api.server.domain.steelmanagement.service.v1;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteelManagementChangeHistoryService {

    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;
    private final SteelManagementService steelManagementService;

    /**
     * 강재수불부 변경 이력 조회 (Slice 방식)
     */
    @Transactional(readOnly = true)
    public Slice<SteelManagementChangeHistoryResponse> getSteelManagementChangeHistory(final Long steelManagementId,
            final Pageable pageable, final Long userId) {
        final SteelManagement steelManagement = steelManagementService.getSteelManagementByIdOrThrow(steelManagementId);
        final Slice<SteelManagementChangeHistory> changeHistories = steelManagementChangeHistoryRepository
                .findBySteelManagement(steelManagement, pageable);

        return changeHistories.map(history -> SteelManagementChangeHistoryResponse.from(history, userId));
    }

    /**
     * 강재수불부 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<SteelManagementChangeHistoryResponse> getSteelManagementChangeHistoryWithPaging(
            final Long steelManagementId,
            final Pageable pageable, final Long userId) {
        final SteelManagement steelManagement = steelManagementService.getSteelManagementByIdOrThrow(steelManagementId);
        final Page<SteelManagementChangeHistory> changeHistoryPage = steelManagementChangeHistoryRepository
                .findBySteelManagementWithPaging(steelManagement, pageable);

        return changeHistoryPage.map(history -> SteelManagementChangeHistoryResponse.from(history, userId));
    }
}
