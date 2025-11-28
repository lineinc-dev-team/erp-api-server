package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.aggregation.constructionoutsourcing.service.ConstructionOutsourcingCompanyAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.outsourcingcompany.service.OutsourcingCompanyDeductionAggregationService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationDetailRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.DeductionAmountAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingCompaniesResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DeductionAmountAggregationResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 외주업체 집계 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/aggregation/outsourcing-companies")
@Tag(name = "집계")
public class OutsourcingCompanyAggregationController extends BaseController {

    private final ConstructionOutsourcingCompanyAggregationService constructionOutsourcingCompanyAggregationService;
    private final OutsourcingCompanyDeductionAggregationService outsourcingCompanyDeductionAggregationService;

    @GetMapping("")
    @Operation(summary = "외주 집계 조회")
    public ResponseEntity<SuccessResponse<ConstructionOutsourcingAggregationResponse>> getConstructionOutsourcingAggregation(
            @Valid final ConstructionOutsourcingAggregationRequest request) {
        final var response =
                constructionOutsourcingCompanyAggregationService.getConstructionOutsourcingAggregation(request);
        return SuccessResponse.ok(response);
    }

    @GetMapping("/construction")
    @Operation(summary = "외주(공사) 외주업체 목록 조회")
    public ResponseEntity<SuccessResponse<List<ConstructionOutsourcingCompaniesResponse>>> getConstructionOutsourcingCompanies(
            @Valid final ConstructionOutsourcingCompaniesRequest request) {
        final var responseList = constructionOutsourcingCompanyAggregationService
                .getConstructionOutsourcingCompanies(request.siteId(), request.siteProcessId());
        return SuccessResponse.ok(responseList);
    }

    @GetMapping("/construction-detail")
    @Operation(summary = "외주(공사) 집계 상세 조회")
    public ResponseEntity<SuccessResponse<ConstructionOutsourcingAggregationDetailResponse>> getConstructionOutsourcingAggregationDetail(
            @Valid final ConstructionOutsourcingAggregationDetailRequest request) {
        final var response =
                constructionOutsourcingCompanyAggregationService.getConstructionOutsourcingAggregationDetail(request);
        return SuccessResponse.ok(response);
    }

    @GetMapping("/deduction-amount")
    @Operation(summary = "외주(공사) 공제금액 집계 조회")
    public ResponseEntity<SuccessResponse<DeductionAmountAggregationResponse>> getDeductionAmountAggregation(
            @Valid final DeductionAmountAggregationRequest request) {
        final var response = outsourcingCompanyDeductionAggregationService.getDeductionAmountAggregation(request);
        return SuccessResponse.ok(response);
    }
}
