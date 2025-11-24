package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;

@Repository
public interface OutsourcingCompanyContractEquipmentRepository
        extends JpaRepository<OutsourcingCompanyContractEquipment, Long> {

    /**
     * 외주업체 계약 ID로 장비 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT e FROM OutsourcingCompanyContractEquipment e "
            + "WHERE e.outsourcingCompanyContract.id = :contractId AND e.deleted = false")
    Page<OutsourcingCompanyContractEquipment> findByOutsourcingCompanyContractId(
            @Param("contractId") Long contractId, Pageable pageable);

    /**
     * 외주업체 계약 ID 목록으로 장비 정보를 페이징하여 조회합니다.
     */
    @Query("SELECT e FROM OutsourcingCompanyContractEquipment e "
            + "WHERE e.outsourcingCompanyContract.id IN :contractIds AND e.deleted = false")
    Page<OutsourcingCompanyContractEquipment> findByOutsourcingCompanyContractIdIn(
            @Param("contractIds") List<Long> contractIds, Pageable pageable);

    /**
     * 삭제되지 않은 장비 정보를 페이징하여 조회합니다.
     * 차량번호가 제공되면 해당 번호로 검색하고, 제공되지 않으면 모든 데이터를 조회합니다.
     */
    @Query("SELECT e FROM OutsourcingCompanyContractEquipment e "
            + "WHERE (:vehicleNumber IS NULL OR e.vehicleNumber LIKE %:vehicleNumber%) AND e.deleted = false")
    Slice<OutsourcingCompanyContractEquipment> findAllByVehicleNumber(
            @Param("vehicleNumber") String vehicleNumber, Pageable pageable);

    /**
     * 외주업체 계약 ID 목록으로 장비 정보를 키워드로 검색하여 페이징 조회합니다.
     * 키워드는 차량번호 또는 규격으로 검색됩니다.
     */
    @Query("SELECT e FROM OutsourcingCompanyContractEquipment e "
            + "WHERE e.outsourcingCompanyContract.id IN :contractIds "
            + "AND (:keyword IS NULL OR e.vehicleNumber LIKE %:keyword% OR e.specification LIKE %:keyword%) "
            + "AND e.deleted = false")
    Page<OutsourcingCompanyContractEquipment> findByContractIdsAndKeyword(
            @Param("contractIds") List<Long> contractIds, @Param("keyword") String keyword,
            Pageable pageable);
}
