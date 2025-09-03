package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;

@Repository
public interface MaterialManagementDetailRepository extends JpaRepository<MaterialManagementDetail, Long> {
    @Query("SELECT mmd.name, mmd.id FROM MaterialManagementDetail mmd WHERE mmd.deleted = false AND mmd.materialManagement.deleted = false")
    Slice<Object[]> findAllDistinctNames(Pageable pageable);

    @Query("SELECT mmd.name, mmd.id FROM MaterialManagementDetail mmd WHERE mmd.deleted = false AND mmd.materialManagement.deleted = false AND mmd.name LIKE %:keyword%")
    Slice<Object[]> findDistinctNamesByKeyword(String keyword, Pageable pageable);
}
