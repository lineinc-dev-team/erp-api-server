package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteChangeHistoryRepository extends JpaRepository<SiteChangeHistory, Long> {

    /**
     * 특정 현장의 변경 이력을 페이징하여 조회합니다. (Slice 방식)
     * 
     * @param siteId   현장 ID
     * @param pageable 페이징 정보
     * @return 현장 변경 이력 목록
     */
    Slice<SiteChangeHistory> findBySite(Site site, Pageable pageable);

    /**
     * 특정 현장의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param siteId   현장 ID
     * @param pageable 페이징 정보 (정렬 포함)
     * @return 현장 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT sch FROM SiteChangeHistory sch WHERE sch.site = :site")
    Page<SiteChangeHistory> findBySiteWithPaging(@Param("site") Site site, Pageable pageable);
}
