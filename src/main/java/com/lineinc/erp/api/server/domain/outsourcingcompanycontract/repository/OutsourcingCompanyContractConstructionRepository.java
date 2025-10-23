package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;

@Repository
public interface OutsourcingCompanyContractConstructionRepository
        extends JpaRepository<OutsourcingCompanyContractConstruction, Long> {

    /**
     * 외주업체 계약 ID로 공사항목 정보를 페이징하여 조회합니다.
     */
    @Query("""
            SELECT c FROM OutsourcingCompanyContractConstruction c \
            WHERE c.outsourcingCompanyContract.id = :contractId \
            AND c.deleted = false""")
    Page<OutsourcingCompanyContractConstruction> findByOutsourcingCompanyContractId(
            @Param("contractId") Long contractId,
            Pageable pageable);

    /**
     * 외주업체 계약 ID 목록으로 공사항목명 목록을 조회합니다.
     */
    @Query("""
            SELECT c FROM OutsourcingCompanyContractConstruction c \
            WHERE c.outsourcingCompanyContract.id IN :contractIds \
            AND c.deleted = false""")
    Page<OutsourcingCompanyContractConstruction> findByOutsourcingCompanyContractIdIn(
            @Param("contractIds") List<Long> contractIds,
            Pageable pageable);

    /**
     * 외주업체 ID, 항목 그룹 ID, 공사항목 이름으로 규격 목록을 조회합니다.
     */
    @Query("""
            SELECT DISTINCT c.specification FROM OutsourcingCompanyContractConstruction c \
            WHERE (:itemName IS NULL OR c.item = :itemName) \
            AND (:constructionGroupId IS NULL OR c.constructionGroup.id = :constructionGroupId) \
            AND c.outsourcingCompanyContract.outsourcingCompany.id = :outsourcingCompanyId \
            AND c.specification IS NOT NULL \
            AND c.specification != '' \
            AND c.deleted = false \
            ORDER BY c.specification""")
    List<String> findDistinctSpecificationsByConditions(
            @Param("itemName") String itemName,
            @Param("constructionGroupId") Long constructionGroupId,
            @Param("outsourcingCompanyId") Long outsourcingCompanyId);
}
