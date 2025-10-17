package com.lineinc.erp.api.server.interfaces.rest.v1.exceldownloadhistory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.exceldownloadhistory.dto.request.ExcelDownloadHistoryCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 엑셀 다운로드 이력 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/excel-download-histories")
@RequiredArgsConstructor
@Tag(name = "엑셀 다운로드 이력 관리")
public class ExcelDownloadHistoryV1Controller extends BaseController {

    private final UserService userService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;

    @PostMapping
    @Operation(summary = "엑셀 다운로드 이력 생성")
    public ResponseEntity<Void> createExcelDownloadHistory(
            @RequestBody @Valid final ExcelDownloadHistoryCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {

        excelDownloadHistoryService.createHistory(
                request.downloadType(),
                userService.getUserByIdOrThrow(user.getUserId()),
                request.fileUrl());

        return ResponseEntity.ok().build();
    }
}
