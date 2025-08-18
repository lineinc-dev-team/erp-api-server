package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;

@Repository
public interface SteelManagementChangeHistoryRepository extends JpaRepository<SteelManagementChangeHistory, Long> {

    Slice<SteelManagementChangeHistory> findBySteelManagementId(Long steelManagementId, Pageable pageable);
}
