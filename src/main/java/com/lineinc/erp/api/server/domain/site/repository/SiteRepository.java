package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, SiteRepositoryCustom {
    boolean existsByName(String name);

    Slice<Site> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Slice<Site> findAllBy(Pageable pageable);
}
