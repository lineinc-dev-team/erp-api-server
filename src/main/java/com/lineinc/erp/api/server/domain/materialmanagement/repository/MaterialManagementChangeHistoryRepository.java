package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;

@Repository
public interface MaterialManagementChangeHistoryRepository
        extends JpaRepository<MaterialManagementChangeHistory, Long> {
    Slice<MaterialManagementChangeHistory> findByMaterialManagement(MaterialManagement materialManagement,
            Pageable pageable);
}
