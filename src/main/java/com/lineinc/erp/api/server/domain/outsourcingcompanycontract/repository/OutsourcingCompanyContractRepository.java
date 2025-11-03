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
     * 계약 구분 설명으로 검색하는 메서드
     * 
     * @param keyword  검색할 계약 구분 설명 키워드 (null 가능)
     * @param type     계약 구분 (null 가능)
     * @param pageable 페이징 정보
     * @return 외주업체 계약 슬라이스
     */
    @Query("""
            SELECT oc FROM OutsourcingCompanyContract oc WHERE \
            (:keyword IS NULL OR LOWER(oc.typeDescription) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))) AND \
            oc.deleted = false AND \
            (:type IS NULL OR oc.type = :type)""")
    Slice<OutsourcingCompanyContract> findByTypeDescriptionAndKeyword(@Param("keyword") String keyword,
            @Param("type") OutsourcingCompanyContractType type, Pageable pageable);
}
