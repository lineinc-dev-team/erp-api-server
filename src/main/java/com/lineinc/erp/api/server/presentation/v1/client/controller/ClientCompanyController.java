package com.lineinc.erp.api.server.presentation.v1.client.controller;

import com.lineinc.erp.api.server.application.client.ClientCompanyService;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client-companies")
@RequiredArgsConstructor
@Tag(name = "client-companies", description = "발주처 관련 API")
public class ClientCompanyController {

    private final ClientCompanyService clientCompanyService;

    @Operation(
            summary = "발주처 등록",
            description = "발주처 정보를 등록합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발주처 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @PostMapping
    public ResponseEntity<Void> createClientCompany(
            @Valid @RequestBody ClientCompanyCreateRequest request
    ) {
        clientCompanyService.createClientCompany(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "발주처 전체 조회",
            description = "등록된 모든 발주처 정보를 반환합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발주처 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<PagingResponse<ClientCompanyResponse>>> getAllClientCompanies(
            @Valid ClientCompanyListRequest request
    ) {
        Page<ClientCompanyResponse> page = clientCompanyService.getAllClientCompanies(
                PageableUtils.createPageable(request.page(), request.size(), request.sort())
        );
        return ResponseEntity.ok(SuccessResponse.of(
                new PagingResponse<>(PagingInfo.from(page), page.getContent())
        ));
    }

}
