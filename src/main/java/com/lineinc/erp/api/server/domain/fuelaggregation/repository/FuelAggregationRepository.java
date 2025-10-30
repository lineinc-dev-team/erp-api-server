package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

@Repository
public interface FuelAggregationRepository
        extends JpaRepository<FuelAggregation, Long>, FuelAggregationRepositoryCustom {

    /**
     * 현장, 공정, 일자로 유류집계 조회
     */
    Optional<FuelAggregation> findBySiteAndSiteProcessAndDate(Site site, SiteProcess siteProcess, OffsetDateTime date);

    /**
     * 현장, 공정, 날짜(미만) 기준으로 "삭제되지 않은" 유류집계 목록을 조회합니다.
     * - endExclusive: 기준일(UTC) 미만
     */
    List<FuelAggregation> findBySiteAndSiteProcessAndDateLessThanAndDeletedFalse(
            Site site,
            SiteProcess siteProcess,
            OffsetDateTime yearMonth);

}
