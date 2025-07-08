package com.lineinc.erp.api.server.domain.roles.repository;

import com.lineinc.erp.api.server.domain.roles.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);
}
