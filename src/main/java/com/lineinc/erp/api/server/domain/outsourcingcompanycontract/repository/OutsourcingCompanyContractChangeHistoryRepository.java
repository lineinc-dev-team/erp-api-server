package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractChangeHistory;

@Repository
public interface OutsourcingCompanyContractChangeHistoryRepository
        extends CrudRepository<OutsourcingCompanyContractChangeHistory, Long> {

    /**
     * 계약 ID로 변경 이력을 페이징하여 조회합니다.
     */
    Slice<OutsourcingCompanyContractChangeHistory> findByOutsourcingCompanyContractId(
            Long contractId, Pageable pageable);

    /**
     * 계약 ID로 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param contractId 계약 ID
     * @param pageable   페이징 정보 (정렬 포함)
     * @return 계약 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT ch FROM OutsourcingCompanyContractChangeHistory ch WHERE ch.outsourcingCompanyContract = :contract")
    Page<OutsourcingCompanyContractChangeHistory> findByOutsourcingCompanyContractWithPaging(
            @Param("contract") OutsourcingCompanyContract contract, Pageable pageable);

    /**
     * 계약 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param contractId 계약 ID
     * @param pageable   페이징 정보
     * @return 계약 변경 이력 페이지
     */
    @Query("SELECT ch FROM OutsourcingCompanyContractChangeHistory ch WHERE ch.outsourcingCompanyContract.id = :contractId")
    Page<OutsourcingCompanyContractChangeHistory> findByContractIdWithPaging(
            @Param("contractId") Long contractId, Pageable pageable);
}
