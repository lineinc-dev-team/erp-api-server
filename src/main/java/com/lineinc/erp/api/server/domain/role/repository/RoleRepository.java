package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {

    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.deleted = false")
    Optional<Role> findByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.name = :name AND r.deleted = false")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT r FROM Role r " +
            "LEFT JOIN FETCH r.permissions rp " +
            "LEFT JOIN FETCH rp.permission p " +
            "LEFT JOIN FETCH p.menu m " +
            "WHERE r.id = :roleId")
    Optional<Role> findWithPermissionsAndMenusById(@Param("roleId") Long roleId);
}
