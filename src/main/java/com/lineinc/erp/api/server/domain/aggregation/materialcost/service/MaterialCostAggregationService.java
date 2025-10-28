package com.lineinc.erp.api.server.domain.aggregation.materialcost.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementV2Repository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.FuelAggregationItemResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.SteelManagementItemResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 재료비 집계 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialCostAggregationService {

    private final MaterialManagementRepository materialManagementRepository;
    private final SteelManagementV2Repository steelManagementV2Repository;
    private final FuelAggregationRepository fuelAggregationRepository;

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;

    /**
     * 재료비 집계 조회
     * 
     * @param siteName        현장명
     * @param siteProcessName 공정명
     * @param yearMonth       조회월 (YYYY-MM)
     * @return 재료비 집계 응답
     */
    public MaterialCostAggregationResponse getMaterialCostAggregation(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {

        // 현장 조회
        final Site site = siteService.getSiteByIdOrThrow(siteId);

        // 공정 조회
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(siteProcessId);

        // 자재관리 데이터 조회
        final List<MaterialManagement> materialManagements = materialManagementRepository
                .findBySiteAndSiteProcessAndYearMonthLessThanEqual(site, siteProcess, yearMonth);

        // 강재수불부 데이터 조회
        final List<SteelManagementV2> steelManagements = steelManagementV2Repository
                .findBySiteAndSiteProcessAndYearMonthLessThanEqual(site, siteProcess, yearMonth);

        // 유류집계 데이터 조회
        final List<FuelAggregation> fuelAggregations = fuelAggregationRepository
                .findBySiteAndSiteProcessAndYearMonthLessThanEqual(site, siteProcess, yearMonth);

        // 자재관리 응답 생성 (업체+품명별로 그룹핑하여 전회까지/금회 집계)
        final List<MaterialManagementItemResponse> materialManagementResponses = aggregateMaterialManagements(
                materialManagements, yearMonth);

        // 강재수불부 응답 생성 (detail의 업체 정보 기반)
        final List<SteelManagementItemResponse> steelManagementResponses = steelManagements.stream()
                .flatMap(sm -> sm.getDetails().stream()
                        .filter(detail -> detail.getOutsourcingCompany() != null) // 업체 정보가 있는 경우만 포함
                        .map(detail -> {
                            final OutsourcingCompany company = detail.getOutsourcingCompany();
                            return new SteelManagementItemResponse(
                                    company.getBusinessNumber(),
                                    detail.getName(), // 품명
                                    company.getName(),
                                    company.getCeoName(),
                                    company.getPhoneNumber(),
                                    company.getBankName(),
                                    company.getAccountNumber(),
                                    company.getAccountHolder());
                        }))
                .toList();

        // 유류집계 응답 생성 (FuelInfo의 업체 정보 기반)
        final List<FuelAggregationItemResponse> fuelAggregationResponses = fuelAggregations.stream()
                .flatMap(fa -> fa.getFuelInfos().stream()
                        .filter(fi -> fi.getOutsourcingCompany() != null) // 업체 정보가 있는 경우만 포함
                        .map(fi -> {
                            final OutsourcingCompany company = fi.getOutsourcingCompany();
                            return new FuelAggregationItemResponse(
                                    company.getBusinessNumber(),
                                    fi.getFuelType() != null ? fi.getFuelType().getLabel() : "", // 품명 (유종)
                                    company.getName(),
                                    company.getCeoName(),
                                    company.getPhoneNumber(),
                                    company.getBankName(),
                                    company.getAccountNumber(),
                                    company.getAccountHolder());
                        }))
                .toList();

        return new MaterialCostAggregationResponse(
                materialManagementResponses,
                steelManagementResponses,
                fuelAggregationResponses);
    }

    /**
     * 자재관리 데이터를 업체+품명별로 그룹핑하여 전회까지/금회 청구내역 집계
     * 
     * @param materialManagements 자재관리 목록
     * @param yearMonth           조회월 (YYYY-MM)
     * @return 집계된 자재관리 응답 목록
     */
    private List<MaterialManagementItemResponse> aggregateMaterialManagements(
            final List<MaterialManagement> materialManagements,
            final String yearMonth) {

        final OffsetDateTime currentMonthStartDateTime = calculateMonthStartDateTime(yearMonth);

        // 업체+품명별로 그룹핑
        final Map<String, List<Map.Entry<MaterialManagement, MaterialManagementDetail>>> groupedByCompanyAndItem = materialManagements
                .stream()
                .flatMap(mm -> mm.getDetails().stream()
                        .map(detail -> Map.entry(mm, detail)))
                .collect(Collectors.groupingBy(this::createGroupingKey));

        // 각 그룹별로 전회까지/금회 집계하여 응답 생성
        return groupedByCompanyAndItem.values().stream()
                .map(group -> createMaterialManagementItemResponse(group, currentMonthStartDateTime))
                .toList();
    }

    /**
     * 그룹핑 키 생성: 업체ID_품명
     */
    private String createGroupingKey(final Map.Entry<MaterialManagement, MaterialManagementDetail> entry) {
        return entry.getKey().getOutsourcingCompany().getId() + "_" + entry.getValue().getName();
    }

    /**
     * 그룹에서 자재관리 응답 생성 (전회까지/금회 집계)
     */
    private MaterialManagementItemResponse createMaterialManagementItemResponse(
            final List<Map.Entry<MaterialManagement, MaterialManagementDetail>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        // 그룹의 첫번째 항목에서 업체와 품명 정보 가져오기
        final MaterialManagement firstMm = group.get(0).getKey();
        final MaterialManagementDetail firstDetail = group.get(0).getValue();

        // 전회까지/금회 청구내역 집계
        final BillingDetail previousBilling = aggregatePreviousBilling(group, currentMonthStartDateTime);
        final BillingDetail currentBilling = aggregateCurrentBilling(group, currentMonthStartDateTime);

        return new MaterialManagementItemResponse(
                CompanyResponse.CompanySimpleResponse.from(firstMm.getOutsourcingCompany()),
                firstDetail.getName(),
                previousBilling,
                currentBilling);
    }

    /**
     * 전회까지 청구내역 집계 (조회월 이전)
     */
    private BillingDetail aggregatePreviousBilling(
            final List<Map.Entry<MaterialManagement, MaterialManagementDetail>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        long supplyPrice = 0;
        long vat = 0;
        long total = 0;

        for (final Map.Entry<MaterialManagement, MaterialManagementDetail> entry : group) {
            final MaterialManagement mm = entry.getKey();
            final MaterialManagementDetail detail = entry.getValue();

            if (mm.getDeliveryDate().isBefore(currentMonthStartDateTime)) {
                supplyPrice += getValueOrZero(detail.getSupplyPrice());
                vat += getValueOrZero(detail.getVat());
                total += getValueOrZero(detail.getTotal());
            }
        }

        return new BillingDetail(supplyPrice, vat, 0L, total);
    }

    /**
     * 금회 청구내역 집계 (조회월)
     */
    private BillingDetail aggregateCurrentBilling(
            final List<Map.Entry<MaterialManagement, MaterialManagementDetail>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        long supplyPrice = 0;
        long vat = 0;
        long total = 0;

        for (final Map.Entry<MaterialManagement, MaterialManagementDetail> entry : group) {
            final MaterialManagement mm = entry.getKey();
            final MaterialManagementDetail detail = entry.getValue();

            if (!mm.getDeliveryDate().isBefore(currentMonthStartDateTime)) {
                supplyPrice += getValueOrZero(detail.getSupplyPrice());
                vat += getValueOrZero(detail.getVat());
                total += getValueOrZero(detail.getTotal());
            }
        }

        return new BillingDetail(supplyPrice, vat, 0L, total);
    }

    /**
     * 조회월의 시작일(UTC) 계산
     */
    private OffsetDateTime calculateMonthStartDateTime(final String yearMonth) {
        final YearMonth ym = YearMonth.parse(yearMonth);
        final LocalDate monthStart = ym.atDay(1);
        return DateTimeFormatUtils.toUtcStartOfDay(monthStart);
    }

    /**
     * Integer 값을 long으로 변환 (null 처리)
     */
    private long getValueOrZero(final Integer value) {
        return value != null ? value.longValue() : 0L;
    }
}
