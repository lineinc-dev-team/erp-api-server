package com.lineinc.erp.api.server.presentation.v1.client.controller;

import com.lineinc.erp.api.server.application.client.ClientCompanyService;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

}
