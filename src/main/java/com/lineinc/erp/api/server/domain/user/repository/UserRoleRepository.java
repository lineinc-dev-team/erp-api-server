package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // 특정 Role ID를 가진 UserRole 목록 조회
    List<UserRole> findByRole_Id(Long roleId);

    // 여러 Role ID를 가진 UserRole 목록 조회
    List<UserRole> findByRole_IdIn(List<Long> roleIds);

    // 특정 User와 Role 관계 존재 여부 확인
    boolean existsByUserAndRole(User user, Role role);
}