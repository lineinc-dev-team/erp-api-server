package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractType;

@Repository
public interface OutsourcingCompanyContractRepository
        extends JpaRepository<OutsourcingCompanyContract, Long>, OutsourcingCompanyContractRepositoryCustom {

    List<OutsourcingCompanyContract> findByOutsourcingCompanyId(Long outsourcingCompanyId);

    /**
     * 외주업체 ID, 현장 ID, 계약 구분으로 계약 ID 목록을 조회합니다.
     * 
     * @param outsourcingCompanyId 외주업체 ID (필수)
     * @param siteId               현장 ID (null 가능)
     * @param types                계약 구분 목록 (null 또는 빈 리스트 가능)
     * @return 계약 ID 목록
     */
    @Query("""
            SELECT oc.id FROM OutsourcingCompanyContract oc WHERE \
            oc.outsourcingCompany.id = :outsourcingCompanyId AND \
            oc.deleted = false AND \
            (:siteId IS NULL OR oc.site.id = :siteId) AND \
            (:types IS NULL OR oc.type IN :types)""")
    List<Long> findContractIdsByCompanyAndSiteAndTypes(@Param("outsourcingCompanyId") Long outsourcingCompanyId,
            @Param("siteId") Long siteId,
            @Param("types") List<OutsourcingCompanyContractType> types);

    /**
     * 특정 현장에서 체결한 외주업체 계약 금액의 합계를 조회합니다.
     *
     * @param siteId 현장 ID
     * @return 외주 계약 금액 합계 (없으면 0)
     */
    @Query("""
            SELECT COALESCE(SUM(oc.contractAmount), 0) FROM OutsourcingCompanyContract oc
            WHERE oc.site.id = :siteId AND oc.deleted = false
            """)
    Long sumContractAmountBySiteId(@Param("siteId") Long siteId);

    /**
     * 계약명으로 검색하는 메서드
     * 
     * @param keyword              검색할 계약명 키워드 (null 가능)
     * @param types                계약 구분 목록 (null 또는 빈 리스트 가능)
     * @param outsourcingCompanyId 외주업체 ID (null 가능)
     * @param pageable             페이징 정보
     * @return 외주업체 계약 슬라이스
     */
    @Query("""
            SELECT oc FROM OutsourcingCompanyContract oc WHERE \
            (:keyword IS NULL OR LOWER(oc.contractName) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))) AND \
            oc.deleted = false AND \
            (:types IS NULL OR oc.type IN :types) AND \
            (:outsourcingCompanyId IS NULL OR oc.outsourcingCompany.id = :outsourcingCompanyId)""")
    Slice<OutsourcingCompanyContract> findByTypeDescriptionAndKeyword(@Param("keyword") String keyword,
            @Param("types") List<OutsourcingCompanyContractType> types,
            @Param("outsourcingCompanyId") Long outsourcingCompanyId, Pageable pageable);
}
