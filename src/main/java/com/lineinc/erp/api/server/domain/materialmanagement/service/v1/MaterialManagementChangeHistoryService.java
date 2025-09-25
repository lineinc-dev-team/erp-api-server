package com.lineinc.erp.api.server.domain.materialmanagement.service.v1;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialManagementChangeHistoryService {

    private final MaterialManagementChangeHistoryRepository changeHistoryRepository;
    private final MaterialManagementService materialManagementService;

    /**
     * 자재관리의 변경이력을 조회합니다. (Slice 방식)
     */
    public Slice<MaterialManagementChangeHistory> getChangeHistories(final Long materialManagementId,
            final Pageable pageable) {
        final MaterialManagement materialManagement = materialManagementService
                .getMaterialManagementByIdOrThrow(materialManagementId);
        // 자재관리 변경이력 조회 (페이지 단위)
        final Slice<MaterialManagementChangeHistory> historySlice = changeHistoryRepository
                .findByMaterialManagement(materialManagement, pageable);

        return historySlice;
    }

    /**
     * 자재관리 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    public Page<MaterialManagementChangeHistoryResponse> getChangeHistoriesWithPaging(final Long materialManagementId,
            final Pageable pageable, final Long userId) {
        final MaterialManagement materialManagement = materialManagementService
                .getMaterialManagementByIdOrThrow(materialManagementId);
        final Page<MaterialManagementChangeHistory> historyPage = changeHistoryRepository
                .findByMaterialManagementWithPaging(materialManagement, pageable);

        return historyPage.map(history -> MaterialManagementChangeHistoryResponse.from(history, userId));
    }
}
