package com.lineinc.erp.api.server.domain.permission.repository;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByMenuAndAction(Menu menu, PermissionAction action);

    Optional<Permission> findByMenuAndAction(Menu menu, PermissionAction action);

    List<Permission> findByMenu(Menu menu);

    @Modifying
    @Query(value = "DELETE FROM roles_permissions WHERE role_id = :roleId", nativeQuery = true)
    void deleteAllByRoleIdNative(@Param("roleId") Long roleId);
}
