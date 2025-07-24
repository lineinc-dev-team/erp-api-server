package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.response.ManagementCostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ManagementCostRepositoryCustom {
    /**
     * 동적 조건으로 관리비 목록을 조회합니다.
     */
    Page<ManagementCostResponse> findAll(
            ManagementCostListRequest request,
            Pageable pageable
    );

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 관리비 목록 전체 조회
     */
    List<ManagementCost> findAllWithoutPaging(
            ManagementCostListRequest request,
            Sort sort
    );
}
