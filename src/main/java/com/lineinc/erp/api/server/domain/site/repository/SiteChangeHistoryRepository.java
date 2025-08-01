package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteChangeHistoryRepository extends JpaRepository<SiteChangeHistory, Long> {
}
