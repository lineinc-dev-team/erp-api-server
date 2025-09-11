package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyChangeHistoryRepository extends JpaRepository<ClientCompanyChangeHistory, Long> {

    /**
     * 특정 발주처의 변경 이력을 페이징하여 조회
     * 
     * @param clientCompany 조회할 발주처
     * @param pageable      페이징 정보
     * @return 변경 이력 슬라이스
     */
    Slice<ClientCompanyChangeHistory> findByClientCompany(ClientCompany clientCompany, Pageable pageable);

}
