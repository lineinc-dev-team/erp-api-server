package com.lineinc.erp.api.server.domain.laborpayroll.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;

/**
 * 노무비 명세서 변경 이력 Repository
 */
@Repository
public interface LaborPayrollChangeHistoryRepository extends JpaRepository<LaborPayrollChangeHistory, Long> {

    /**
     * 노무명세서 집계 ID로 변경이력 검색
     */
    @Query("""
            SELECT h FROM LaborPayrollChangeHistory h
            WHERE h.laborPayrollSummary.id = :laborPayrollSummaryId
            """)
    Slice<LaborPayrollChangeHistory> findBySummaryId(
            @Param("laborPayrollSummaryId") Long laborPayrollSummaryId,
            Pageable pageable);
}
