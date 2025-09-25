package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 외주업체 계약 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingCompanyContractV2Service {

    private final OutsourcingCompanyContractChangeHistoryRepository contractChangeHistoryRepository;

    /**
     * 계약 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param contractId 계약 ID
     * @param pageable   페이징 정보
     * @return 계약 변경 이력 페이지
     */
    public Page<ContractChangeHistoryResponse> getContractChangeHistoriesWithPaging(
            final Long contractId,
            final Pageable pageable, final Long userId) {
        final Page<OutsourcingCompanyContractChangeHistory> historyPage = contractChangeHistoryRepository
                .findByContractIdWithPaging(contractId, pageable);
        return historyPage.map(history -> ContractChangeHistoryResponse.from(history, userId));
    }
}
