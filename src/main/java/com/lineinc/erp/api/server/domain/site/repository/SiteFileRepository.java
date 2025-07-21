package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteFileRepository extends JpaRepository<SiteFile, Long> {
}
