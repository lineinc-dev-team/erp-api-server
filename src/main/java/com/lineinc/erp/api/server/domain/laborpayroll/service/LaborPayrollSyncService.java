package com.lineinc.erp.api.server.domain.laborpayroll.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    private final DailyReportRepository dailyReportRepository;

    /**
     * 출역일보 생성/수정 시 노무비 명세서 동기화
     * 해당 월의 모든 데이터를 삭제하고 다시 생성
     */
    public void syncLaborPayrollFromDailyReport(DailyReport dailyReport) {
        LocalDate reportDate = DateTimeFormatUtils.toKoreaLocalDate(dailyReport.getReportDate());
        String yearMonth = String.format("%04d-%02d", reportDate.getYear(), reportDate.getMonthValue());

        log.info("해당 월({}) 노무비 명세서 전체 재생성", yearMonth);

        // 1. 해당 월의 기존 노무비 명세서 모두 삭제
        deleteExistingPayrolls(yearMonth);

        // 2. 해당 월의 모든 출역일보 조회
        List<DailyReport> monthlyReports = getDailyReportsForMonth(dailyReport, yearMonth);

        // 3. 인력별로 노무비 명세서 재생성
        rebuildPayrollsForMonth(monthlyReports, yearMonth);

        log.info("노무비 명세서 동기화 완료: 출역일보 ID={}", dailyReport.getId());
    }

    /**
     * 해당 월의 기존 노무비 명세서 모두 삭제
     */
    private void deleteExistingPayrolls(String yearMonth) {
        List<LaborPayroll> existingPayrolls = laborPayrollRepository.findByYearMonth(yearMonth);
        if (!existingPayrolls.isEmpty()) {
            laborPayrollRepository.deleteAll(existingPayrolls);
            log.info("기존 노무비 명세서 {}건 삭제: {}", existingPayrolls.size(), yearMonth);
        }
    }

    /**
     * 해당 월의 모든 출역일보 조회
     */
    private List<DailyReport> getDailyReportsForMonth(DailyReport triggerReport,
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
     * 인력별로 노무비 명세서 재생성
     */
    private void rebuildPayrollsForMonth(List<DailyReport> monthlyReports,
            String yearMonth) {
        // 인력별, 단가별로 데이터 수집 (동일 인력이라도 단가가 다르면 별도 행으로 분리)
        Map<String, LaborPayrollData> laborDataMap = new HashMap<>();

        for (DailyReport dailyReport : monthlyReports) {
            LocalDate reportDate = DateTimeFormatUtils.toKoreaLocalDate(dailyReport.getReportDate());
            int dayOfMonth = reportDate.getDayOfMonth();

            // 정직원 처리
            for (DailyReportEmployee employee : dailyReport.getEmployees()) {
                processEmployeeData(laborDataMap, employee, dayOfMonth, yearMonth);
            }

            // 직영/계약직 처리
            for (DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                processDirectContractData(laborDataMap, directContract, dayOfMonth, yearMonth);
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
            DailyReportEmployee employee, int dayOfMonth, String yearMonth) {
        Long laborId = employee.getLabor().getId();
        Long unitPrice = employee.getUnitPrice(); // 출역일보의 단가 사용

        // 인력ID와 단가 조합으로 키 생성
        String dataKey = generateDataKey(laborId, unitPrice);

        LaborPayrollData laborData = laborDataMap.computeIfAbsent(dataKey,
                _k -> new LaborPayrollData(employee.getLabor(), yearMonth, unitPrice));

        // 근무시간 설정
        Double workHours = employee.getWorkQuantity();
        laborData.setDayHours(dayOfMonth, workHours);
    }

    /**
     * 직영/계약직 데이터 처리
     */
    private void processDirectContractData(Map<String, LaborPayrollData> laborDataMap,
            DailyReportDirectContract directContract, int dayOfMonth, String yearMonth) {
        Long laborId = directContract.getLabor().getId();
        Long unitPrice = directContract.getUnitPrice(); // 출역일보의 단가 사용

        // 인력ID와 단가 조합으로 키 생성
        String dataKey = generateDataKey(laborId, unitPrice);

        LaborPayrollData laborData = laborDataMap.computeIfAbsent(dataKey,
                _k -> new LaborPayrollData(directContract.getLabor(), yearMonth, unitPrice));

        // 근무시간 설정
        Double workHours = directContract.getWorkQuantity();
        laborData.setDayHours(dayOfMonth, workHours);
    }

    /**
     * 노무비 명세서 생성
     */
    private void createLaborPayroll(LaborPayrollData laborData) {
        LaborPayroll.LaborPayrollBuilder builder = LaborPayroll.builder()
                .labor(laborData.getLabor())
                .yearMonth(laborData.getYearMonth())
                .dailyWage(laborData.getDailyWage())
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
     * 인력ID와 단가 조합으로 데이터 키 생성
     */
    private String generateDataKey(Long laborId, Long unitPrice) {
        return laborId + "_" + (unitPrice != null ? unitPrice : 0L);
    }

    /**
     * Builder에 일별 근무시간 설정하는 헬퍼 메서드
     */
    private LaborPayroll.LaborPayrollBuilder setBuilderDayHours(LaborPayroll.LaborPayrollBuilder builder,
            int day, Double hours) {
        return switch (day) {
            case 1 -> builder.day01Hours(hours);
            case 2 -> builder.day02Hours(hours);
            case 3 -> builder.day03Hours(hours);
            case 4 -> builder.day04Hours(hours);
            case 5 -> builder.day05Hours(hours);
            case 6 -> builder.day06Hours(hours);
            case 7 -> builder.day07Hours(hours);
            case 8 -> builder.day08Hours(hours);
            case 9 -> builder.day09Hours(hours);
            case 10 -> builder.day10Hours(hours);
            case 11 -> builder.day11Hours(hours);
            case 12 -> builder.day12Hours(hours);
            case 13 -> builder.day13Hours(hours);
            case 14 -> builder.day14Hours(hours);
            case 15 -> builder.day15Hours(hours);
            case 16 -> builder.day16Hours(hours);
            case 17 -> builder.day17Hours(hours);
            case 18 -> builder.day18Hours(hours);
            case 19 -> builder.day19Hours(hours);
            case 20 -> builder.day20Hours(hours);
            case 21 -> builder.day21Hours(hours);
            case 22 -> builder.day22Hours(hours);
            case 23 -> builder.day23Hours(hours);
            case 24 -> builder.day24Hours(hours);
            case 25 -> builder.day25Hours(hours);
            case 26 -> builder.day26Hours(hours);
            case 27 -> builder.day27Hours(hours);
            case 28 -> builder.day28Hours(hours);
            case 29 -> builder.day29Hours(hours);
            case 30 -> builder.day30Hours(hours);
            case 31 -> builder.day31Hours(hours);
            default -> builder;
        };
    }

    /**
     * 인력별 노무비 데이터를 임시로 저장하는 클래스
     */
    private static class LaborPayrollData {
        private final Labor labor;
        private final String yearMonth;
        private final Integer dailyWage;
        private final Map<Integer, Double> dailyHours = new HashMap<>();

        public LaborPayrollData(Labor labor, String yearMonth, Long unitPrice) {
            this.labor = labor;
            this.yearMonth = yearMonth;
            this.dailyWage = unitPrice != null ? unitPrice.intValue() : 0;
        }

        public void setDayHours(int day, Double hours) {
            dailyHours.put(day, hours);
        }

        public Double getDayHours(int day) {
            return dailyHours.get(day);
        }

        // Getters
        public Labor getLabor() {
            return labor;
        }

        public String getYearMonth() {
            return yearMonth;
        }

        public Integer getDailyWage() {
            return dailyWage;
        }
    }
}
