package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.response.ManagementCostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagementCostRepositoryCustom {
    /**
     * 동적 조건으로 관리비 목록을 조회합니다.
     */
    Page<ManagementCostResponse> findAll(
            ManagementCostListRequest request,
            Pageable pageable
    );

}
