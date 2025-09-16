package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;

@Repository
public interface MaterialManagementChangeHistoryRepository
        extends JpaRepository<MaterialManagementChangeHistory, Long> {

    /**
     * 자재관리의 변경 이력 목록을 조회합니다. (Slice 방식)
     */
    Slice<MaterialManagementChangeHistory> findByMaterialManagement(MaterialManagement materialManagement,
            Pageable pageable);

    /**
     * 자재관리의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param materialManagementId 자재관리 ID
     * @param pageable             페이징 정보 (정렬 포함)
     * @return 자재관리 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT mmch FROM MaterialManagementChangeHistory mmch WHERE mmch.materialManagement = :materialManagement")
    Page<MaterialManagementChangeHistory> findByMaterialManagementWithPaging(
            @Param("materialManagement") MaterialManagement materialManagement, Pageable pageable);
}
