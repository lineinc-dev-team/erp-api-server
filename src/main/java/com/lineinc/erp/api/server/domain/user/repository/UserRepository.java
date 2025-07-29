package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    Slice<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Slice<User> findAllBy(Pageable pageable);

    @Query("select u from User u " +
            "left join fetch u.userRoles ur " +
            "left join fetch ur.role " +
            "where u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
}
