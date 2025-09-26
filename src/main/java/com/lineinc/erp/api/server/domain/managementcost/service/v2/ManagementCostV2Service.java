package com.lineinc.erp.api.server.domain.managementcost.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 관리비 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementCostV2Service {

    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;

    /**
     * 관리비 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param managementCostId 관리비 ID
     * @param pageable         페이징 정보
     * @return 관리비 변경 이력 페이지
     */
    public Page<ManagementCostChangeHistoryResponse> getManagementCostChangeHistoriesWithPaging(
            final Long managementCostId,
            final CustomUserDetails loginUser,
            final Pageable pageable) {
        final Page<ManagementCostChangeHistory> historyPage = managementCostChangeHistoryRepository
                .findAllByManagementCostWithPaging(managementCostId, pageable);
        return historyPage.map(history -> ManagementCostChangeHistoryResponse.from(history, loginUser.getUserId()));
    }
}
