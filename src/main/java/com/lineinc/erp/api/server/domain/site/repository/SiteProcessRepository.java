package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteProcessRepository extends JpaRepository<SiteProcess, Long> {
    Slice<SiteProcess> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Slice<SiteProcess> findBySiteIdAndNameContainingIgnoreCase(Long siteId, String name, Pageable pageable);

    Slice<SiteProcess> findBySiteId(Long siteId, Pageable pageable);

    Slice<SiteProcess> findAllBy(Pageable pageable);
}
