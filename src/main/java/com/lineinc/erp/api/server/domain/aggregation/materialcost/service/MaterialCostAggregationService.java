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
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementDetailV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
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

        // 강재수불부 응답 생성 (입고탭만, 업체+품명별로 그룹핑하여 전회까지/금회 집계)
        final List<SteelManagementItemResponse> steelManagementResponses = aggregateSteelManagements(
                steelManagements, yearMonth);

        // 유류집계 응답 생성 (업체+품명별로 그룹핑하여 전회까지/금회 집계)
        final List<FuelAggregationItemResponse> fuelAggregationResponses = aggregateFuelAggregations(
                fuelAggregations, yearMonth);

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

    /**
     * Long 값을 long으로 변환 (null 처리)
     */
    private long getValueOrZero(final Long value) {
        return value != null ? value : 0L;
    }

    /**
     * 강재수불부 데이터를 업체+품명별로 그룹핑하여 전회까지/금회 청구내역 집계 (입고탭만)
     * 
     * @param steelManagements 강재수불부 목록
     * @param yearMonth        조회월 (YYYY-MM)
     * @return 집계된 강재수불부 응답 목록
     */
    private List<SteelManagementItemResponse> aggregateSteelManagements(
            final List<SteelManagementV2> steelManagements,
            final String yearMonth) {

        final OffsetDateTime currentMonthStartDateTime = calculateMonthStartDateTime(yearMonth);

        // 업체+품명별로 그룹핑 (입고탭만, 자사자재 포함)
        final Map<String, List<Map.Entry<SteelManagementV2, SteelManagementDetailV2>>> groupedByCompanyAndItem = steelManagements
                .stream()
                .flatMap(sm -> sm.getDetails().stream()
                        .filter(detail -> detail.getType() == SteelManagementDetailV2Type.INCOMING) // 입고탭만
                        .map(detail -> Map.entry(sm, detail)))
                .collect(Collectors.groupingBy(this::createSteelGroupingKey));

        // 각 그룹별로 전회까지/금회 집계하여 응답 생성
        return groupedByCompanyAndItem.values().stream()
                .map(group -> createSteelManagementItemResponse(group, currentMonthStartDateTime))
                .toList();
    }

    /**
     * 강재수불부 그룹핑 키 생성: 업체ID_품명 (외주업체가 없는 경우 "SELF" 사용)
     */
    private String createSteelGroupingKey(final Map.Entry<SteelManagementV2, SteelManagementDetailV2> entry) {
        final SteelManagementDetailV2 detail = entry.getValue();
        final String companyKey = detail.getOutsourcingCompany() != null
                ? String.valueOf(detail.getOutsourcingCompany().getId())
                : "SELF";
        return companyKey + "_" + detail.getName();
    }

    /**
     * 그룹에서 강재수불부 응답 생성 (전회까지/금회 집계)
     */
    private SteelManagementItemResponse createSteelManagementItemResponse(
            final List<Map.Entry<SteelManagementV2, SteelManagementDetailV2>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        // 그룹의 첫번째 항목에서 업체와 품명 정보 가져오기
        final SteelManagementDetailV2 firstDetail = group.get(0).getValue();

        // 전회까지/금회 청구내역 집계
        final BillingDetail previousBilling = aggregateSteelPreviousBilling(group, currentMonthStartDateTime);
        final BillingDetail currentBilling = aggregateSteelCurrentBilling(group, currentMonthStartDateTime);

        // 외주업체가 있는 경우에만 CompanySimpleResponse 생성
        final CompanyResponse.CompanySimpleResponse companyResponse = firstDetail.getOutsourcingCompany() != null
                ? CompanyResponse.CompanySimpleResponse.from(firstDetail.getOutsourcingCompany())
                : null;

        return new SteelManagementItemResponse(
                companyResponse,
                firstDetail.getName(),
                firstDetail.getCategory() != null ? firstDetail.getCategory().getLabel() : "",
                firstDetail.getCategory() != null ? firstDetail.getCategory().name() : "",
                previousBilling,
                currentBilling);
    }

    /**
     * 강재수불부 전회까지 청구내역 집계 (조회월 이전)
     */
    private BillingDetail aggregateSteelPreviousBilling(
            final List<Map.Entry<SteelManagementV2, SteelManagementDetailV2>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        long supplyPrice = 0;
        long vat = 0;
        long total = 0;

        for (final Map.Entry<SteelManagementV2, SteelManagementDetailV2> entry : group) {
            final SteelManagementDetailV2 detail = entry.getValue();

            // 입고일 기준으로 전회까지 집계
            if (detail.getIncomingDate() != null && detail.getIncomingDate().isBefore(currentMonthStartDateTime)) {
                supplyPrice += getValueOrZero(detail.getAmount()); // 공급가
                vat += getValueOrZero(detail.getVat());
                total += getValueOrZero(detail.getTotal());
            }
        }

        return new BillingDetail(supplyPrice, vat, 0L, total);
    }

    /**
     * 강재수불부 금회 청구내역 집계 (조회월)
     */
    private BillingDetail aggregateSteelCurrentBilling(
            final List<Map.Entry<SteelManagementV2, SteelManagementDetailV2>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        long supplyPrice = 0;
        long vat = 0;

        for (final Map.Entry<SteelManagementV2, SteelManagementDetailV2> entry : group) {
            final SteelManagementDetailV2 detail = entry.getValue();

            // 입고일 기준으로 금회 집계
            if (detail.getIncomingDate() != null && !detail.getIncomingDate().isBefore(currentMonthStartDateTime)) {
                supplyPrice += getValueOrZero(detail.getAmount()); // 공급가
                vat += getValueOrZero(detail.getVat());
            }
        }

        // total은 supplyPrice + vat로 직접 계산 (자사자재의 경우 detail.total이 0일 수 있음)
        final long total = supplyPrice + vat;
        return new BillingDetail(supplyPrice, vat, 0L, total);
    }

    /**
     * 유류집계 데이터를 업체+품명별로 그룹핑하여 전회까지/금회 청구내역 집계
     * 
     * @param fuelAggregations 유류집계 목록
     * @param yearMonth        조회월 (YYYY-MM)
     * @return 집계된 유류집계 응답 목록
     */
    private List<FuelAggregationItemResponse> aggregateFuelAggregations(
            final List<FuelAggregation> fuelAggregations,
            final String yearMonth) {

        final OffsetDateTime currentMonthStartDateTime = calculateMonthStartDateTime(yearMonth);

        // 업체+품명별로 그룹핑
        final Map<String, List<Map.Entry<FuelAggregation, FuelInfo>>> groupedByCompanyAndItem = fuelAggregations
                .stream()
                .flatMap(fa -> fa.getFuelInfos().stream()
                        .filter(fi -> fi.getOutsourcingCompany() != null) // 업체 정보가 있는 경우만
                        .map(fi -> Map.entry(fa, fi)))
                .collect(Collectors.groupingBy(this::createFuelGroupingKey));

        // 각 그룹별로 전회까지/금회 집계하여 응답 생성
        return groupedByCompanyAndItem.values().stream()
                .map(group -> createFuelAggregationItemResponse(group, currentMonthStartDateTime))
                .toList();
    }

    /**
     * 유류집계 그룹핑 키 생성: 업체ID만 (유종 구분 없이 업체별로 통합)
     */
    private String createFuelGroupingKey(final Map.Entry<FuelAggregation, FuelInfo> entry) {
        final FuelInfo fuelInfo = entry.getValue();
        return fuelInfo.getOutsourcingCompany().getId().toString();
    }

    /**
     * 그룹에서 유류집계 응답 생성 (전회까지/금회 집계)
     */
    private FuelAggregationItemResponse createFuelAggregationItemResponse(
            final List<Map.Entry<FuelAggregation, FuelInfo>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        // 그룹의 첫번째 항목에서 업체와 유종 정보 가져오기
        final FuelInfo firstFuelInfo = group.get(0).getValue();

        // 전회까지/금회 청구내역 집계
        final BillingDetail previousBilling = aggregateFuelPreviousBilling(group, currentMonthStartDateTime);
        final BillingDetail currentBilling = aggregateFuelCurrentBilling(group, currentMonthStartDateTime);

        return new FuelAggregationItemResponse(
                CompanyResponse.CompanySimpleResponse.from(firstFuelInfo.getOutsourcingCompany()),
                "유류대",
                previousBilling,
                currentBilling);
    }

    /**
     * 유류집계 전회까지 청구내역 집계 (조회월 이전)
     */
    private BillingDetail aggregateFuelPreviousBilling(
            final List<Map.Entry<FuelAggregation, FuelInfo>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        long supplyPrice = 0;

        for (final Map.Entry<FuelAggregation, FuelInfo> entry : group) {
            final FuelAggregation fa = entry.getKey();
            final FuelInfo fi = entry.getValue();

            // date 기준으로 전회까지 집계
            if (fa.getDate() != null && fa.getDate().isBefore(currentMonthStartDateTime)) {
                supplyPrice += calculateFuelCost(fa, fi);
            }
        }

        // 부가세 10% 계산
        final long vat = Math.round(supplyPrice * 0.1);
        final long total = supplyPrice + vat;
        return new BillingDetail(supplyPrice, vat, 0L, total);
    }

    /**
     * 유류집계 금회 청구내역 집계 (조회월)
     */
    private BillingDetail aggregateFuelCurrentBilling(
            final List<Map.Entry<FuelAggregation, FuelInfo>> group,
            final OffsetDateTime currentMonthStartDateTime) {

        long supplyPrice = 0;

        for (final Map.Entry<FuelAggregation, FuelInfo> entry : group) {
            final FuelAggregation fa = entry.getKey();
            final FuelInfo fi = entry.getValue();

            // date 기준으로 금회 집계
            if (fa.getDate() != null && !fa.getDate().isBefore(currentMonthStartDateTime)) {
                supplyPrice += calculateFuelCost(fa, fi);
            }
        }

        // 부가세 10% 계산
        final long vat = Math.round(supplyPrice * 0.1);
        final long total = supplyPrice + vat;
        return new BillingDetail(supplyPrice, vat, 0L, total);
    }

    /**
     * 유류비 계산: 주유량 * 유종별 가격
     */
    private long calculateFuelCost(final FuelAggregation fa, final FuelInfo fi) {
        if (fi.getFuelAmount() == null || fi.getFuelType() == null) {
            return 0L;
        }

        final long fuelAmount = fi.getFuelAmount();
        final FuelInfoFuelType fuelType = fi.getFuelType();

        return switch (fuelType) {
            case GASOLINE -> getValueOrZero(fa.getGasolinePrice()) * fuelAmount;
            case DIESEL -> getValueOrZero(fa.getDieselPrice()) * fuelAmount;
            case UREA -> getValueOrZero(fa.getUreaPrice()) * fuelAmount;
            default -> 0L;
        };
    }
}
