package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.response.SteelManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SteelManagementRepositoryCustom {
    /**
     * 동적 조건으로 강재 관리 목록을 조회합니다.
     */
    Page<SteelManagementResponse> findAll(
            SteelManagementListRequest request,
            Pageable pageable
    );

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 강재 관리 목록 전체 조회
     */
    List<SteelManagement> findAllWithoutPaging(
            SteelManagementListRequest request,
            Sort sort
    );
}
