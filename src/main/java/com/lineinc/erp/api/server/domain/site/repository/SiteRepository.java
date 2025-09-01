package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, SiteRepositoryCustom {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Site s WHERE s.name = :name AND s.deleted = false")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT s FROM Site s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.deleted = false")
    Slice<Site> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT s FROM Site s WHERE s.deleted = false")
    Slice<Site> findAllBy(Pageable pageable);
}
