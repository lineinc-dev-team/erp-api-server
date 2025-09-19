package com.lineinc.erp.api.server.domain.clientcompany.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyChangeHistory;

@Repository
public interface ClientCompanyChangeHistoryRepository extends JpaRepository<ClientCompanyChangeHistory, Long> {

    /**
     * 특정 발주처의 변경 이력을 페이징하여 조회 (Slice 방식)
     * 
     * @param clientCompany 조회할 발주처
     * @param pageable      페이징 정보
     * @return 변경 이력 슬라이스
     */
    Slice<ClientCompanyChangeHistory> findByClientCompany(ClientCompany clientCompany, Pageable pageable);

    /**
     * 특정 발주처의 변경 이력을 전체 개수와 함께 페이징하여 조회합니다.
     * Pageable을 통해 정렬 조건과 페이징 설정 가능
     * 
     * @param clientCompany 조회할 발주처
     * @param pageable      페이징 정보 (정렬 포함)
     * @return 발주처 변경 이력 페이지 (전체 개수 포함)
     */
    @Query("SELECT cch FROM ClientCompanyChangeHistory cch WHERE cch.clientCompany = :clientCompany")
    Page<ClientCompanyChangeHistory> findByClientCompanyWithPaging(@Param("clientCompany") ClientCompany clientCompany,
            Pageable pageable);

}
