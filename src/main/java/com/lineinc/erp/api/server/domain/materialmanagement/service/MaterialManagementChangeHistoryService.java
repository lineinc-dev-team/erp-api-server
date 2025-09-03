package com.lineinc.erp.api.server.domain.materialmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialManagementChangeHistoryService {

    private final MaterialManagementChangeHistoryRepository changeHistoryRepository;

    /**
     * 자재관리의 변경이력을 조회합니다.
     */
    public Slice<MaterialManagementChangeHistory> getChangeHistories(Long materialManagementId, Pageable pageable) {
        // 자재관리 변경이력 조회 (페이지 단위)
        Slice<MaterialManagementChangeHistory> historySlice = changeHistoryRepository
                .findByMaterialManagementId(materialManagementId, pageable);

        return historySlice;
    }
}
