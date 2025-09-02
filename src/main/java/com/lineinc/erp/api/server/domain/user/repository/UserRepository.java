package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByLoginId(String loginId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.loginId = :loginId AND u.deleted = false")
    boolean existsByLoginId(@Param("loginId") String loginId);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND LOWER(u.username) != :excludeUsername AND (:keyword IS NULL OR :keyword = '' OR u.username LIKE %:keyword%) AND (:hasRole IS NULL OR (CASE WHEN :hasRole = true THEN EXISTS (SELECT 1 FROM u.userRoles) ELSE NOT EXISTS (SELECT 1 FROM u.userRoles) END)) ORDER BY u.username ASC")
    Slice<User> findAllByKeywordAndExcludeUsername(@Param("keyword") String keyword,
            @Param("excludeUsername") String excludeUsername, @Param("hasRole") Boolean hasRole, Pageable pageable);

    @Query("select u from User u " +
            "left join fetch u.userRoles ur " +
            "left join fetch ur.role " +
            "where u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("select u from User u " +
            "left join fetch u.userRoles ur " +
            "left join fetch ur.role r " +
            "left join fetch r.permissions rp " +
            "left join fetch rp.permission p " +
            "left join fetch p.menu m " +
            "where u.id = :id")
    Optional<User> findByIdWithPermissions(@Param("id") Long id);

    // 권한 체크 전용 쿼리 - 성능 최적화
    @Query("SELECT COUNT(p) > 0 FROM User u " +
            "JOIN u.userRoles ur " +
            "JOIN ur.role r " +
            "JOIN r.permissions rp " +
            "JOIN rp.permission p " +
            "JOIN p.menu m " +
            "WHERE u.id = :userId AND m.name = :menuName AND p.action = :action ")
    boolean hasPermission(@Param("userId") Long userId, @Param("menuName") String menuName,
            @Param("action") PermissionAction action);

    long countByIdIn(Iterable<Long> ids);
}
