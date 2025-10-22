package com.lineinc.erp.api.server.domain.laborpayroll.service.v1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollChangeHistory;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollSummaryRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 노무비 명세서 동기화 서비스
 * 출역일보 데이터 변경 시 노무비 명세서를 자동으로 업데이트
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LaborPayrollSyncService {

    private final LaborPayrollRepository laborPayrollRepository;
    private final LaborPayrollSummaryRepository laborPayrollSummaryRepository;
    private final LaborPayrollChangeHistoryRepository laborPayrollChangeHistoryRepository;
    private final DailyReportRepository dailyReportRepository;

    /**
     * 출역일보 변경 시 노무비 명세서 동기화
     * 해당 현장/공정의 해당 월 데이터를 재생성
     */
    public void syncLaborPayrollFromDailyReport(final DailyReport dailyReport) {
        final LocalDate reportDate = DateTimeFormatUtils.toKoreaLocalDate(dailyReport.getReportDate());
        final String yearMonth = String.format("%04d-%02d", reportDate.getYear(), reportDate.getMonthValue());

        log.info("현장/공정({}/{})의 {}월 노무비 명세서 재생성 시작",
                dailyReport.getSite().getName(), dailyReport.getSiteProcess().getName(), yearMonth);

        // 1. 기존 노무비 명세서 삭제
        removeExistingPayrolls(dailyReport.getSite(), dailyReport.getSiteProcess(), yearMonth);

        // 2. 해당 월 출역일보 조회
        final List<DailyReport> monthlyReports = findMonthlyDailyReports(dailyReport,
                yearMonth);

        // 3. 노무비 명세서 재생성
        regeneratePayrollsFromReports(monthlyReports, yearMonth);

        // 4. 집계 테이블 재생성
        regenerateSummaryTable(dailyReport.getSite(), dailyReport.getSiteProcess(),
                yearMonth);

        log.info("노무비 명세서 동기화 완료: 출역일보 ID={}", dailyReport.getId());
    }

    /**
     * 기존 노무비 명세서 삭제
     */
    private void removeExistingPayrolls(final Site site, final SiteProcess siteProcess, final String yearMonth) {
        final List<LaborPayroll> existingPayrolls = laborPayrollRepository.findBySiteAndSiteProcessAndYearMonth(
                site, siteProcess, yearMonth);
        if (!existingPayrolls.isEmpty()) {
            laborPayrollRepository.deleteAll(existingPayrolls);
            log.info("기존 노무비 명세서 {}건 삭제: 현장={}, 공정={}, 년월={}",
                    existingPayrolls.size(), site.getName(), siteProcess.getName(), yearMonth);
        }
    }

    /**
     * 해당 월의 출역일보 조회
     */
    private List<DailyReport> findMonthlyDailyReports(final DailyReport triggerReport,
            final String yearMonth) {
        // 해당 월의 시작일과 끝일 계산
        final String[] parts = yearMonth.split("-");
        final int year = Integer.parseInt(parts[0]);
        final int month = Integer.parseInt(parts[1]);
        final LocalDate startDate = LocalDate.of(year, month, 1);
        final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        final OffsetDateTime startDateTime = DateTimeFormatUtils.toUtcStartOfDay(startDate);
        final OffsetDateTime endDateTime = DateTimeFormatUtils.toUtcEndOfDay(endDate);

        // 같은 현장, 공정의 해당 월 출역일보 모두 조회
        return dailyReportRepository.findBySiteAndSiteProcessAndReportDateBetween(
                triggerReport.getSite(),
                triggerReport.getSiteProcess(),
                startDateTime,
                endDateTime);
    }

    /**
     * 출역일보로부터 노무비 명세서 재생성
     */
    private void regeneratePayrollsFromReports(final List<DailyReport> monthlyReports,
            final String yearMonth) {
        // 인력별, 단가별로 데이터 수집 (동일 인력이라도 단가가 다르면 별도 행으로 분리)
        final Map<String, LaborPayrollData> laborDataMap = new HashMap<>();

        for (final DailyReport dailyReport : monthlyReports) {
            final LocalDate reportDate = DateTimeFormatUtils.toKoreaLocalDate(dailyReport.getReportDate());
            final int dayOfMonth = reportDate.getDayOfMonth();

            // 정직원 처리 (삭제되지 않은 데이터만)
            for (final DailyReportEmployee employee : dailyReport.getEmployees()) {
                if (!employee.isDeleted()) {
                    processEmployeeData(laborDataMap, employee, dayOfMonth, yearMonth,
                            dailyReport.getSite(), dailyReport.getSiteProcess());
                }
            }

            // 직영/용역 처리 (삭제되지 않은 데이터만)
            for (final DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                if (!directContract.isDeleted()) {
                    processDirectContractData(laborDataMap, directContract, dayOfMonth, yearMonth,
                            dailyReport.getSite(), dailyReport.getSiteProcess());
                }
            }
        }

        // 수집된 데이터로 노무비 명세서 생성
        for (final LaborPayrollData laborData : laborDataMap.values()) {
            createLaborPayroll(laborData);
        }

        log.info("노무비 명세서 {}건 생성 완료: {}", laborDataMap.size(), yearMonth);
    }

    /**
     * 정직원 데이터 처리
     */
    private void processEmployeeData(final Map<String, LaborPayrollData> laborDataMap,
            final DailyReportEmployee employee, final int dayOfMonth, final String yearMonth, final Site site,
            final SiteProcess siteProcess) {
        processLaborData(laborDataMap, employee.getLabor(), employee.getUnitPrice(),
                employee.getWorkQuantity(), dayOfMonth, yearMonth, site, siteProcess);
    }

    /**
     * 직영/용역 데이터 처리
     */
    private void processDirectContractData(final Map<String, LaborPayrollData> laborDataMap,
            final DailyReportDirectContract directContract, final int dayOfMonth, final String yearMonth,
            final Site site,
            final SiteProcess siteProcess) {
        processLaborData(laborDataMap, directContract.getLabor(), directContract.getUnitPrice(),
                directContract.getWorkQuantity(), dayOfMonth, yearMonth, site, siteProcess);
    }

    /**
     * 공통 인력 데이터 처리 로직
     */
    private void processLaborData(final Map<String, LaborPayrollData> laborDataMap,
            final Labor labor, final Long unitPrice, final Double workQuantity, final int dayOfMonth,
            final String yearMonth,
            final Site site, final SiteProcess siteProcess) {
        final Long laborId = labor.getId();

        // 현장, 공정, 인력ID, 단가 조합으로 키 생성
        final String dataKey = generateDataKey(site.getId(), siteProcess.getId(), laborId, unitPrice);

        // 기존 데이터가 있으면 근무시간 추가, 없으면 새로 생성
        final LaborPayrollData existingData = laborDataMap.get(dataKey);
        if (existingData != null) {
            laborDataMap.put(dataKey, existingData.addDayHours(dayOfMonth, workQuantity));
        } else {
            final LaborPayrollData newData = new LaborPayrollData(labor, yearMonth, unitPrice, site, siteProcess);
            laborDataMap.put(dataKey, newData.addDayHours(dayOfMonth, workQuantity));
        }
    }

    /**
     * 노무비 명세서 생성
     */
    private void createLaborPayroll(final LaborPayrollData laborData) {
        LaborPayroll.LaborPayrollBuilder builder = LaborPayroll.builder()
                .labor(laborData.labor())
                .site(laborData.site())
                .siteProcess(laborData.siteProcess())
                .yearMonth(laborData.yearMonth())
                .dailyWage(laborData.dailyWage())
                .memo(null);

        // 일별 근무시간 설정
        for (int day = 1; day <= 31; day++) {
            final Double dayHours = laborData.getDayHours(day);
            if (dayHours != null && dayHours > 0.0) {
                builder = setBuilderDayHours(builder, day, dayHours);
            }
        }

        final LaborPayroll laborPayroll = builder.build();
        laborPayroll.calculatePayroll(); // 급여 계산
        laborPayrollRepository.save(laborPayroll);
    }

    /**
     * 현장, 공정, 인력ID, 단가 조합으로 데이터 키 생성
     */
    private String generateDataKey(final Long siteId, final Long siteProcessId, final Long laborId,
            final Long unitPrice) {
        return siteId + "_" + siteProcessId + "_" + laborId + "_" + (unitPrice != null ? unitPrice : 0L);
    }

    /**
     * Builder에 일별 근무시간 설정하는 헬퍼 메서드
     * 리플렉션을 사용해서 switch문 대신 동적으로 메서드 호출
     */
    private LaborPayroll.LaborPayrollBuilder setBuilderDayHours(final LaborPayroll.LaborPayrollBuilder builder,
            final int day, final Double hours) {
        if (day < 1 || day > 31) {
            return builder;
        }

        try {
            final String methodName = String.format("day%02dHours", day);
            final var method = LaborPayroll.LaborPayrollBuilder.class.getMethod(methodName, Double.class);
            return (LaborPayroll.LaborPayrollBuilder) method.invoke(builder, hours);
        } catch (final Exception e) {
            log.warn("일별 근무시간 설정 실패: day={}, hours={}", day, hours, e);
            return builder;
        }
    }

    /**
     * 집계 테이블 재생성 (기존 데이터 삭제 후 새로 생성)
     */
    private void regenerateSummaryTable(final Site site, final SiteProcess siteProcess, final String yearMonth) {
        // 1. 기존 집계 데이터 삭제
        removeExistingSummary(site, siteProcess, yearMonth);

        // 2. 해당 월 노무비 명세서 조회
        final List<LaborPayroll> payrolls = findPayrollsForSummary(site, siteProcess, yearMonth);

        // 3. 노무비 명세서가 없으면 생성하지 않음
        if (payrolls.isEmpty()) {
            log.info("노무비 명세서가 없어 집계 테이블 생성 건너뜀: 현장={}, 공정={}, 년월={}",
                    site.getName(), siteProcess.getName(), yearMonth);
            return;
        }

        // 4. 집계 데이터 계산 및 새로 생성
        final var calculatedData = calculateSummaryData(payrolls);
        final LaborPayrollSummary summary = createNewSummary(site, siteProcess, yearMonth, calculatedData);
        laborPayrollSummaryRepository.save(summary);
    }

    /**
     * 기존 집계 데이터 삭제
     * 집계 데이터 삭제 전에 관련된 변경 이력을 먼저 삭제
     */
    private void removeExistingSummary(final Site site, final SiteProcess siteProcess, final String yearMonth) {
        final Optional<LaborPayrollSummary> existingSummary = findExistingSummary(site, siteProcess, yearMonth);
        if (existingSummary.isPresent()) {
            final LaborPayrollSummary summary = existingSummary.get();

            // 1. 먼저 관련된 변경 이력 삭제
            removeChangeHistoryForSummary(summary);

            // 2. 집계 데이터 삭제
            laborPayrollSummaryRepository.delete(summary);
            log.info("기존 집계 데이터 삭제: 현장={}, 공정={}, 년월={}",
                    site.getName(), siteProcess.getName(), yearMonth);
        }
    }

    /**
     * 집계 데이터와 관련된 변경 이력 삭제
     */
    private void removeChangeHistoryForSummary(final LaborPayrollSummary summary) {
        final List<LaborPayrollChangeHistory> changeHistories = laborPayrollChangeHistoryRepository
                .findBySummaryId(summary.getId(), Pageable.unpaged()).getContent();

        if (!changeHistories.isEmpty()) {
            laborPayrollChangeHistoryRepository.deleteAll(changeHistories);
            log.info("집계 데이터 관련 변경 이력 {}건 삭제: 집계 ID={}",
                    changeHistories.size(), summary.getId());
        }
    }

    /**
     * 집계용 노무비 명세서 조회
     */
    private List<LaborPayroll> findPayrollsForSummary(final Site site, final SiteProcess siteProcess,
            final String yearMonth) {
        return laborPayrollRepository.findBySiteAndSiteProcessAndYearMonth(site, siteProcess, yearMonth);
    }

    /**
     * 기존 집계 데이터 조회
     */
    private Optional<LaborPayrollSummary> findExistingSummary(final Site site, final SiteProcess siteProcess,
            final String yearMonth) {
        return laborPayrollSummaryRepository.findBySiteAndSiteProcessAndYearMonth(site, siteProcess, yearMonth);
    }

    /**
     * 새 집계 데이터 생성
     */
    private LaborPayrollSummary createNewSummary(final Site site, final SiteProcess siteProcess, final String yearMonth,
            final SummaryData calculatedData) {
        final LaborPayrollSummary summary = LaborPayrollSummary.builder()
                .site(site)
                .siteProcess(siteProcess)
                .yearMonth(yearMonth)
                .regularEmployeeCount(calculatedData.regularEmployeeCount())
                .directContractCount(calculatedData.directContractCount())
                .etcCount(calculatedData.etcCount())
                .totalLaborCost(calculatedData.totalLaborCost())
                .totalDeductions(calculatedData.totalDeductions())
                .totalNetPayment(calculatedData.totalNetPayment())
                .memo(null)
                .build();
        log.info("집계 테이블 생성: 현장={}, 공정={}, 년월={}",
                site.getName(), siteProcess.getName(), yearMonth);
        return summary;
    }

    /**
     * 노무비 명세서 목록으로부터 집계 데이터 계산
     */
    private SummaryData calculateSummaryData(final List<LaborPayroll> payrolls) {
        // 인력 타입별 고유 인력 수 집계
        final Map<LaborType, Long> laborTypeCount = payrolls.stream()
                .collect(Collectors.groupingBy(
                        payroll -> payroll.getLabor().getType(),
                        Collectors.mapping(
                                payroll -> payroll.getLabor().getId(),
                                Collectors.toSet())))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (long) entry.getValue().size()));

        // 금액 집계
        final BigDecimal totalLaborCost = payrolls.stream()
                .map(LaborPayroll::getTotalLaborCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalDeductions = payrolls.stream()
                .map(LaborPayroll::getTotalDeductions)
                .filter(deductions -> deductions != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalNetPayment = payrolls.stream()
                .map(LaborPayroll::getNetPayment)
                .filter(payment -> payment != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 집계 데이터 반환
        final Integer regularEmployeeCount = laborTypeCount.getOrDefault(LaborType.REGULAR_EMPLOYEE, 0L).intValue();
        final Integer directContractCount = laborTypeCount.getOrDefault(LaborType.DIRECT_CONTRACT, 0L).intValue();
        final Integer etcCount = laborTypeCount.getOrDefault(LaborType.ETC, 0L).intValue();

        return new SummaryData(
                regularEmployeeCount,
                directContractCount,
                etcCount,
                totalLaborCost,
                totalDeductions,
                totalNetPayment);
    }

    /**
     * 집계 데이터를 담는 레코드
     */
    private record SummaryData(
            Integer regularEmployeeCount,
            Integer directContractCount,
            Integer etcCount,
            BigDecimal totalLaborCost,
            BigDecimal totalDeductions,
            BigDecimal totalNetPayment) {
    }

    /**
     * 인력별 노무비 데이터를 임시로 저장하는 레코드
     * 불변 객체로 변경하여 더 안전하고 간단하게 만듦
     */
    private record LaborPayrollData(
            Labor labor,
            Site site,
            SiteProcess siteProcess,
            String yearMonth,
            Integer dailyWage,
            Map<Integer, Double> dailyHours) {
        public LaborPayrollData(final Labor labor, final String yearMonth, final Long unitPrice, final Site site,
                final SiteProcess siteProcess) {
            this(labor, site, siteProcess, yearMonth,
                    unitPrice != null ? unitPrice.intValue() : 0,
                    new HashMap<>());
        }

        /**
         * 근무시간 추가 (같은 날에 이미 근무시간이 있으면 합산)
         */
        public LaborPayrollData addDayHours(final int day, final Double hours) {
            final Map<Integer, Double> newDailyHours = new HashMap<>(this.dailyHours);
            final Double existingHours = newDailyHours.get(day);
            final Double newHours = existingHours != null ? existingHours + (hours != null ? hours : 0.0)
                    : (hours != null ? hours : 0.0);
            newDailyHours.put(day, newHours);

            return new LaborPayrollData(labor, site, siteProcess, yearMonth, dailyWage, newDailyHours);
        }

        public Double getDayHours(final int day) {
            return dailyHours.get(day);
        }
    }
}
