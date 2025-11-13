package com.lineinc.erp.api.server.domain.aggregation.constructionoutsourcing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportOutsourcingConstructionGroupRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingCompaniesResponse;

import lombok.RequiredArgsConstructor;

/**
 * 외주(공사) 외주업체 집계 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstructionOutsourcingCompanyAggregationService {

    private final DailyReportOutsourcingConstructionGroupRepository dailyReportOutsourcingConstructionGroupRepository;

    /**
     * 외주(공사) 외주업체 목록 조회
     * 현장, 공정으로 필터링하여 외주업체와 외주업체계약을 중복 제거 후 반환
     */
    public List<ConstructionOutsourcingCompaniesResponse> getConstructionOutsourcingCompanies(
            final Long siteId,
            final Long siteProcessId) {
        return dailyReportOutsourcingConstructionGroupRepository
                .findBySiteAndSiteProcess(siteId, siteProcessId)
                .stream()
                .map(group -> ConstructionOutsourcingCompaniesResponse.from(
                        group.getOutsourcingCompany(),
                        group.getOutsourcingCompanyContractConstructionGroup().getOutsourcingCompanyContract()))
                .distinct()
                .toList();
    }
}
