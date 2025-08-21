package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborChangeHistory;

@Repository
public interface LaborChangeHistoryRepository extends JpaRepository<LaborChangeHistory, Long> {

    /**
     * 특정 인력정보의 변경 이력을 페이징하여 조회
     */
    Slice<LaborChangeHistory> findByLabor(Labor labor, Pageable pageable);
}
