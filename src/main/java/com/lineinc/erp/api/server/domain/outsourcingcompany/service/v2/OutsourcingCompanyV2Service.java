package com.lineinc.erp.api.server.domain.outsourcingcompany.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompany.repository.OutsourcingCompanyChangeRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyChangeHistoryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 외주업체 관련 V2 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingCompanyV2Service {

    private final OutsourcingCompanyChangeRepository outsourcingCompanyChangeRepository;

    /**
     * 외주업체 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param companyId 외주업체 ID
     * @param pageable  페이징 정보
     * @return 외주업체 변경 이력 페이지
     */
    public Page<CompanyChangeHistoryResponse> getOutsourcingCompanyChangeHistoriesWithPaging(
            final Long companyId,
            final Pageable pageable) {
        final Page<OutsourcingCompanyChangeHistory> historyPage = outsourcingCompanyChangeRepository
                .findByCompanyIdWithPaging(companyId, pageable);
        return historyPage.map(CompanyChangeHistoryResponse::from);
    }
}
