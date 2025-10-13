// package
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.controller;

// import java.util.Arrays;
// import java.util.List;

// import org.apache.poi.ss.usermodel.Workbook;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Slice;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
// import
// com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
// import
// com.lineinc.erp.api.server.domain.steelmanagement.service.v1.SteelManagementChangeHistoryService;
// import
// com.lineinc.erp.api.server.domain.steelmanagement.service.v1.SteelManagementService;
// import
// com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
// import
// com.lineinc.erp.api.server.infrastructure.config.security.RequireMenuPermission;
// import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.ApproveSteelManagementRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.DeleteSteelManagementRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.ReleaseSteelManagementRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementCreateRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementDownloadRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementListRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementUpdateRequest;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementChangeHistoryResponse;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementDetailViewResponse;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementNameResponse;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementResponse;
// import
// com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementTypeResponse;
// import com.lineinc.erp.api.server.shared.constant.AppConstants;
// import com.lineinc.erp.api.server.shared.dto.request.PageRequest;
// import com.lineinc.erp.api.server.shared.dto.request.SortRequest;
// import com.lineinc.erp.api.server.shared.dto.response.PagingInfo;
// import com.lineinc.erp.api.server.shared.dto.response.PagingResponse;
// import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
// import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
// import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
// import com.lineinc.erp.api.server.shared.util.DownloadFieldUtils;
// import com.lineinc.erp.api.server.shared.util.PageableUtils;
// import com.lineinc.erp.api.server.shared.util.ResponseHeaderUtils;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/v1/steel-managements")
// @RequiredArgsConstructor
// @Tag(name = "강재수불부 관리", description = "강재수불부 관리 API")
// public class SteelManagementController extends BaseController {
// private final SteelManagementService steelManagementService;
// private final SteelManagementChangeHistoryService
// steelManagementChangeHistoryService;

// @Operation(summary = "강재수불부 등록", description = "강재수불부 정보를 등록합니다.")
// @PostMapping
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.CREATE)
// public ResponseEntity<SuccessResponse<Long>> createSteelManagement(
// @Valid @RequestBody final SteelManagementCreateRequest request,
// @AuthenticationPrincipal final CustomUserDetails user) {
// steelManagementService.createSteelManagement(request, user);
// return ResponseEntity.ok().build();
// }

// @Operation(summary = "강재 관리 수정", description = "강재 관리 정보를 수정합니다.")
// @PatchMapping("/{id}")
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.UPDATE)
// public ResponseEntity<Void> updateSteelManagement(
// @PathVariable final Long id,
// @Valid @RequestBody final SteelManagementUpdateRequest request,
// @AuthenticationPrincipal final CustomUserDetails user) {
// steelManagementService.updateSteelManagement(id, request, user);
// return ResponseEntity.ok().build();
// }

// @Operation(summary = "강재수불부 목록 조회", description = "등록된 강재수불부 목록을 조회합니다.")
// @GetMapping
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.VIEW)
// public
// ResponseEntity<SuccessResponse<PagingResponse<SteelManagementResponse>>>
// getSteelManagementList(
// @Valid final PageRequest pageRequest,
// @Valid final SortRequest sortRequest,
// @Valid final SteelManagementListRequest request) {
// final Page<SteelManagementResponse> page =
// steelManagementService.getSteelManagementList(
// request,
// PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
// sortRequest.sort()));

// return ResponseEntity.ok(SuccessResponse.of(
// new PagingResponse<>(PagingInfo.from(page), page.getContent())));
// }

// @Operation(summary = "강재수불부 구분 목록 조회", description = "강재수불부 구분 목록을 반환합니다")
// @GetMapping("/steel-management-types")
// public ResponseEntity<SuccessResponse<List<SteelManagementTypeResponse>>>
// getSteelManagementTypes() {
// final List<SteelManagementTypeResponse> responseList =
// Arrays.stream(SteelManagementType.values())
// .map(type -> new SteelManagementTypeResponse(type.name(), type.getLabel()))
// .toList();
// return ResponseEntity.ok(SuccessResponse.of(responseList));
// }

// @Operation(summary = "강재수불부 상세 품명 키워드 검색", description = "상세 품명으로 간단한 검색을
// 수행합니다.")
// @GetMapping("/detail-names/search")
// public
// ResponseEntity<SuccessResponse<SliceResponse<SteelManagementNameResponse>>>
// getSteelManagementDetailNames(
// @Valid final PageRequest pageRequest,
// @Valid final SortRequest sortRequest,
// @RequestParam(required = false) final String keyword) {
// final Pageable pageable = PageableUtils.createPageable(pageRequest.page(),
// pageRequest.size(), sortRequest.sort());
// final Slice<SteelManagementNameResponse> slice =
// steelManagementService.getSteelManagementNames(keyword,
// pageable);

// return ResponseEntity.ok(SuccessResponse.of(
// new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
// }

// @Operation(summary = "강재수불부 삭제", description = "하나 이상의 강재수불부 ID를 받아 해당 데이터를
// 삭제합니다.")
// @DeleteMapping
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.DELETE)
// public ResponseEntity<Void> deleteSteelManagements(
// @Valid @RequestBody final DeleteSteelManagementRequest request) {
// steelManagementService.deleteSteelManagements(request);
// return ResponseEntity.ok().build();
// }

// @Operation(summary = "강재수불부 승인 처리", description = "하나 이상의 강재수불부 ID를 받아 구분값을
// 승인으로 변경합니다.")
// @PatchMapping("/approve")
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.APPROVE)
// public ResponseEntity<Void> approveSteelManagement(
// @Valid @RequestBody final ApproveSteelManagementRequest request,
// @AuthenticationPrincipal final CustomUserDetails user) {
// steelManagementService.approveSteelManagements(request, user);
// return ResponseEntity.ok().build();
// }

// @Operation(summary = "강재수불부 반출 처리", description = "하나 이상의 강재수불부 ID를 받아 구분값을
// 반출로 변경합니다.")
// @PatchMapping("/release")
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.VIEW)
// public ResponseEntity<Void> releaseSteelManagements(
// @Valid @RequestBody final ReleaseSteelManagementRequest request,
// @AuthenticationPrincipal final CustomUserDetails user) {
// steelManagementService.releaseSteelManagements(request, user);
// return ResponseEntity.ok().build();
// }

// @Operation(summary = "강재수불부 엑셀 다운로드", description = "검색 조건에 맞는 강재수불부 목록을 엑셀
// 파일로 다운로드합니다.")
// @GetMapping("/download")
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.VIEW)
// public void downloadSteelManagementsExcel(
// @Valid final SortRequest sortRequest,
// @Valid final SteelManagementListRequest request,
// @Valid final SteelManagementDownloadRequest steelManagementDownloadRequest,
// final HttpServletResponse response) throws java.io.IOException {
// final List<String> parsed =
// DownloadFieldUtils.parseFields(steelManagementDownloadRequest.fields());
// DownloadFieldUtils.validateFields(parsed,
// SteelManagementDownloadRequest.ALLOWED_FIELDS);
// ResponseHeaderUtils.setExcelDownloadHeader(response, "강재수불부 목록.xlsx");

// try (Workbook workbook = steelManagementService.downloadExcel(
// request,
// PageableUtils.parseSort(sortRequest.sort()),
// parsed)) {
// workbook.write(response.getOutputStream());
// }
// }

// @Operation(summary = "강재수불부 상세 조회", description = "강재수불부 상세 정보를 조회합니다.")
// @GetMapping("/{id}")
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.VIEW)
// public ResponseEntity<SuccessResponse<SteelManagementDetailViewResponse>>
// getSteelManagementDetail(
// @PathVariable final Long id) {
// final SteelManagementDetailViewResponse steelManagementDetailViewResponse =
// steelManagementService
// .getSteelManagementById(id);
// return
// ResponseEntity.ok(SuccessResponse.of(steelManagementDetailViewResponse));
// }

// @Operation(summary = "강재수불부 수정이력 조회", description = "강재수불부의 수정이력을 조회합니다")
// @GetMapping("/{id}/change-histories")
// @RequireMenuPermission(menu = AppConstants.MENU_STEEL_MANAGEMENT, action =
// PermissionAction.VIEW)
// public
// ResponseEntity<SuccessResponse<SliceResponse<SteelManagementChangeHistoryResponse>>>
// getSteelManagementChangeHistories(
// @PathVariable final Long id,
// @Valid final PageRequest pageRequest,
// @Valid final SortRequest sortRequest,
// @AuthenticationPrincipal final CustomUserDetails user) {

// final Pageable pageable = PageableUtils.createPageable(pageRequest.page(),
// pageRequest.size(), sortRequest.sort());
// final var slice =
// steelManagementChangeHistoryService.getSteelManagementChangeHistory(id,
// pageable,
// user.getUserId());

// return ResponseEntity.ok(SuccessResponse.of(
// new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
// }
// }
