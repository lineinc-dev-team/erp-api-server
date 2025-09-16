package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCostChangeHistoryRepository extends JpaRepository<ManagementCostChangeHistory, Long> {

    /**
     * 관리비의 변경 이력 목록을 조회합니다. (Slice 방식)
     */
    Slice<ManagementCostChangeHistory> findAllByManagementCost(ManagementCost managementCost, Pageable pageable);

    /**
     * 관리비의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param managementCost 관리비 엔티티
     * @param pageable       페이징 정보 (정렬 포함)
     * @return 관리비 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT mch FROM ManagementCostChangeHistory mch WHERE mch.managementCost = :managementCost")
    Page<ManagementCostChangeHistory> findAllByManagementCostWithPaging(
            @Param("managementCost") ManagementCost managementCost, Pageable pageable);
}
