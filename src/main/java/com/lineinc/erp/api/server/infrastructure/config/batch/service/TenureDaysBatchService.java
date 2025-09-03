package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 근속일수 산정 배치 서비스
 * 근속일수 계산 및 업데이트 로직을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenureDaysBatchService implements BatchService {

    private final LaborRepository laborRepository;
    private final DailyReportRepository dailyReportRepository;

    @Override
    public String getBatchName() {
        return "근속일수 업데이트 배치";
    }

    @Override
    @Transactional
    public int execute() throws Exception {
        return updateAllTenureDays();
    }

    @Transactional
    public int updateAllTenureDays() {
        log.info("근속일수 업데이트 배치 시작");

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
        OffsetDateTime fortyFiveDaysAgo = now.minus(45, ChronoUnit.DAYS);
        OffsetDateTime lastMonthStart = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime lastMonthEnd = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        return laborRepository.findAll().stream()
                .filter(labor -> !labor.isDeleted())
                .mapToInt(labor -> updateLaborTenureDays(labor, fortyFiveDaysAgo, lastMonthStart, lastMonthEnd))
                .sum();
    }

    private int updateLaborTenureDays(Labor labor, OffsetDateTime fortyFiveDaysAgo,
            OffsetDateTime lastMonthStart, OffsetDateTime lastMonthEnd) {
        Long newTenureDays = calculateTenureDays(labor, fortyFiveDaysAgo, lastMonthStart, lastMonthEnd);

        if (newTenureDays != null && !newTenureDays.equals(labor.getTenureDays())) {
            laborRepository.updateTenureDays(labor.getId(), newTenureDays);
            log.info("인력 {} - 근속일수: {} → {}", labor.getName(), labor.getTenureDays(), newTenureDays);
            return 1;
        }
        return 0;
    }

    private Long calculateTenureDays(Labor labor, OffsetDateTime fortyFiveDaysAgo,
            OffsetDateTime lastMonthStart, OffsetDateTime lastMonthEnd) {

        // 45일 이내 출근 기록 없음 → 0
        if (!dailyReportRepository.hasWorkRecordInLast45Days(labor.getId(), fortyFiveDaysAgo)) {
            return 0L;
        }

        // 지난달 근로시간 60시간 미만 → 0
        Double lastMonthWorkHours = dailyReportRepository.calculateLastMonthWorkHours(
                labor.getId(), lastMonthStart, lastMonthEnd);
        if (lastMonthWorkHours == null || lastMonthWorkHours < 60.0) {
            return 0L;
        }

        // 근속일수 +1
        return labor.getTenureDays() == null ? 1L : labor.getTenureDays() + 1;
    }
}
