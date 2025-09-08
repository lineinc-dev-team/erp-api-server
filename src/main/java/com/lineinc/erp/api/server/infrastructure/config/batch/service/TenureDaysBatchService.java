package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;
import com.lineinc.erp.api.server.domain.batch.repository.BatchExecutionHistoryRepository;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

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
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    @Override
    public String getBatchName() {
        return "근속일수 업데이트 배치";
    }

    @Override
    @Transactional
    public void execute() throws Exception {
        // 오늘 이미 실행된 배치가 있는지 확인
        LocalDate today = LocalDate.now(AppConstants.KOREA_ZONE);
        boolean alreadyExecuted = batchExecutionHistoryRepository.existsByBatchNameAndStartTimeBetween(
                getBatchName(),
                DateTimeFormatUtils.toUtcStartOfDay(today),
                DateTimeFormatUtils.toUtcEndOfDay(today));

        if (alreadyExecuted) {
            log.info("근속일수 업데이트 배치가 오늘 이미 실행되었습니다. 중복 실행을 건너뜁니다.");
            return;
        }

        // 배치 실행 이력 생성
        BatchExecutionHistory history = createExecutionHistory();
        batchExecutionHistoryRepository.save(history);

        try {
            updateAllTenureDays();
            history.markAsCompleted();
            log.info("근속일수 업데이트 배치 완료");
        } catch (Exception e) {
            history.markAsFailed(e.getMessage());
            log.error("근속일수 업데이트 배치 실패: {}", e.getMessage(), e);
            throw e;
        } finally {
            batchExecutionHistoryRepository.save(history);
        }
    }

    @Transactional
    public void updateAllTenureDays() {
        log.info("근속일수 업데이트 배치 시작");

        // 한국 시간 기준 현재 시점
        LocalDate now = LocalDate.now(AppConstants.KOREA_ZONE);
        // 45일 전 날짜 (최근 출근 기록 확인용)
        LocalDate fortyFiveDaysAgo = now.minusDays(45);
        // 지난달 1일 (지난달 근로시간 계산 시작점)
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        // 지난달 마지막일 (지난달 근로시간 계산 종료점)
        LocalDate lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        laborRepository.findEligibleLaborsForTenureDaysCalculation(LaborType.DIRECT_CONTRACT, LaborType.ETC).stream()
                .forEach(labor -> updateLaborTenureDays(labor, fortyFiveDaysAgo,
                        lastMonthStart, lastMonthEnd));
    }

    private void updateLaborTenureDays(Labor labor, LocalDate fortyFiveDaysAgo,
            LocalDate lastMonthStart, LocalDate lastMonthEnd) {
        Long newTenureDays = calculateTenureDays(labor, fortyFiveDaysAgo, lastMonthStart, lastMonthEnd);

        if (newTenureDays != null && !newTenureDays.equals(labor.getTenureDays())) {
            laborRepository.updateTenureDays(labor.getId(), newTenureDays);

            // 근속일수가 설정된 기준일 이상이면 퇴직금 발생 예정 (알림용)
            if (newTenureDays >= AppConstants.SEVERANCE_PAY_NOTIFICATION_DAYS
                    && !Boolean.TRUE.equals(labor.getIsSeverancePayEligible())) {
                labor.setIsSeverancePayEligible(true);
                laborRepository.save(labor);
                log.info("인력 {} - 퇴직금 발생 예정 알림 (근속일수: {}일, 6개월 경과)", labor.getName(), newTenureDays);
            }

            log.info("인력 {} - 근속일수: {} → {}", labor.getName(), labor.getTenureDays(),
                    newTenureDays);
        }
    }

    private Long calculateTenureDays(Labor labor, LocalDate fortyFiveDaysAgo,
            LocalDate lastMonthStart, LocalDate lastMonthEnd) {

        // 1. 45일 이내 출근 기록 없음 → 퇴사일 지정 및 근속일수 0
        if (!dailyReportRepository.hasWorkRecordSince(labor.getId(),
                DateTimeFormatUtils.toUtcStartOfDay(fortyFiveDaysAgo))) {

            // 퇴사일이 없는 경우에만 설정 (중복 처리 방지)
            if (labor.getResignationDate() == null) {
                labor.setHireDate(null);
                labor.setTenureDays(0L);
                labor.setIsSeverancePayEligible(false);
                labor.setResignationDate(DateTimeFormatUtils.toOffsetDateTime(fortyFiveDaysAgo));
                laborRepository.save(labor);
                log.info("인력 {} - 45일 이내 출근 기록 없음으로 퇴사일 지정: {}",
                        labor.getName(), fortyFiveDaysAgo);
            }
            return 0L;
        }

        // 2. 첫 근무 시작일이 지난달 이후인 경우 → 근속일수 +1 (신규 입사자)
        LocalDate firstWorkDateKorea = DateTimeFormatUtils.toKoreaLocalDate(labor.getFirstWorkDate());
        if (firstWorkDateKorea.isAfter(lastMonthStart)) {
            return labor.getTenureDays() == null ? 1L : labor.getTenureDays() + 1;
        }

        // 3. 지난달 근로시간 60시간(7.5일) 미만 → 0
        Double lastMonthWorkDays = dailyReportRepository.calculateLastMonthWorkHours(
                labor.getId(),
                DateTimeFormatUtils.toUtcStartOfDay(lastMonthStart),
                DateTimeFormatUtils.toUtcEndOfDay(lastMonthEnd));
        if (lastMonthWorkDays == null || lastMonthWorkDays < 7.5) {
            return 0L;
        }

        // 4. 조건 만족 시 근속일수 +1
        return labor.getTenureDays() == null ? 1L : labor.getTenureDays() + 1;
    }
}
