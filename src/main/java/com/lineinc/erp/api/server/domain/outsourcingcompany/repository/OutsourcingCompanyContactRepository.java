package com.lineinc.erp.api.server.domain.outsourcingcompany.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyContact;

public interface OutsourcingCompanyContactRepository extends JpaRepository<OutsourcingCompanyContact, Long> {

    /**
     * 외주업체 ID로 담당자 목록을 조회합니다.
     */
    List<OutsourcingCompanyContact> findByOutsourcingCompanyId(Long outsourcingCompanyId);
}
