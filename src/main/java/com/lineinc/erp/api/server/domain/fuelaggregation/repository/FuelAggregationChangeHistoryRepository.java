package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;

@Repository
public interface FuelAggregationChangeHistoryRepository extends JpaRepository<FuelAggregationChangeHistory, Long> {

    /**
     * 유류집계의 변경 이력 목록을 조회합니다. (Slice 방식)
     */
    Slice<FuelAggregationChangeHistory> findByFuelAggregation(FuelAggregation fuelAggregation, Pageable pageable);

    /**
     * 유류집계의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param fuelAggregation 유류집계
     * @param pageable        페이징 정보 (정렬 포함)
     * @return 유류집계 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT fach FROM FuelAggregationChangeHistory fach WHERE fach.fuelAggregation = :fuelAggregation")
    Page<FuelAggregationChangeHistory> findByFuelAggregationWithPaging(
            @Param("fuelAggregation") FuelAggregation fuelAggregation, Pageable pageable);
}
