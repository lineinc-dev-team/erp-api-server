package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Repository
public interface UserChangeHistoryRepository extends JpaRepository<UserChangeHistory, Long> {

    /**
     * 특정 사용자의 변경 이력을 페이징하여 조회합니다.
     * 
     * @param user     사용자 엔티티
     * @param pageable 페이징 정보
     * @return 사용자 변경 이력 목록
     */
    Slice<UserChangeHistory> findByUser(User user, Pageable pageable);
}
