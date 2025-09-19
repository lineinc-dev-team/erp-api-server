package com.lineinc.erp.api.server.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;

@Repository
public interface UserChangeHistoryRepository extends JpaRepository<UserChangeHistory, Long> {

    /**
     * 특정 사용자의 변경 이력을 페이징하여 조회합니다. (Slice 방식)
     * 
     * @param user     사용자 엔티티
     * @param pageable 페이징 정보
     * @return 사용자 변경 이력 목록
     */
    Slice<UserChangeHistory> findByUser(User user, Pageable pageable);

    /**
     * 특정 사용자의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param user     사용자 엔티티
     * @param pageable 페이징 정보 (정렬 포함)
     * @return 사용자 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT uch FROM UserChangeHistory uch WHERE uch.user = :user")
    Page<UserChangeHistory> findByUserWithPaging(@Param("user") User user, Pageable pageable);

    /**
     * 사용자 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자 변경 이력 페이지
     */
    @Query("SELECT uch FROM UserChangeHistory uch WHERE uch.user.id = :userId")
    Page<UserChangeHistory> findByUserIdWithPaging(@Param("userId") Long userId, Pageable pageable);
}
