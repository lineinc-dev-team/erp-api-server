package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.aggregation.equipmentoperation.service.EquipmentOperationStatusService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.EquipmentOperationStatusRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 장비가동현황 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/aggregation/equipment-operation-status")
@RequiredArgsConstructor
@Tag(name = "집계")
public class EquipmentOperationStatusController extends BaseController {

    private final EquipmentOperationStatusService equipmentOperationStatusService;

    @GetMapping
    @Operation(summary = "장비가동현황 조회")
    @RequireMenuPermission(menu = AppConstants.MENU_AGGREGATION_MANAGEMENT, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<EquipmentOperationStatusResponse>> getEquipmentOperationStatus(
            @Valid final EquipmentOperationStatusRequest request) {
        final EquipmentOperationStatusResponse response = equipmentOperationStatusService
                .getEquipmentOperationStatus(request.siteId(), request.siteProcessId(), request.yearMonth());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
