package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutsourcingChangeRepository extends JpaRepository<OutsourcingChangeHistory, Long> {
    Slice<OutsourcingChangeHistory> findAllByOutsourcingCompany(OutsourcingCompany company, Pageable pageable);
}
