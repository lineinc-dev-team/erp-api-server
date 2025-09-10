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

    /**
     * 로그인 ID로 삭제되지 않은 사용자를 조회합니다.
     * 
     * @param loginId 로그인 ID
     * @return 사용자 정보가 포함된 Optional (없으면 empty)
     */
    Optional<User> findByLoginIdAndDeletedFalse(String loginId);

    /**
     * 로그인 ID가 존재하는지 확인합니다.
     * 
     * @param loginId 확인할 로그인 ID
     * @return 로그인 ID가 존재하면 true, 아니면 false
     */
    boolean existsByLoginIdAndDeletedFalse(String loginId);

    /**
     * 키워드로 사용자를 검색합니다.
     * 
     * @param keyword         검색 키워드 (null 또는 빈 문자열이면 전체 검색)
     * @param excludeUsername 제외할 사용자명
     * @param hasRole         역할 필터 (null=전체, true=역할보유자만, false=역할미보유자만)
     * @param pageable        페이징 정보
     * @return 검색된 사용자 목록
     */
    @Query("""
            SELECT u FROM User u
            WHERE u.deleted = false
            AND LOWER(u.username) != :excludeUsername
            AND (:keyword IS NULL OR :keyword = '' OR u.username LIKE %:keyword%)
            AND (:hasRole IS NULL OR
                (CASE WHEN :hasRole = true
                    THEN EXISTS (SELECT 1 FROM u.userRoles ur WHERE ur.deleted = false AND ur.role.deleted = false)
                    ELSE NOT EXISTS (SELECT 1 FROM u.userRoles ur WHERE ur.deleted = false AND ur.role.deleted = false)
                END))
            """)
    Slice<User> findAllByKeywordAndExcludeUsername(@Param("keyword") String keyword,
            @Param("excludeUsername") String excludeUsername, @Param("hasRole") Boolean hasRole, Pageable pageable);

    /**
     * 사용자 ID로 사용자와 역할 정보를 함께 조회합니다.
     * 
     * @param id 사용자 ID
     * @return 사용자와 역할 정보가 포함된 Optional
     */
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    /**
     * 사용자가 특정 메뉴에 대한 특정 액션 권한을 가지고 있는지 확인합니다.
     * 
     * @param userId   사용자 ID
     * @param menuName 메뉴명
     * @param action   권한 액션
     * @return 권한이 있으면 true, 없으면 false
     */
    @Query("""
            SELECT EXISTS(
                SELECT 1 FROM User u
                JOIN u.userRoles ur
                JOIN ur.role r
                JOIN r.permissions rp
                JOIN rp.permission p
                JOIN p.menu m
                WHERE u.id = :userId AND m.name = :menuName AND p.action = :action
            )
            """)
    boolean hasPermission(@Param("userId") Long userId, @Param("menuName") String menuName,
            @Param("action") PermissionAction action);
}
