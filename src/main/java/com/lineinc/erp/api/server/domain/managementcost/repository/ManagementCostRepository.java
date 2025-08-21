package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCostRepository extends JpaRepository<ManagementCost, Long>, ManagementCostRepositoryCustom {

    /**
     * 특정 itemType의 itemDescription 값들을 조회
     */
    @Query("SELECT DISTINCT mc.itemDescription, mc.id FROM ManagementCost mc WHERE mc.itemType = :itemType AND mc.itemDescription IS NOT NULL")
    Slice<Object[]> findAllDistinctItemDescriptions(@Param("itemType") ItemType itemType, Pageable pageable);

    /**
     * 특정 itemType의 itemDescription 값들을 키워드로 검색
     */
    @Query("SELECT DISTINCT mc.itemDescription, mc.id FROM ManagementCost mc WHERE mc.itemType = :itemType AND mc.itemDescription IS NOT NULL AND mc.itemDescription LIKE %:keyword%")
    Slice<Object[]> findDistinctItemDescriptionsByKeyword(@Param("itemType") ItemType itemType,
            @Param("keyword") String keyword, Pageable pageable);
}
