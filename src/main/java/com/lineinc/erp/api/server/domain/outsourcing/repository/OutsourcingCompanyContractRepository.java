package com.lineinc.erp.api.server.domain.outsourcing.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;

@Repository
public interface OutsourcingCompanyContractRepository extends JpaRepository<OutsourcingCompanyContract, Long> {

    /**
     * 외주계약 리스트를 검색 조건에 따라 페이징하여 조회합니다.
     */
    @Query("SELECT c FROM OutsourcingCompanyContract c " +
            "JOIN FETCH c.outsourcingCompany " +
            "JOIN FETCH c.site " +
            "JOIN FETCH c.siteProcess " +
            "WHERE (:companyName IS NULL OR LOWER(c.outsourcingCompany.name) LIKE LOWER(CONCAT('%', :companyName, '%'))) "
            +
            "AND (:siteName IS NULL OR LOWER(c.site.name) LIKE LOWER(CONCAT('%', :siteName, '%'))) " +
            "AND (:processName IS NULL OR LOWER(c.siteProcess.name) LIKE LOWER(CONCAT('%', :processName, '%'))) " +
            "AND (:contractType IS NULL OR c.type = :contractType) " +
            "AND (:contractStatus IS NULL OR c.status = :contractStatus) " +
            "AND (:contractStartDate IS NULL OR c.contractStartDate >= :contractStartDate) " +
            "AND (:contractEndDate IS NULL OR c.contractEndDate >= :contractEndDate) " +
            "AND (:contactName IS NULL OR EXISTS (SELECT 1 FROM c.contacts contact WHERE LOWER(contact.name) LIKE LOWER(CONCAT('%', :contactName, '%'))))")
    Page<OutsourcingCompanyContract> findContractsWithSearch(
            @Param("companyName") String companyName,
            @Param("siteName") String siteName,
            @Param("processName") String processName,
            @Param("contractType") OutsourcingCompanyContractType contractType,
            @Param("contractStatus") OutsourcingCompanyContractStatus contractStatus,
            @Param("contractStartDate") OffsetDateTime contractStartDate,
            @Param("contractEndDate") OffsetDateTime contractEndDate,
            @Param("contactName") String contactName,
            Pageable pageable);
}
