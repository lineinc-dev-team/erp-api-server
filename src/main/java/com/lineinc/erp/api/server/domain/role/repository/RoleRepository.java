package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {

    /**
     * 롤명으로 삭제되지 않은 롤 조회
     * 
     * @param name 조회할 롤명
     * @return 해당 롤명의 롤 (없으면 Optional.empty())
     */
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.deleted = false")
    Optional<Role> findByName(@Param("name") String name);

    /**
     * 롤명으로 삭제되지 않은 롤의 존재 여부 확인
     * 
     * @param name 확인할 롤명
     * @return 롤이 존재하면 true, 없으면 false
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.name = :name AND r.deleted = false")
    boolean existsByName(@Param("name") String name);

    /**
     * 롤 ID로 권한과 메뉴 정보를 함께 조회
     * N+1 문제를 방지하기 위해 JOIN FETCH 사용
     * 
     * @param roleId 조회할 롤 ID
     * @return 권한과 메뉴 정보가 포함된 롤 (없으면 Optional.empty())
     */
    @Query("""
            SELECT r FROM Role r
            LEFT JOIN FETCH r.permissions rp
            LEFT JOIN FETCH rp.permission p
            LEFT JOIN FETCH p.menu m
            WHERE r.id = :roleId
            """)
    Optional<Role> findRoleWithPermissions(@Param("roleId") Long roleId);

    /**
     * 롤 ID로 권한, 메뉴, 활성 현장/공정 정보를 함께 조회
     * 삭제되지 않은 롤과 현장/공정만 조회
     * 
     * @param roleId 조회할 롤 ID
     * @return 권한, 메뉴, 활성 현장/공정 정보가 포함된 롤 (없으면 Optional.empty())
     */
    @Query("""
            SELECT r FROM Role r
            LEFT JOIN FETCH r.permissions rp
            LEFT JOIN FETCH rp.permission p
            LEFT JOIN FETCH p.menu m
            LEFT JOIN FETCH r.siteProcesses rsp
            LEFT JOIN FETCH rsp.site s
            LEFT JOIN FETCH rsp.process sp
            WHERE r.id = :roleId
            AND r.deleted = false
            AND (s.deleted = false OR s IS NULL)
            AND (sp.deleted = false OR sp IS NULL)
            """)
    Optional<Role> findRoleWithDetails(@Param("roleId") Long roleId);

    /**
     * 롤명으로 검색하여 페이징된 결과 조회
     * - 삭제되지 않은 롤만 조회
     * - ID가 1인 롤 제외 (시스템 기본 롤)
     * - 키워드가 null이면 모든 롤 조회, 있으면 부분 일치 검색
     * 
     * @param keyword  검색할 롤명 키워드 (null 가능)
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 롤들의 페이징된 결과
     */
    @Query(value = """
            SELECT * FROM roles r
            WHERE r.deleted = false
            AND r.id != 1
            AND (:keyword IS NULL OR r.name::text LIKE CONCAT('%', :keyword, '%'))
            """, nativeQuery = true)
    Page<Role> searchRoles(@Param("keyword") String keyword, Pageable pageable);
}
