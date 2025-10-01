package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;

/**
 * 강재수불부 V2 Repository
 */
@Repository
public interface SteelManagementV2Repository
        extends JpaRepository<SteelManagementV2, Long>, SteelManagementV2RepositoryCustom {

    /**
     * 현장과 공정으로 강재수불부 V2 조회
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SteelManagementV2 s WHERE s.site = :site AND s.siteProcess = :siteProcess AND s.deleted = false")
    boolean existsBySiteAndSiteProcess(@Param("site") Site site, @Param("siteProcess") SiteProcess siteProcess);
}
