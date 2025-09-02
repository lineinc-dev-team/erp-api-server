package com.lineinc.erp.api.server.domain.outsourcingcontract.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractSubEquipment;

@Repository
public interface OutsourcingCompanyContractSubEquipmentRepository
        extends JpaRepository<OutsourcingCompanyContractSubEquipment, Long> {
}
