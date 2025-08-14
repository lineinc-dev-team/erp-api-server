package com.lineinc.erp.api.server.domain.materialmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialManagementChangeHistoryService {

    private final MaterialManagementChangeHistoryRepository changeHistoryRepository;
    private final MaterialManagementRepository materialManagementRepository;

    /**
     * 자재관리의 변경이력을 조회합니다.
     */
    public Slice<MaterialManagementChangeHistory> getChangeHistories(Long materialManagementId, Pageable pageable) {
        // 해당 자재관리 존재 여부 확인
        MaterialManagement materialManagement = materialManagementRepository.findById(materialManagementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MATERIAL_MANAGEMENT_NOT_FOUND));

        // 자재관리 변경이력 조회 (페이지 단위)
        Slice<MaterialManagementChangeHistory> historySlice = changeHistoryRepository
                .findByMaterialManagementId(materialManagementId, pageable);

        return historySlice;
    }
}
