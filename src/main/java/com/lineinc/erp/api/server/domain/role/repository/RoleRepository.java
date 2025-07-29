package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"permissions", "permissions.permission.menu"})
    Optional<Role> findWithPermissionsAndMenusById(Long roleId);
}
