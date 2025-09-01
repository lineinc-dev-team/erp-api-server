package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Page;

public interface OutsourcingCompanyRepository
        extends JpaRepository<OutsourcingCompany, Long>, OutsourcingCompanyRepositoryCustom {

    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN true ELSE false END FROM OutsourcingCompany oc WHERE oc.businessNumber = :businessNumber AND oc.deleted = false")
    boolean existsByBusinessNumber(@Param("businessNumber") String businessNumber);

    @Query("SELECT oc FROM OutsourcingCompany oc WHERE LOWER(oc.name) LIKE LOWER(CONCAT('%', :name, '%')) AND oc.deleted = false")
    Slice<OutsourcingCompany> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT oc FROM OutsourcingCompany oc WHERE oc.deleted = false")
    Slice<OutsourcingCompany> findAllBy(Pageable pageable);

    /**
     * 장비 데이터가 존재하는 외주업체 목록을 조회합니다.
     */
    @Query("SELECT DISTINCT oc FROM OutsourcingCompany oc " +
            "WHERE oc.deleted = false AND oc.id IN (SELECT DISTINCT c.outsourcingCompany.id FROM OutsourcingCompanyContract c "
            +
            "JOIN c.equipments e WHERE c.deleted = false)")
    Page<OutsourcingCompany> findCompaniesWithEquipment(Pageable pageable);
}
