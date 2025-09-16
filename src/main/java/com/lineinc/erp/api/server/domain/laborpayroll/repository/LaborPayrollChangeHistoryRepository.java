package com.lineinc.erp.api.server.domain.laborpayroll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;

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

    /**
     * 노무명세서 집계로 변경이력을 전체 개수와 함께 페이징하여 조회
     * 페이지 네비게이션이 필요한 경우 사용
     * 
     * @param laborPayrollSummary 노무명세서 집계
     * @param pageable            페이징 정보 (정렬 포함)
     * @return 노무명세서 변경이력 페이지 (전체 개수 포함)
     */
    @Query("""
            SELECT h FROM LaborPayrollChangeHistory h
            WHERE h.laborPayrollSummary = :laborPayrollSummary
            """)
    Page<LaborPayrollChangeHistory> findBySummaryIdWithPaging(
            @Param("laborPayrollSummary") LaborPayrollSummary laborPayrollSummary,
            Pageable pageable);
}
