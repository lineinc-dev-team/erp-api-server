package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
