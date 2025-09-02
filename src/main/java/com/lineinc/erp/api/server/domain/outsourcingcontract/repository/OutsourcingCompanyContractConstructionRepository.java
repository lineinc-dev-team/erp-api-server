package com.lineinc.erp.api.server.domain.outsourcingcontract.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractConstruction;

@Repository
public interface OutsourcingCompanyContractConstructionRepository
        extends JpaRepository<OutsourcingCompanyContractConstruction, Long> {

    /**
     * 외주업체 계약 ID로 공사항목 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT c FROM OutsourcingCompanyContractConstruction c " +
            "WHERE c.outsourcingCompanyContract.id = :contractId " +
            "AND c.deleted = false")
    Page<OutsourcingCompanyContractConstruction> findByOutsourcingCompanyContractId(
            @Param("contractId") Long contractId,
            Pageable pageable);
}
