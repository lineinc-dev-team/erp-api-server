package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteProcessRepository extends JpaRepository<SiteProcess, Long> {

    /**
     * 통합 검색 메서드 (현장 ID와 키워드 조건부)
     * 
     * @param siteId   현장 ID (null 가능)
     * @param keyword  검색 키워드 (null 가능)
     * @param pageable 페이징 정보
     * @return 공정 슬라이스
     */
    @Query("SELECT sp FROM SiteProcess sp WHERE " +
            "(:siteId IS NULL OR sp.site.id = :siteId) AND " +
            "(:keyword IS NULL OR LOWER(sp.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))) AND " +
            "sp.deleted = false")
    Slice<SiteProcess> findBySiteIdAndKeyword(@Param("siteId") Long siteId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
