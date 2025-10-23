package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstructionGroup;

@Repository
public interface OutsourcingCompanyContractConstructionGroupRepository
        extends JpaRepository<OutsourcingCompanyContractConstructionGroup, Long> {
    Page<OutsourcingCompanyContractConstructionGroup> findByOutsourcingCompanyContractId(Long contractId,
            Pageable pageable);

    /**
     * 외주업체 계약 ID 목록으로 공사항목 그룹 정보를 페이징하여 조회합니다.
     */
    @Query("""
            SELECT g FROM OutsourcingCompanyContractConstructionGroup g \
            WHERE g.outsourcingCompanyContract.id IN :contractIds \
            AND g.deleted = false""")
    Page<OutsourcingCompanyContractConstructionGroup> findByOutsourcingCompanyContractIdIn(
            @Param("contractIds") List<Long> contractIds,
            Pageable pageable);
}
