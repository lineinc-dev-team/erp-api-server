package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;

@Repository
public interface SteelManagementChangeHistoryRepository extends JpaRepository<SteelManagementChangeHistory, Long> {

    /**
     * 강재수불부의 변경 이력 목록을 조회합니다. (Slice 방식)
     */
    Slice<SteelManagementChangeHistory> findBySteelManagement(SteelManagement steelManagement, Pageable pageable);

    /**
     * 강재수불부의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param steelManagementId 강재수불부 ID
     * @param pageable          페이징 정보 (정렬 포함)
     * @return 강재수불부 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT smch FROM SteelManagementChangeHistory smch WHERE smch.steelManagement = :steelManagement")
    Page<SteelManagementChangeHistory> findBySteelManagementWithPaging(
            @Param("steelManagement") SteelManagement steelManagement, Pageable pageable);

    /**
     * 강재수불부 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param steelManagementId 강재수불부 ID
     * @param pageable          페이징 정보
     * @return 강재수불부 변경 이력 페이지
     */
    @Query("SELECT smch FROM SteelManagementChangeHistory smch WHERE smch.steelManagement.id = :steelManagementId")
    Page<SteelManagementChangeHistory> findBySteelManagementIdWithPaging(
            @Param("steelManagementId") Long steelManagementId, Pageable pageable);
}
