package com.lineinc.erp.api.server.domain.outsourcing.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;

@Repository
public interface OutsourcingCompanyContractEquipmentRepository
        extends JpaRepository<OutsourcingCompanyContractEquipment, Long> {

    /**
     * 외주업체 계약 ID로 장비 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT e FROM OutsourcingCompanyContractEquipment e " +
            "WHERE e.outsourcingCompanyContract.id = :contractId")
    Page<OutsourcingCompanyContractEquipment> findByOutsourcingCompanyContractId(
            @Param("contractId") Long contractId,
            Pageable pageable);

    /**
     * 외주업체 계약 ID 목록으로 장비 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT e FROM OutsourcingCompanyContractEquipment e " +
            "WHERE e.outsourcingCompanyContract.id IN :contractIds")
    Page<OutsourcingCompanyContractEquipment> findByOutsourcingCompanyContractIdIn(
            @Param("contractIds") List<Long> contractIds,
            Pageable pageable);
}
