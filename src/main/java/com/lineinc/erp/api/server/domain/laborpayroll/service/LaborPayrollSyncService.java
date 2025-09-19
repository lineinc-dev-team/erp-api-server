package com.lineinc.erp.api.server.domain.laborpayroll.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollSummaryRepository;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final DailyReportRepository dailyReportRepository;

    /**
     * 출역일보 변경 시 노무비 명세서 동기화
     * 해당 현장/공정의 해당 월 데이터를 재생성
     */
    public void syncLaborPayrollFromDailyReport(DailyReport dailyReport) {
        LocalDate reportDate = DateTimeFormatUtils.toKoreaLocalDate(dailyReport.getReportDate());
        String yearMonth = String.format("%04d-%02d", reportDate.getYear(), reportDate.getMonthValue());

        log.info("현장/공정({}/{})의 {}월 노무비 명세서 재생성 시작",
                dailyReport.getSite().getName(), dailyReport.getSiteProcess().getName(), yearMonth);

        // 1. 기존 노무비 명세서 삭제
        removeExistingPayrolls(dailyReport.getSite(), dailyReport.getSiteProcess(), yearMonth);

        // 2. 해당 월 출역일보 조회
        List<DailyReport> monthlyReports = findMonthlyDailyReports(dailyReport,
                yearMonth);

        // 3. 노무비 명세서 재생성
        regeneratePayrollsFromReports(monthlyReports, yearMonth);

        // // 4. 집계 테이블 업데이트
        updateSummaryTable(dailyReport.getSite(), dailyReport.getSiteProcess(),
                yearMonth);

        log.info("노무비 명세서 동기화 완료: 출역일보 ID={}", dailyReport.getId());
    }

    /**
     * 기존 노무비 명세서 삭제
     */
    private void removeExistingPayrolls(Site site, SiteProcess siteProcess, String yearMonth) {
        List<LaborPayroll> existingPayrolls = laborPayrollRepository.findBySiteAndSiteProcessAndYearMonth(
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
    private List<DailyReport> findMonthlyDailyReports(DailyReport triggerReport,
            String yearMonth) {
        // 해당 월의 시작일과 끝일 계산
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        OffsetDateTime startDateTime = DateTimeFormatUtils.toUtcStartOfDay(startDate);
        OffsetDateTime endDateTime = DateTimeFormatUtils.toUtcEndOfDay(endDate);

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
    private void regeneratePayrollsFromReports(List<DailyReport> monthlyReports,
            String yearMonth) {
        // 인력별, 단가별로 데이터 수집 (동일 인력이라도 단가가 다르면 별도 행으로 분리)
        Map<String, LaborPayrollData> laborDataMap = new HashMap<>();

        for (DailyReport dailyReport : monthlyReports) {
            LocalDate reportDate = DateTimeFormatUtils.toKoreaLocalDate(dailyReport.getReportDate());
            int dayOfMonth = reportDate.getDayOfMonth();

            // 정직원 처리 (삭제되지 않은 데이터만)
            for (DailyReportEmployee employee : dailyReport.getEmployees()) {
                if (!employee.isDeleted()) {
                    processEmployeeData(laborDataMap, employee, dayOfMonth, yearMonth,
                            dailyReport.getSite(), dailyReport.getSiteProcess());
                }
            }

            // 직영/계약직 처리 (삭제되지 않은 데이터만)
            for (DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                if (!directContract.isDeleted()) {
                    processDirectContractData(laborDataMap, directContract, dayOfMonth, yearMonth,
                            dailyReport.getSite(), dailyReport.getSiteProcess());
                }
            }
        }

        // 수집된 데이터로 노무비 명세서 생성
        for (LaborPayrollData laborData : laborDataMap.values()) {
            createLaborPayroll(laborData);
        }

        log.info("노무비 명세서 {}건 생성 완료: {}", laborDataMap.size(), yearMonth);
    }

    /**
     * 정직원 데이터 처리
     */
    private void processEmployeeData(Map<String, LaborPayrollData> laborDataMap,
            DailyReportEmployee employee, int dayOfMonth, String yearMonth, Site site, SiteProcess siteProcess) {
        processLaborData(laborDataMap, employee.getLabor(), employee.getUnitPrice(),
                employee.getWorkQuantity(), dayOfMonth, yearMonth, site, siteProcess);
    }

    /**
     * 직영/계약직 데이터 처리
     */
    private void processDirectContractData(Map<String, LaborPayrollData> laborDataMap,
            DailyReportDirectContract directContract, int dayOfMonth, String yearMonth, Site site,
            SiteProcess siteProcess) {
        processLaborData(laborDataMap, directContract.getLabor(), directContract.getUnitPrice(),
                directContract.getWorkQuantity(), dayOfMonth, yearMonth, site, siteProcess);
    }

    /**
     * 공통 인력 데이터 처리 로직
     */
    private void processLaborData(Map<String, LaborPayrollData> laborDataMap,
            Labor labor, Long unitPrice, Double workQuantity, int dayOfMonth, String yearMonth,
            Site site, SiteProcess siteProcess) {
        Long laborId = labor.getId();

        // 현장, 공정, 인력ID, 단가 조합으로 키 생성
        String dataKey = generateDataKey(site.getId(), siteProcess.getId(), laborId, unitPrice);

        // 기존 데이터가 있으면 근무시간 추가, 없으면 새로 생성
        LaborPayrollData existingData = laborDataMap.get(dataKey);
        if (existingData != null) {
            laborDataMap.put(dataKey, existingData.addDayHours(dayOfMonth, workQuantity));
        } else {
            LaborPayrollData newData = new LaborPayrollData(labor, yearMonth, unitPrice, site, siteProcess);
            laborDataMap.put(dataKey, newData.addDayHours(dayOfMonth, workQuantity));
        }
    }

    /**
     * 노무비 명세서 생성
     */
    private void createLaborPayroll(LaborPayrollData laborData) {
        LaborPayroll.LaborPayrollBuilder builder = LaborPayroll.builder()
                .labor(laborData.labor())
                .site(laborData.site())
                .siteProcess(laborData.siteProcess())
                .yearMonth(laborData.yearMonth())
                .dailyWage(laborData.dailyWage())
                .memo(null);

        // 일별 근무시간 설정
        for (int day = 1; day <= 31; day++) {
            Double dayHours = laborData.getDayHours(day);
            if (dayHours != null && dayHours > 0.0) {
                builder = setBuilderDayHours(builder, day, dayHours);
            }
        }

        LaborPayroll laborPayroll = builder.build();
        laborPayroll.calculatePayroll(); // 급여 계산
        laborPayrollRepository.save(laborPayroll);
    }

    /**
     * 현장, 공정, 인력ID, 단가 조합으로 데이터 키 생성
     */
    private String generateDataKey(Long siteId, Long siteProcessId, Long laborId, Long unitPrice) {
        return siteId + "_" + siteProcessId + "_" + laborId + "_" + (unitPrice != null ? unitPrice : 0L);
    }

    /**
     * Builder에 일별 근무시간 설정하는 헬퍼 메서드
     * 리플렉션을 사용해서 switch문 대신 동적으로 메서드 호출
     */
    private LaborPayroll.LaborPayrollBuilder setBuilderDayHours(LaborPayroll.LaborPayrollBuilder builder,
            int day, Double hours) {
        if (day < 1 || day > 31) {
            return builder;
        }

        try {
            String methodName = String.format("day%02dHours", day);
            var method = LaborPayroll.LaborPayrollBuilder.class.getMethod(methodName, Double.class);
            return (LaborPayroll.LaborPayrollBuilder) method.invoke(builder, hours);
        } catch (Exception e) {
            log.warn("일별 근무시간 설정 실패: day={}, hours={}", day, hours, e);
            return builder;
        }
    }

    /**
     * 집계 테이블 업데이트
     */
    private void updateSummaryTable(Site site, SiteProcess siteProcess, String yearMonth) {
        List<LaborPayroll> payrolls = findPayrollsForSummary(site, siteProcess, yearMonth);
        var existingSummary = findExistingSummary(site, siteProcess, yearMonth);

        if (payrolls.isEmpty()) {
            deleteSummaryIfExists(existingSummary, site, siteProcess, yearMonth);
            return;
        }

        var calculatedData = calculateSummaryData(payrolls);
        LaborPayrollSummary summary = createOrUpdateSummary(existingSummary, site, siteProcess, yearMonth,
                calculatedData);
        laborPayrollSummaryRepository.save(summary);
    }

    /**
     * 집계용 노무비 명세서 조회
     */
    private List<LaborPayroll> findPayrollsForSummary(Site site, SiteProcess siteProcess, String yearMonth) {
        return laborPayrollRepository.findBySiteAndSiteProcessAndYearMonth(site, siteProcess, yearMonth);
    }

    /**
     * 기존 집계 데이터 조회
     */
    private Optional<LaborPayrollSummary> findExistingSummary(Site site, SiteProcess siteProcess, String yearMonth) {
        return laborPayrollSummaryRepository.findBySiteAndSiteProcessAndYearMonth(site, siteProcess, yearMonth);
    }

    /**
     * 집계 데이터가 없을 때 기존 데이터 삭제
     */
    private void deleteSummaryIfExists(Optional<LaborPayrollSummary> existingSummary, Site site,
            SiteProcess siteProcess, String yearMonth) {
        if (existingSummary.isPresent()) {
            laborPayrollSummaryRepository.delete(existingSummary.get());
            log.info("노무비 명세서가 없어 집계 데이터 삭제: 현장={}, 공정={}, 년월={}",
                    site.getName(), siteProcess.getName(), yearMonth);
        }
    }

    /**
     * 집계 데이터 생성 또는 업데이트
     */
    private LaborPayrollSummary createOrUpdateSummary(Optional<LaborPayrollSummary> existingSummary, Site site,
            SiteProcess siteProcess,
            String yearMonth, SummaryData calculatedData) {
        if (existingSummary.isPresent()) {
            return updateExistingSummary(existingSummary.get(), calculatedData, site, siteProcess, yearMonth);
        } else {
            return createNewSummary(site, siteProcess, yearMonth, calculatedData);
        }
    }

    /**
     * 기존 집계 데이터 업데이트
     */
    private LaborPayrollSummary updateExistingSummary(LaborPayrollSummary summary, SummaryData calculatedData,
            Site site, SiteProcess siteProcess, String yearMonth) {
        summary.updateSummary(
                calculatedData.regularEmployeeCount(),
                calculatedData.directContractCount(),
                calculatedData.etcCount(),
                calculatedData.totalLaborCost(),
                calculatedData.totalDeductions(),
                calculatedData.totalNetPayment(),
                null);
        log.info("집계 테이블 업데이트: 현장={}, 공정={}, 년월={}",
                site.getName(), siteProcess.getName(), yearMonth);
        return summary;
    }

    /**
     * 새 집계 데이터 생성
     */
    private LaborPayrollSummary createNewSummary(Site site, SiteProcess siteProcess, String yearMonth,
            SummaryData calculatedData) {
        LaborPayrollSummary summary = LaborPayrollSummary.builder()
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
    private SummaryData calculateSummaryData(List<LaborPayroll> payrolls) {
        // 인력 타입별 고유 인력 수 집계
        Map<LaborType, Long> laborTypeCount = payrolls.stream()
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
        BigDecimal totalLaborCost = payrolls.stream()
                .map(LaborPayroll::getTotalLaborCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDeductions = payrolls.stream()
                .map(LaborPayroll::getTotalDeductions)
                .filter(deductions -> deductions != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNetPayment = payrolls.stream()
                .map(LaborPayroll::getNetPayment)
                .filter(payment -> payment != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 집계 데이터 반환
        Integer regularEmployeeCount = laborTypeCount.getOrDefault(LaborType.REGULAR_EMPLOYEE, 0L).intValue();
        Integer directContractCount = laborTypeCount.getOrDefault(LaborType.DIRECT_CONTRACT, 0L).intValue();
        Integer etcCount = laborTypeCount.getOrDefault(LaborType.ETC, 0L).intValue();

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
        public LaborPayrollData(Labor labor, String yearMonth, Long unitPrice, Site site, SiteProcess siteProcess) {
            this(labor, site, siteProcess, yearMonth,
                    unitPrice != null ? unitPrice.intValue() : 0,
                    new HashMap<>());
        }

        /**
         * 근무시간 추가 (같은 날에 이미 근무시간이 있으면 합산)
         */
        public LaborPayrollData addDayHours(int day, Double hours) {
            Map<Integer, Double> newDailyHours = new HashMap<>(this.dailyHours);
            Double existingHours = newDailyHours.get(day);
            Double newHours = existingHours != null ? existingHours + (hours != null ? hours : 0.0)
                    : (hours != null ? hours : 0.0);
            newDailyHours.put(day, newHours);

            return new LaborPayrollData(labor, site, siteProcess, yearMonth, dailyWage, newDailyHours);
        }

        public Double getDayHours(int day) {
            return dailyHours.get(day);
        }
    }
}
