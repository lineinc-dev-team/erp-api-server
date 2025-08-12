package com.lineinc.erp.api.server.domain.outsourcing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractHistory;

@Repository
public interface OutsourcingCompanyContractHistoryRepository
                extends JpaRepository<OutsourcingCompanyContractHistory, Long> {

        /**
         * 외주업체 ID로 계약 이력을 페이징하여 조회합니다.
         */
        @Query("SELECT h FROM OutsourcingCompanyContractHistory h " +
                        "JOIN FETCH h.outsourcingCompany " +
                        "JOIN FETCH h.contract " +
                        "WHERE h.outsourcingCompany.id = :companyId")
        Page<OutsourcingCompanyContractHistory> findByOutsourcingCompanyIdWithPaging(
                        @Param("companyId") Long companyId, Pageable pageable);

}
