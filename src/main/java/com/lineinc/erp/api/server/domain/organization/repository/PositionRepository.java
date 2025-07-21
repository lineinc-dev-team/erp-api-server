package com.lineinc.erp.api.server.domain.organization.repository;

import com.lineinc.erp.api.server.domain.organization.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
