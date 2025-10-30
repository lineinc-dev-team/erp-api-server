package com.lineinc.erp.api.server.domain.managementcost.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ManagementCostRepository extends JpaRepository<ManagementCost, Long>, ManagementCostRepositoryCustom {

    /**
     * 특정 itemType의 itemDescription 값들을 조회
     */
    @Query("SELECT DISTINCT mc.itemTypeDescription, mc.id FROM ManagementCost mc WHERE mc.itemType = :itemType AND mc.itemTypeDescription IS NOT NULL AND mc.deleted = false")
    Slice<Object[]> findAllDistinctItemDescriptions(@Param("itemType") ManagementCostItemType itemType,
            Pageable pageable);

    /**
     * 특정 itemType의 itemDescription 값들을 키워드로 검색
     */
    @Query("SELECT DISTINCT mc.itemTypeDescription, mc.id FROM ManagementCost mc WHERE mc.itemType = :itemType AND mc.itemTypeDescription IS NOT NULL AND mc.itemTypeDescription LIKE %:keyword% AND mc.deleted = false")
    Slice<Object[]> findDistinctItemDescriptionsByKeyword(@Param("itemType") ManagementCostItemType itemType,
            @Param("keyword") String keyword, Pageable pageable);

    // site, siteProcess, paymentDate < endExclusive, deleted = false
    List<ManagementCost> findBySiteIdAndSiteProcessIdAndPaymentDateLessThanAndDeletedFalse(
        Long siteId,
        Long siteProcessId,
        OffsetDateTime endExclusive);
}
