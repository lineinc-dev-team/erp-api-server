package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChangeHistoryRepository extends JpaRepository<UserChangeHistory, Long> {


}

