package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByLoginId(String loginId);

    List<User> findAllByRoles_Id(Long roleId);

    List<User> findAllByRoles_IdIn(List<Long> roleIds);

    boolean existsByLoginId(String loginId);
}
