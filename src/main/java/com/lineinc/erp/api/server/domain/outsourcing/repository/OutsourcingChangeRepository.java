package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutsourcingChangeRepository extends JpaRepository<OutsourcingChangeHistory, Long> {
}
