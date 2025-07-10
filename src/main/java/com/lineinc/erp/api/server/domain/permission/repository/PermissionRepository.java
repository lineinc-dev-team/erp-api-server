package com.lineinc.erp.api.server.domain.permission.repository;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByMenuAndAction(Menu menu, PermissionAction action);
}
