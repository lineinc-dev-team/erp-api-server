package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT r FROM Role r " +
            "JOIN FETCH r.permissions " +
            "JOIN FETCH r.permissions.permission " +
            "JOIN FETCH r.permissions.permission.menu " +
            "WHERE r.id = :roleId")
    Optional<Role> findWithPermissionsAndMenusById(@Param("roleId") Long roleId);
}
