package com.lineinc.erp.api.server.domain.clientcompany.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.clientcompany.repository.ClientCompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 발주처 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientCompanyV2Service {

    private final ClientCompanyChangeHistoryRepository clientCompanyChangeHistoryRepository;

    /**
     * 발주처 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param clientCompanyId 발주처 ID
     * @param pageable        페이징 정보
     * @return 발주처 변경 이력 페이지
     */
    public Page<ClientCompanyChangeHistoryResponse> getClientCompanyChangeHistoriesWithPaging(
            final Long clientCompanyId,
            final Pageable pageable) {
        final Page<ClientCompanyChangeHistory> historyPage = clientCompanyChangeHistoryRepository
                .findByClientCompanyWithPaging(clientCompanyId, pageable);
        return historyPage.map(ClientCompanyChangeHistoryResponse::from);
    }
}
