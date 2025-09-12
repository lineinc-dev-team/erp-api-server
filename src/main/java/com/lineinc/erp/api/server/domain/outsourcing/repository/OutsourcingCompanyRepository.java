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

    /**
     * 사업자등록번호 중복 확인
     * 
     * @param businessNumber 확인할 사업자등록번호
     * @return 중복 여부
     */
    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN true ELSE false END FROM OutsourcingCompany oc WHERE oc.businessNumber = :businessNumber AND oc.deleted = false")
    boolean existsByBusinessNumber(@Param("businessNumber") String businessNumber);

    /**
     * 통합 검색 메서드 (외주업체명 조건부)
     * 
     * @param name     검색할 외주업체명 (null 가능)
     * @param pageable 페이징 정보
     * @return 외주업체 슬라이스
     */
    @Query("SELECT oc FROM OutsourcingCompany oc WHERE " +
            "(:name IS NULL OR LOWER(oc.name) LIKE LOWER(CONCAT('%', CAST(:name AS text), '%'))) AND " +
            "oc.deleted = false")
    Slice<OutsourcingCompany> findByNameAndKeyword(@Param("name") String name, Pageable pageable);

    /**
     * 장비 데이터가 존재하는 외주업체 목록을 조회합니다.
     * 
     * @param pageable 페이징 정보
     * @return 장비 보유 외주업체 페이지
     */
    @Query("SELECT DISTINCT oc FROM OutsourcingCompany oc " +
            "WHERE oc.deleted = false AND oc.id IN (SELECT DISTINCT c.outsourcingCompany.id FROM OutsourcingCompanyContract c "
            +
            "JOIN c.equipments e WHERE c.deleted = false)")
    Page<OutsourcingCompany> findCompaniesWithEquipment(Pageable pageable);
}
