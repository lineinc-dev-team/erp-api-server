package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.controller;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.dailyreport.service.DailyReportService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFuelUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEmployeeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportOutsourcingResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFuelResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFileResponse;
import com.lineinc.erp.api.server.shared.dto.PageRequest;
import com.lineinc.erp.api.server.shared.dto.SortRequest;
import com.lineinc.erp.api.server.shared.dto.response.SliceInfo;
import com.lineinc.erp.api.server.shared.dto.response.SliceResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/daily-reports")
@RequiredArgsConstructor
@Tag(name = "출역일보", description = "출역일보 관련 API")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    @Operation(summary = "출역일보 등록", description = "새로운 출역일보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출역일보 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장, 공정, 인력 등을 찾을 수 없음", content = @Content())
    })
    @PostMapping
    public ResponseEntity<Void> createDailyReport(
            @Valid @RequestBody DailyReportCreateRequest request) {
        dailyReportService.createDailyReport(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 직원정보 조회", description = "출역일보 직원정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장 또는 공정을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/employees")
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportEmployeeResponse>>> searchDailyReportEmployees(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid DailyReportSearchRequest request) {
        Slice<DailyReportEmployeeResponse> slice = dailyReportService.searchDailyReportEmployees(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 직원정보 수정", description = "출역일보 직원정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "출역일보 직원정보를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/employees")
    public ResponseEntity<Void> updateDailyReportEmployee(
            @Valid DailyReportSearchRequest searchRequest,
            @Valid @RequestBody DailyReportEmployeeUpdateRequest request) {
        dailyReportService.updateDailyReportEmployees(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 직영/계약직 수정", description = "출역일보 직영/계약직 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "출역일보 직영/계약직 정보를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/direct-contracts")
    public ResponseEntity<Void> updateDailyReportDirectContract(
            @Valid DailyReportSearchRequest searchRequest,
            @Valid @RequestBody DailyReportDirectContractUpdateRequest request) {
        dailyReportService.updateDailyReportDirectContracts(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 외주 수정", description = "출역일보 외주 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "출역일보 외주 정보를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/outsourcings")
    public ResponseEntity<Void> updateDailyReportOutsourcing(
            @Valid DailyReportSearchRequest searchRequest,
            @Valid @RequestBody DailyReportOutsourcingUpdateRequest request) {
        dailyReportService.updateDailyReportOutsourcings(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 장비 수정", description = "출역일보 장비 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "출역일보 장비 정보를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/equipments")
    public ResponseEntity<Void> updateDailyReportEquipment(
            @Valid DailyReportSearchRequest searchRequest,
            @Valid @RequestBody DailyReportEquipmentUpdateRequest request) {
        dailyReportService.updateDailyReportEquipments(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 유류 수정", description = "출역일보 유류 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "출역일보 유류 정보를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/fuels")
    public ResponseEntity<Void> updateDailyReportFuel(
            @Valid DailyReportSearchRequest searchRequest,
            @Valid @RequestBody DailyReportFuelUpdateRequest request) {
        dailyReportService.updateDailyReportFuels(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 파일 수정", description = "출역일보 파일 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "출역일보 파일 정보를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/files")
    public ResponseEntity<Void> updateDailyReportFile(
            @Valid DailyReportSearchRequest searchRequest,
            @Valid @RequestBody DailyReportFileUpdateRequest request) {
        dailyReportService.updateDailyReportFiles(searchRequest, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출역일보 직영/계약직 조회", description = "출역일보 직영/계약직 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장 또는 공정을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/direct-contracts")
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportDirectContractResponse>>> searchDailyReportDirectContracts(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid DailyReportSearchRequest request) {
        Slice<DailyReportDirectContractResponse> slice = dailyReportService.searchDailyReportDirectContracts(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 외주 조회", description = "출역일보 외주 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장 또는 공정을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/outsourcings")
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportOutsourcingResponse>>> searchDailyReportOutsourcings(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid DailyReportSearchRequest request) {
        Slice<DailyReportOutsourcingResponse> slice = dailyReportService.searchDailyReportOutsourcings(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 유류 조회", description = "출역일보 유류 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장 또는 공정을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/fuels")
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportFuelResponse>>> searchDailyReportFuels(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid DailyReportSearchRequest request) {
        Slice<DailyReportFuelResponse> slice = dailyReportService.searchDailyReportFuels(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 장비 조회", description = "출역일보 장비 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장 또는 공정을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/equipments")
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportEquipmentResponse>>> searchDailyReportEquipments(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid DailyReportSearchRequest request) {
        Slice<DailyReportEquipmentResponse> slice = dailyReportService.searchDailyReportEquipments(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }

    @Operation(summary = "출역일보 파일 조회", description = "출역일보 파일 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content()),
            @ApiResponse(responseCode = "404", description = "현장 또는 공정을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/files")
    public ResponseEntity<SuccessResponse<SliceResponse<DailyReportFileResponse>>> searchDailyReportFiles(
            @Valid PageRequest pageRequest,
            @Valid SortRequest sortRequest,
            @Valid DailyReportSearchRequest request) {
        Slice<DailyReportFileResponse> slice = dailyReportService.searchDailyReportFiles(
                request,
                PageableUtils.createPageable(pageRequest.page(), pageRequest.size(),
                        sortRequest.sort()));

        return ResponseEntity.ok(SuccessResponse.of(
                new SliceResponse<>(SliceInfo.from(slice), slice.getContent())));
    }
}
