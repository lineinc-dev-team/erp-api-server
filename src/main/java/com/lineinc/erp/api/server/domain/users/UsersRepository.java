package com.lineinc.erp.api.server.domain.users;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Users> findByLoginId(String loginId);
}
