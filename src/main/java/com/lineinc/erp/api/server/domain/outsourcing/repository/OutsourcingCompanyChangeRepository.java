package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutsourcingCompanyChangeRepository extends JpaRepository<OutsourcingChangeHistory, Long> {

    /**
     * 특정 외주업체의 변경 이력을 페이징하여 조회 (Slice 방식)
     */
    Slice<OutsourcingChangeHistory> findAllByOutsourcingCompany(OutsourcingCompany company, Pageable pageable);

    /**
     * 특정 외주업체의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param company  외주업체 엔티티
     * @param pageable 페이징 정보 (정렬 포함)
     * @return 외주업체 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT och FROM OutsourcingChangeHistory och WHERE och.outsourcingCompany = :company")
    Page<OutsourcingChangeHistory> findAllByOutsourcingCompanyWithPaging(@Param("company") OutsourcingCompany company,
            Pageable pageable);
}
