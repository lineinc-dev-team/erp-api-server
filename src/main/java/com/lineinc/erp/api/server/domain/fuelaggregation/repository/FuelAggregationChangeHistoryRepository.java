package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;

@Repository
public interface FuelAggregationChangeHistoryRepository extends JpaRepository<FuelAggregationChangeHistory, Long> {

    Slice<FuelAggregationChangeHistory> findByFuelAggregationId(Long fuelAggregationId, Pageable pageable);
}
