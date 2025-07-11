package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("""
            SELECT r FROM Role r
            LEFT JOIN FETCH r.permissions p
            LEFT JOIN FETCH p.menu m
            WHERE r.id = :roleId
            """)
    Optional<Role> findByIdWithMenusAndPermissions(Long roleId);
}
