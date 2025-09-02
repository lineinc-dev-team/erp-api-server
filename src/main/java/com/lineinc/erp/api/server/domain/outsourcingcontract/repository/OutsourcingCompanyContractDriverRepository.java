package com.lineinc.erp.api.server.domain.outsourcingcontract.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractDriver;

@Repository
public interface OutsourcingCompanyContractDriverRepository
        extends JpaRepository<OutsourcingCompanyContractDriver, Long> {

    /**
     * 외주업체 계약 ID로 기사(운전자) 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT d FROM OutsourcingCompanyContractDriver d " +
            "WHERE d.outsourcingCompanyContract.id = :contractId " +
            "AND d.deleted = false")
    Page<OutsourcingCompanyContractDriver> findByOutsourcingCompanyContractId(
            @Param("contractId") Long contractId,
            Pageable pageable);

    /**
     * 외주업체 계약 ID 목록으로 기사(운전자) 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT d FROM OutsourcingCompanyContractDriver d " +
            "WHERE d.outsourcingCompanyContract.id IN :contractIds " +
            "AND d.deleted = false")
    Page<OutsourcingCompanyContractDriver> findByOutsourcingCompanyContractIdIn(
            @Param("contractIds") List<Long> contractIds,
            Pageable pageable);
}
