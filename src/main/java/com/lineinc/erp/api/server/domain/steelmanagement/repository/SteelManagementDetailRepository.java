package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;

@Repository
public interface SteelManagementDetailRepository extends JpaRepository<SteelManagementDetail, Long> {
    @Query("SELECT smd.name, MIN(smd.id) FROM SteelManagementDetail smd GROUP BY smd.name ORDER BY smd.name")
    Slice<Object[]> findAllDistinctNames(Pageable pageable);

    @Query("SELECT smd.name, MIN(smd.id) FROM SteelManagementDetail smd WHERE smd.name LIKE %:keyword% GROUP BY smd.name ORDER BY smd.name")
    Slice<Object[]> findDistinctNamesByKeyword(String keyword, Pageable pageable);
}
