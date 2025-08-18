package com.lineinc.erp.api.server.domain.steelmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteelManagementChangeHistoryService {

    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;

    @Transactional(readOnly = true)
    public Slice<SteelManagementChangeHistoryResponse> getSteelManagementChangeHistory(Long steelManagementId,
            Pageable pageable) {
        Slice<SteelManagementChangeHistory> changeHistories = steelManagementChangeHistoryRepository
                .findBySteelManagementId(steelManagementId, pageable);

        return changeHistories.map(SteelManagementChangeHistoryResponse::from);
    }
}
