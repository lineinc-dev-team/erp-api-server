package com.lineinc.erp.api.server.domain.site.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.site.entity.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, SiteRepositoryCustom {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Site s WHERE s.name = :name AND s.deleted = false")
    boolean existsByName(@Param("name") String name);

    @Query("""
            SELECT s FROM Site s WHERE \
            (:keyword IS NULL OR :keyword = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) \
            AND s.deleted = false \
            AND (:siteIds IS NULL OR s.id IN :siteIds)""")
    Slice<Site> findByNameContainingIgnoreCaseAndIdIn(@Param("keyword") String keyword,
            @Param("siteIds") List<Long> siteIds, Pageable pageable);

    /**
     * 현장명으로 조회
     */
    @Query("SELECT s FROM Site s WHERE s.name = :name AND s.deleted = false")
    java.util.Optional<Site> findByName(@Param("name") String name);
}
