package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;

@Repository
public interface FuelInfoRepository extends JpaRepository<FuelInfo, Long> {

}
