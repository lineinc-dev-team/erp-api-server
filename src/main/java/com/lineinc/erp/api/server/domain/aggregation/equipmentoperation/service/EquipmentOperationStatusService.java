package com.lineinc.erp.api.server.domain.aggregation.equipmentoperation.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipmentSubEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportOutsourcingEquipmentRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContactSubEquipmentType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse.DailyUsage;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse.EquipmentDailyUsage;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse.EquipmentOperationStatusItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse.FuelDailyUsage;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse.FuelUsage;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentOperationStatusResponse.SubEquipmentTypeItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 장비가동현황 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentOperationStatusService {

    private final DailyReportOutsourcingEquipmentRepository equipmentRepository;
    private final FuelAggregationRepository fuelAggregationRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;

    /**
     * 장비가동현황 조회
     *
     * @param siteId        현장 ID
     * @param siteProcessId 공정 ID
     * @param yearMonth     조회월 (YYYY-MM)
     * @return 장비가동현황 응답
     */
    public EquipmentOperationStatusResponse getEquipmentOperationStatus(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {

        final Site site = siteService.getSiteByIdOrThrow(siteId);
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(siteProcessId);

        // 해당 월까지의 외주업체 장비 데이터 조회
        final List<DailyReportOutsourcingEquipment> allEquipments = findEquipmentsUpToMonth(site, siteProcess,
                yearMonth);

        // 해당 월의 유류집계 데이터 조회 후 장비별 일자 사용량 Map 구성 (해당 월만)
        final YearMonth ym = YearMonth.parse(yearMonth);
        final LocalDate startOfMonth = ym.atDay(1);
        final LocalDate nextMonthFirstDay = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startOfMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthFirstDay);
        final List<FuelAggregation> fuelAggregations = fuelAggregationRepository
                .findBySiteAndSiteProcessAndDateGreaterThanEqualAndDateLessThanAndDeletedFalse(
                        site, siteProcess, startInclusive, endExclusive);
        final Map<Long, Map<Integer, Long>> fuelUsageByEquipment = buildFuelUsageByEquipmentForMonth(
                fuelAggregations, YearMonth.parse(yearMonth));

        // 외주업체별로 그룹핑
        final List<EquipmentOperationStatusItem> items = aggregateByOutsourcingCompany(allEquipments, yearMonth,
                fuelUsageByEquipment);

        return new EquipmentOperationStatusResponse(items);
    }

    /**
     * 해당 월까지의 외주업체 장비 데이터 조회
     */
    private List<DailyReportOutsourcingEquipment> findEquipmentsUpToMonth(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth) {

        final YearMonth ym = YearMonth.parse(yearMonth);
        final LocalDate startOfMonth = ym.atDay(1);
        final LocalDate nextMonthFirstDay = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startOfMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthFirstDay);

        return equipmentRepository.findBySiteAndSiteProcessAndReportDateBetweenMonth(
                site.getId(),
                siteProcess.getId(),
                startInclusive,
                endExclusive);
    }

    /**
     * 외주업체 + 규격별로 그룹핑 (1:1 매핑)
     */
    private List<EquipmentOperationStatusItem> aggregateByOutsourcingCompany(
            final List<DailyReportOutsourcingEquipment> allEquipments,
            final String currentYearMonth,
            final Map<Long, Map<Integer, Long>> fuelUsageByEquipment) {

        final YearMonth currentYm = YearMonth.parse(currentYearMonth);

        // 외주업체 ID + 규격별로 그룹핑
        final Map<String, List<DailyReportOutsourcingEquipment>> groupedByCompanyAndSpecification = allEquipments
                .stream()
                .filter(equipment -> equipment.getOutsourcingCompany() != null)
                .filter(equipment -> equipment.getOutsourcingCompanyContractEquipment() != null)
                .collect(Collectors.groupingBy(equipment -> {
                    final Long companyId = equipment.getOutsourcingCompany().getId();
                    final String specification = equipment.getOutsourcingCompanyContractEquipment().getSpecification();
                    return companyId + "_" + (specification != null ? specification : "");
                }));

        return groupedByCompanyAndSpecification.entrySet().stream()
                .map(entry -> createEquipmentOperationStatusItem(entry.getValue(), currentYm, fuelUsageByEquipment))
                .toList();
    }

    /**
     * 외주업체 + 규격별 장비가동현황 항목 생성
     */
    private EquipmentOperationStatusItem createEquipmentOperationStatusItem(
            final List<DailyReportOutsourcingEquipment> equipments,
            final YearMonth currentYm,
            final Map<Long, Map<Integer, Long>> fuelUsageByEquipment) {

        final DailyReportOutsourcingEquipment firstEquipment = equipments.get(0);

        // 외주업체 정보
        final CompanyResponse.CompanySimpleResponse outsourcingCompany = CompanyResponse.CompanySimpleResponse
                .from(firstEquipment.getOutsourcingCompany());

        // 규격
        final String specification = firstEquipment.getOutsourcingCompanyContractEquipment() != null
                ? firstEquipment.getOutsourcingCompanyContractEquipment().getSpecification()
                : "";

        // 기사 정보 (첫 번째 기사 정보 사용, 여러 기사가 있을 수 있음)
        final ContractDriverResponse.ContractDriverSimpleResponse driver = firstEquipment
                .getOutsourcingCompanyContractDriver() != null
                        ? ContractDriverResponse.ContractDriverSimpleResponse
                                .from(firstEquipment.getOutsourcingCompanyContractDriver())
                        : null;

        // 장비 정보 (첫 번째 장비의 type 사용, 장비는 하나이므로)
        final OutsourcingCompanyContractCategoryType equipmentType = equipments.stream()
                .map(equipment -> equipment.getOutsourcingCompanyContractEquipment())
                .filter(contractEquipment -> contractEquipment != null && contractEquipment.getType() != null)
                .map(contractEquipment -> contractEquipment.getType())
                .findFirst()
                .orElse(null);

        final EquipmentDailyUsage equipmentDailyUsage = createEquipmentDailyUsage(equipmentType, equipments, currentYm);

        // 서브장비 구분값별로 그룹핑
        final Map<OutsourcingCompanyContactSubEquipmentType, List<DailyReportOutsourcingEquipmentSubEquipment>> groupedBySubEquipmentType = equipments
                .stream()
                .flatMap(eq -> eq.getSubEquipments().stream())
                .filter(subEquipment -> {
                    final OutsourcingCompanyContractSubEquipment contractSubEquipment = subEquipment
                            .getOutsourcingCompanyContractSubEquipment();
                    return contractSubEquipment != null && contractSubEquipment.getType() != null;
                })
                .collect(Collectors.groupingBy(subEquipment -> subEquipment.getOutsourcingCompanyContractSubEquipment()
                        .getType()));

        final List<SubEquipmentTypeItem> subEquipmentItems = groupedBySubEquipmentType.entrySet().stream()
                .map(entry -> createSubEquipmentTypeItem(entry.getKey(), entry.getValue(), currentYm))
                .toList();

        // 유류 사용량 (해당 장비의 ContractEquipment ID 기준)
        final Long equipmentId = firstEquipment.getOutsourcingCompanyContractEquipment() != null
                ? firstEquipment.getOutsourcingCompanyContractEquipment().getId()
                : null;
        final FuelUsage fuelUsage = createFuelUsage(fuelUsageByEquipment.get(equipmentId));

        return new EquipmentOperationStatusItem(outsourcingCompany, specification, driver, equipmentDailyUsage,
                fuelUsage, subEquipmentItems);
    }

    /**
     * 장비 일별 사용량 생성
     */
    private EquipmentDailyUsage createEquipmentDailyUsage(
            final OutsourcingCompanyContractCategoryType equipmentType,
            final List<DailyReportOutsourcingEquipment> equipments,
            final YearMonth currentYm) {

        // 1~31일별 데이터 초기화
        final Map<Integer, DailyUsage> dailyUsageMap = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            dailyUsageMap.put(day, null);
        }

        // 해당 월의 장비 데이터만 처리
        for (final DailyReportOutsourcingEquipment equipment : equipments) {
            if (equipment.getDailyReport() == null || equipment.getDailyReport().getReportDate() == null) {
                continue;
            }

            final LocalDate reportDate = DateTimeFormatUtils
                    .toKoreaLocalDate(equipment.getDailyReport().getReportDate());

            // 해당 월이 아닌 데이터는 스킵
            if (!(reportDate.getYear() == currentYm.getYear()
                    && reportDate.getMonthValue() == currentYm.getMonthValue())) {
                continue;
            }

            final int day = reportDate.getDayOfMonth();
            final Double workHours = equipment.getWorkHours() != null ? equipment.getWorkHours() : 0.0;
            final Long unitPrice = equipment.getUnitPrice() != null ? equipment.getUnitPrice() : 0L;

            // 같은 날에 데이터가 있으면 시간 합산, 단가는 첫 번째 것으로 사용
            final DailyUsage existing = dailyUsageMap.get(day);
            if (existing != null) {
                final Double totalHours = existing.hours() + workHours;
                dailyUsageMap.put(day, new DailyUsage(totalHours, existing.unitPrice()));
            } else {
                dailyUsageMap.put(day, new DailyUsage(workHours, unitPrice));
            }
        }

        final String typeLabel = equipmentType != null ? equipmentType.getLabel() : null;
        final String typeCode = equipmentType != null ? equipmentType.name() : null;

        return createEquipmentDailyUsageWithDailyUsage(typeLabel, typeCode, dailyUsageMap);
    }

    /**
     * 서브장비 구분값별 항목 생성
     */
    private SubEquipmentTypeItem createSubEquipmentTypeItem(
            final OutsourcingCompanyContactSubEquipmentType subEquipmentType,
            final List<DailyReportOutsourcingEquipmentSubEquipment> subEquipments,
            final YearMonth currentYm) {

        // 1~31일별 데이터 초기화
        final Map<Integer, DailyUsage> dailyUsageMap = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            dailyUsageMap.put(day, null);
        }

        // 해당 월의 서브장비 데이터만 처리
        for (final DailyReportOutsourcingEquipmentSubEquipment subEquipment : subEquipments) {
            final DailyReportOutsourcingEquipment equipment = subEquipment.getDailyReportOutsourcingEquipment();
            if (equipment == null || equipment.getDailyReport() == null
                    || equipment.getDailyReport().getReportDate() == null) {
                continue;
            }

            final LocalDate reportDate = DateTimeFormatUtils
                    .toKoreaLocalDate(equipment.getDailyReport().getReportDate());

            // 해당 월이 아닌 데이터는 스킵
            if (!(reportDate.getYear() == currentYm.getYear()
                    && reportDate.getMonthValue() == currentYm.getMonthValue())) {
                continue;
            }

            final int day = reportDate.getDayOfMonth();
            final Double workHours = subEquipment.getWorkHours() != null ? subEquipment.getWorkHours() : 0.0;
            final Long unitPrice = subEquipment.getUnitPrice() != null ? subEquipment.getUnitPrice() : 0L;

            // 같은 날에 데이터가 있으면 시간 합산, 단가는 첫 번째 것으로 사용
            final DailyUsage existing = dailyUsageMap.get(day);
            if (existing != null) {
                final Double totalHours = existing.hours() + workHours;
                dailyUsageMap.put(day, new DailyUsage(totalHours, existing.unitPrice()));
            } else {
                dailyUsageMap.put(day, new DailyUsage(workHours, unitPrice));
            }
        }

        return createSubEquipmentTypeItemWithDailyUsage(
                subEquipmentType.getLabel(),
                subEquipmentType.name(),
                dailyUsageMap);
    }

    /**
     * 유류 사용량 Map 구성 (장비별, 일자별 합계) - 해당 월만 포함
     */
    private Map<Long, Map<Integer, Long>> buildFuelUsageByEquipmentForMonth(
            final List<FuelAggregation> fuelAggregations,
            final YearMonth ym) {
        final Map<Long, Map<Integer, Long>> result = new HashMap<>();

        for (final FuelAggregation fa : fuelAggregations) {
            if (fa.getDate() == null || fa.getFuelInfos() == null) {
                continue;
            }
            final LocalDate date = DateTimeFormatUtils.toKoreaLocalDate(fa.getDate());
            if (!(date.getYear() == ym.getYear() && date.getMonthValue() == ym.getMonthValue())) {
                continue;
            }

            final int day = date.getDayOfMonth();
            for (final FuelInfo fi : fa.getFuelInfos()) {
                if (fi.getEquipment() == null || fi.getFuelAmount() == null) {
                    continue;
                }
                final Long equipmentId = fi.getEquipment().getId();
                result.computeIfAbsent(equipmentId, k -> new HashMap<>());
                final Map<Integer, Long> daily = result.get(equipmentId);
                daily.put(day, (daily.getOrDefault(day, 0L) + fi.getFuelAmount()));
            }
        }
        return result;
    }

    private FuelUsage createFuelUsage(final Map<Integer, Long> dailyAmountMap) {
        if (dailyAmountMap == null) {
            return new FuelUsage(null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null);
        }

        return new FuelUsage(
                toFuelDaily(dailyAmountMap.get(1)), toFuelDaily(dailyAmountMap.get(2)),
                toFuelDaily(dailyAmountMap.get(3)), toFuelDaily(dailyAmountMap.get(4)),
                toFuelDaily(dailyAmountMap.get(5)), toFuelDaily(dailyAmountMap.get(6)),
                toFuelDaily(dailyAmountMap.get(7)), toFuelDaily(dailyAmountMap.get(8)),
                toFuelDaily(dailyAmountMap.get(9)), toFuelDaily(dailyAmountMap.get(10)),
                toFuelDaily(dailyAmountMap.get(11)), toFuelDaily(dailyAmountMap.get(12)),
                toFuelDaily(dailyAmountMap.get(13)), toFuelDaily(dailyAmountMap.get(14)),
                toFuelDaily(dailyAmountMap.get(15)), toFuelDaily(dailyAmountMap.get(16)),
                toFuelDaily(dailyAmountMap.get(17)), toFuelDaily(dailyAmountMap.get(18)),
                toFuelDaily(dailyAmountMap.get(19)), toFuelDaily(dailyAmountMap.get(20)),
                toFuelDaily(dailyAmountMap.get(21)), toFuelDaily(dailyAmountMap.get(22)),
                toFuelDaily(dailyAmountMap.get(23)), toFuelDaily(dailyAmountMap.get(24)),
                toFuelDaily(dailyAmountMap.get(25)), toFuelDaily(dailyAmountMap.get(26)),
                toFuelDaily(dailyAmountMap.get(27)), toFuelDaily(dailyAmountMap.get(28)),
                toFuelDaily(dailyAmountMap.get(29)), toFuelDaily(dailyAmountMap.get(30)),
                toFuelDaily(dailyAmountMap.get(31)));
    }

    private FuelDailyUsage toFuelDaily(final Long amount) {
        return amount == null ? null : new FuelDailyUsage(amount);
    }

    /**
     * EquipmentDailyUsage 생성 (일별 데이터 포함)
     */
    private EquipmentDailyUsage createEquipmentDailyUsageWithDailyUsage(
            final String type,
            final String typeCode,
            final Map<Integer, DailyUsage> dailyUsageMap) {
        return new EquipmentDailyUsage(
                type,
                typeCode,
                dailyUsageMap.get(1), dailyUsageMap.get(2), dailyUsageMap.get(3), dailyUsageMap.get(4),
                dailyUsageMap.get(5), dailyUsageMap.get(6), dailyUsageMap.get(7), dailyUsageMap.get(8),
                dailyUsageMap.get(9), dailyUsageMap.get(10), dailyUsageMap.get(11), dailyUsageMap.get(12),
                dailyUsageMap.get(13), dailyUsageMap.get(14), dailyUsageMap.get(15), dailyUsageMap.get(16),
                dailyUsageMap.get(17), dailyUsageMap.get(18), dailyUsageMap.get(19), dailyUsageMap.get(20),
                dailyUsageMap.get(21), dailyUsageMap.get(22), dailyUsageMap.get(23), dailyUsageMap.get(24),
                dailyUsageMap.get(25), dailyUsageMap.get(26), dailyUsageMap.get(27), dailyUsageMap.get(28),
                dailyUsageMap.get(29), dailyUsageMap.get(30), dailyUsageMap.get(31));
    }

    /**
     * SubEquipmentTypeItem 생성 (일별 데이터 포함)
     */
    private SubEquipmentTypeItem createSubEquipmentTypeItemWithDailyUsage(
            final String type,
            final String typeCode,
            final Map<Integer, DailyUsage> dailyUsageMap) {
        return new SubEquipmentTypeItem(
                type,
                typeCode,
                dailyUsageMap.get(1), dailyUsageMap.get(2), dailyUsageMap.get(3), dailyUsageMap.get(4),
                dailyUsageMap.get(5), dailyUsageMap.get(6), dailyUsageMap.get(7), dailyUsageMap.get(8),
                dailyUsageMap.get(9), dailyUsageMap.get(10), dailyUsageMap.get(11), dailyUsageMap.get(12),
                dailyUsageMap.get(13), dailyUsageMap.get(14), dailyUsageMap.get(15), dailyUsageMap.get(16),
                dailyUsageMap.get(17), dailyUsageMap.get(18), dailyUsageMap.get(19), dailyUsageMap.get(20),
                dailyUsageMap.get(21), dailyUsageMap.get(22), dailyUsageMap.get(23), dailyUsageMap.get(24),
                dailyUsageMap.get(25), dailyUsageMap.get(26), dailyUsageMap.get(27), dailyUsageMap.get(28),
                dailyUsageMap.get(29), dailyUsageMap.get(30), dailyUsageMap.get(31));
    }
}
