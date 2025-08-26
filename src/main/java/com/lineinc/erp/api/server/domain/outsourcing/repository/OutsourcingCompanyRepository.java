package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Page;

public interface OutsourcingCompanyRepository
        extends JpaRepository<OutsourcingCompany, Long>, OutsourcingCompanyRepositoryCustom {
    boolean existsByBusinessNumber(String businessNumber);

    Slice<OutsourcingCompany> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Slice<OutsourcingCompany> findAllBy(Pageable pageable);

    /**
     * 장비 데이터가 존재하는 외주업체 목록을 조회합니다.
     */
    @Query("SELECT DISTINCT oc FROM OutsourcingCompany oc " +
            "WHERE oc.id IN (SELECT DISTINCT c.outsourcingCompany.id FROM OutsourcingCompanyContract c " +
            "JOIN c.equipments e)")
    Page<OutsourcingCompany> findCompaniesWithEquipment(Pageable pageable);
}
