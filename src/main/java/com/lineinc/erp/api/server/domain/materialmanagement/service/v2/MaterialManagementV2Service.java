package com.lineinc.erp.api.server.domain.materialmanagement.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 자재관리 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialManagementV2Service {

    private final MaterialManagementChangeHistoryRepository materialManagementChangeHistoryRepository;

    /**
     * 자재관리 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param materialManagementId 자재관리 ID
     * @param pageable             페이징 정보
     * @return 자재관리 변경 이력 페이지
     */
    public Page<MaterialManagementChangeHistoryResponse> getMaterialManagementChangeHistoriesWithPaging(
            final Long materialManagementId,
            final Pageable pageable) {
        final Page<MaterialManagementChangeHistory> historyPage = materialManagementChangeHistoryRepository
                .findByMaterialManagementWithPaging(materialManagementId, pageable);
        return historyPage.map(MaterialManagementChangeHistoryResponse::from);
    }
}
