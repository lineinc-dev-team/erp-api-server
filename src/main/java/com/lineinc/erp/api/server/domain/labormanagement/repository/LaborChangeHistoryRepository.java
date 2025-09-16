package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborChangeHistory;

@Repository
public interface LaborChangeHistoryRepository extends JpaRepository<LaborChangeHistory, Long> {

    /**
     * 특정 인력정보의 변경 이력을 페이징하여 조회 (Slice 방식)
     */
    Slice<LaborChangeHistory> findByLabor(Labor labor, Pageable pageable);

    /**
     * 특정 인력정보의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param labor    인력 엔티티
     * @param pageable 페이징 정보 (정렬 포함)
     * @return 인력 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT lch FROM LaborChangeHistory lch WHERE lch.labor = :labor")
    Page<LaborChangeHistory> findByLaborWithPaging(@Param("labor") Labor labor, Pageable pageable);
}
