package com.lineinc.erp.api.server.presentation.v1.client.controller;

import com.lineinc.erp.api.server.application.client.ClientCompanyService;
import com.lineinc.erp.api.server.common.request.PageRequest;
import com.lineinc.erp.api.server.common.request.SortRequest;
import com.lineinc.erp.api.server.common.response.PagingInfo;
import com.lineinc.erp.api.server.common.response.PagingResponse;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.common.util.ResponseHeaderUtils;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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

//    @Operation(
//            summary = "발주처 전체 조회",
//            description = "등록된 모든 발주처 정보를 반환합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "발주처 목록 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
//    })
//    @GetMapping
//    public ResponseEntity<SuccessResponse<PagingResponse<ClientCompanyResponse>>> getAllClientCompanies(
//            @Valid PageRequest pageRequest,
//            @Valid SortRequest sortRequest,
//            @Valid ClientCompanyListRequest request
//    ) {
//        Page<ClientCompanyResponse> page = clientCompanyService.getAllClientCompanies(
//                request,
//                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort())
//        );
//
//        return ResponseEntity.ok(SuccessResponse.of(
//                new PagingResponse<>(PagingInfo.from(page), page.getContent())
//        ));
//    }
//
//    @Operation(
//            summary = "발주처 삭제",
//            description = "하나 이상의 발주처 ID를 받아 해당 발주처를 삭제합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "발주처 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "발주처를 찾을 수 없음"),
//    })
//    @DeleteMapping
//    public ResponseEntity<Void> deleteClientCompanies(@RequestBody List<Long> ids) {
//        clientCompanyService.deleteClientCompanies(ids);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(
//            summary = "발주처 수정",
//            description = "특정 발주처 정보를 수정합니다"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "발주처 수정 성공"),
//            @ApiResponse(responseCode = "400", description = "입력값 오류"),
//            @ApiResponse(responseCode = "404", description = "발주처를 찾을 수 없음"),
//    })
//    @PatchMapping("/{id}")
//    public ResponseEntity<Void> updateClientCompany(
//            @PathVariable Long id,
//            @Valid @RequestBody ClientCompanyUpdateRequest request
//    ) {
//        clientCompanyService.updateClientCompany(id, request);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(
//            summary = "발주처 엑셀 다운로드",
//            description = "등록된 발주처 목록을 엑셀 파일로 다운로드합니다."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
//    })
//    @GetMapping("/download")
//    public void downloadClientCompaniesExcel(
//            HttpServletResponse response,
//            @Valid SortRequest sortRequest,
//            @Valid ClientCompanyListRequest request,
//            @Valid DownloadableRequest downloadableRequest
//    ) throws IOException {
//        List<String> allowedFields = List.of("id", "businessNumber", "name", "ceoName", "address");
//        List<String> validatedFields = downloadableRequest.validatedFields(allowedFields);
//        ResponseHeaderUtils.setExcelDownloadHeader(response, "발주처 목록.xlsx");
//
//        Workbook workbook = clientCompanyService.downloadExcel(
//                request,
//                PageableUtils.parseSort(sortRequest.sort()),
//                validatedFields
//        );
//        workbook.write(response.getOutputStream());
//        workbook.close();
//    }
}
