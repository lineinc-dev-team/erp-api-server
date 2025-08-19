package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;

@Repository
public interface MaterialManagementDetailRepository extends JpaRepository<MaterialManagementDetail, Long> {
    @Query("SELECT mmd.name, MIN(mmd.id) FROM MaterialManagementDetail mmd GROUP BY mmd.name ORDER BY mmd.name")
    Slice<Object[]> findAllDistinctNames(Pageable pageable);

    @Query("SELECT mmd.name, MIN(mmd.id) FROM MaterialManagementDetail mmd WHERE mmd.name LIKE %:keyword% GROUP BY mmd.name ORDER BY mmd.name")
    Slice<Object[]> findDistinctNamesByKeyword(String keyword, Pageable pageable);
}
