package com.lineinc.erp.api.server.domain.outsourcing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;

import java.util.List;

@Repository
public interface OutsourcingCompanyContractWorkerRepository
        extends JpaRepository<OutsourcingCompanyContractWorker, Long> {

    /**
     * 외주업체 계약 ID로 인력 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT w FROM OutsourcingCompanyContractWorker w " +
            "WHERE w.outsourcingCompanyContract.id = :contractId " +
            "AND w.deleted = false")
    Page<OutsourcingCompanyContractWorker> findByOutsourcingCompanyContractId(
            @Param("contractId") Long contractId,
            Pageable pageable);

    /**
     * 계약 ID 목록으로 인력 정보를 페이징하여 조회합니다.
     */
    Page<OutsourcingCompanyContractWorker> findByOutsourcingCompanyContractIdIn(List<Long> contractIds,
            Pageable pageable);
}
