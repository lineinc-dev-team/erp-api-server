package com.lineinc.erp.api.server.interfaces.rest.v1.client.controller;

import static com.lineinc.erp.api.server.shared.constant.AppConstants.MENU_ACCOUNT;
import static com.lineinc.erp.api.server.shared.constant.AppConstants.MENU_CLIENT_COMPANY;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyFileType;
import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyPaymentMethod;
import com.lineinc.erp.api.server.domain.clientcompany.service.v1.ClientCompanyService;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyDeleteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyDownloadRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyFileTypeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyPaymentMethodResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanySimpleResponse;
import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client-companies")
@Tag(name = "발주처 관련 API")
public class ClientCompanyController extends BaseController {

    private final ClientCompanyService clientCompanyService;

    @PostMapping
    @Operation(summary = "발주처 등록")
    @RequireMenuPermission(menu = MENU_CLIENT_COMPANY, action = PermissionAction.CREATE)
    public ResponseEntity<Void> createClientCompany(@Valid @RequestBody final ClientCompanyCreateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        clientCompanyService.createClientCompany(request, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "발주처 목록 조회")
    @RequireMenuPermission(menu = MENU_CLIENT_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<PagingResponse<ClientCompanyResponse>>> getAllClientCompanies(
            @Valid final PageRequest pageRequest, @Valid final SortRequest sortRequest,
            @Valid final ClientCompanyListRequest request) {
        final Page<ClientCompanyResponse> page = clientCompanyService.getAllClientCompanies(request,
                PageableUtils.createPageable(pageRequest, sortRequest));
        return SuccessResponse.ok(PagingResponse.from(page));
    }

    @GetMapping("/search")
    @Operation(summary = "발주처 이름 검색")
    public ResponseEntity<SuccessResponse<SliceResponse<ClientCompanySimpleResponse>>> searchClientCompanyByName(
            @Valid final SortRequest sortRequest, @Valid final PageRequest pageRequest,
            @RequestParam(required = false) final String keyword) {
        final Slice<ClientCompanySimpleResponse> slice = clientCompanyService.searchClientCompanyByName(keyword,
                PageableUtils.createPageable(pageRequest, sortRequest));
        return ResponseEntity.ok(SuccessResponse.of(SliceResponse.from(slice)));
    }

    @DeleteMapping
    @Operation(summary = "발주처 삭제")
    @RequireMenuPermission(menu = MENU_CLIENT_COMPANY, action = PermissionAction.DELETE)
    public ResponseEntity<Void> deleteClientCompanies(@RequestBody final ClientCompanyDeleteRequest clientCompanyIds) {
        clientCompanyService.deleteClientCompanies(clientCompanyIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "발주처 수정")
    @RequireMenuPermission(menu = MENU_CLIENT_COMPANY, action = PermissionAction.UPDATE)
    public ResponseEntity<Void> updateClientCompany(@PathVariable final Long id,
            @Valid @RequestBody final ClientCompanyUpdateRequest request,
            @AuthenticationPrincipal final CustomUserDetails user) {
        clientCompanyService.updateClientCompany(id, request, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download")
    @Operation(summary = "발주처 엑셀 다운로드")
    @RequireMenuPermission(menu = MENU_ACCOUNT, action = PermissionAction.EXCEL_DOWNLOAD)
    public void downloadClientCompaniesExcel(@AuthenticationPrincipal final CustomUserDetails user,
            @Valid final SortRequest sortRequest, @Valid final ClientCompanyListRequest request,
            @Valid final ClientCompanyDownloadRequest companyDownloadRequest, final HttpServletResponse response)
            throws IOException {

        DownloadFieldUtils.validateFields(DownloadFieldUtils.parseFields(companyDownloadRequest.fields()),
                ClientCompanyDownloadRequest.ALLOWED_FIELDS);

        try (Workbook workbook =
                clientCompanyService.downloadExcel(user, request, PageableUtils.parseSort(sortRequest.sort()),
                        DownloadFieldUtils.parseFields(companyDownloadRequest.fields()))) {
            ResponseHeaderUtils.setExcelDownloadHeader(response, "발주처 목록.xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "발주처 상세 조회")
    @RequireMenuPermission(menu = MENU_CLIENT_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<ClientCompanyDetailResponse>> getClientCompanyById(
            @PathVariable final Long id) {
        final ClientCompanyDetailResponse response = clientCompanyService.getClientCompanyById(id);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/{id}/change-histories")
    @Operation(summary = "발주처 변경 이력 조회")
    @RequireMenuPermission(menu = MENU_CLIENT_COMPANY, action = PermissionAction.VIEW)
    public ResponseEntity<SuccessResponse<SliceResponse<ClientCompanyChangeHistoryResponse>>> getClientCompanyChangeHistories(
            @PathVariable final Long id, @Valid final PageRequest pageRequest, @Valid final SortRequest sortRequest,
            @AuthenticationPrincipal final CustomUserDetails user) {
        final Slice<ClientCompanyChangeHistoryResponse> slice = clientCompanyService.getClientCompanyChangeHistories(id,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(), sortRequest.sort()), user);
        return ResponseEntity.ok(SuccessResponse.of(new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "발주처 결제 수단 목록 조회")
    public ResponseEntity<SuccessResponse<List<ClientCompanyPaymentMethodResponse>>> getPaymentMethods() {
        final List<ClientCompanyPaymentMethodResponse> paymentMethods =
                Arrays.stream(ClientCompanyPaymentMethod.values()).map(ClientCompanyPaymentMethodResponse::from)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(SuccessResponse.of(paymentMethods));
    }

    @GetMapping("/file-types")
    @Operation(summary = "발주처 파일 타입 목록 조회")
    public ResponseEntity<SuccessResponse<List<ClientCompanyFileTypeResponse>>> getFileTypes() {
        final List<ClientCompanyFileTypeResponse> responseList = Arrays.stream(ClientCompanyFileType.values())
                .map(ClientCompanyFileTypeResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(SuccessResponse.of(responseList));
    }

}
