package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteChangeHistoryRepository extends JpaRepository<SiteChangeHistory, Long> {
    Page<SiteChangeHistory> findBySiteId(Long siteId, Pageable pageable);
}
