package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;

@Repository
public interface LaborRepository extends JpaRepository<Labor, Long> {

    /**
     * 주민등록번호로 노무 조회 (삭제되지 않은 것만)
     */
    boolean existsByResidentNumberAndDeletedFalse(String residentNumber);
}
