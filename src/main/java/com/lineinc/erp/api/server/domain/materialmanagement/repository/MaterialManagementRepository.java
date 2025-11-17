package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;

@Repository
public interface MaterialManagementRepository
        extends JpaRepository<MaterialManagement, Long>, MaterialManagementRepositoryCustom {
    /**
     * 현장, 공정, 납품일자 미만으로 자재관리 목록을 조회합니다.
     */
    List<MaterialManagement> findBySiteIdAndSiteProcessIdAndDeliveryDateLessThanAndDeletedFalse(
            Long siteId, Long siteProcessId, OffsetDateTime deliveryDate);
}
