package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementChangeHistoryV2;

/**
 * 강재수불부 V2 변경 이력 Repository
 */
@Repository
public interface SteelManagementChangeHistoryV2Repository
        extends JpaRepository<SteelManagementChangeHistoryV2, Long> {

    /**
     * 강재수불부 ID로 변경 이력을 페이징하여 조회
     */
    @Query("""
            SELECT h FROM SteelManagementChangeHistoryV2 h \
            WHERE h.steelManagementV2.id = :steelManagementV2Id \
            ORDER BY h.createdAt DESC""")
    Page<SteelManagementChangeHistoryV2> findBySteelManagementV2IdWithPaging(
            @Param("steelManagementV2Id") Long steelManagementV2Id,
            Pageable pageable);
}
