package com.lineinc.erp.api.server.domain.users.repository;

import com.lineinc.erp.api.server.domain.users.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Users> findByLoginId(String loginId);
}
