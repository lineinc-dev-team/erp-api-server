package com.lineinc.erp.api.server.domain.role.respository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
