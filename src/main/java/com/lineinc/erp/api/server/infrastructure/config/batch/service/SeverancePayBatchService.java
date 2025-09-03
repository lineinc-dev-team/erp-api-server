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
 * 퇴직금 발생 배치 서비스
 * 퇴직금 발생 요건 확인 및 기준일 갱신 로직을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeverancePayBatchService implements BatchService {

    private final LaborRepository laborRepository;
    private final DailyReportRepository dailyReportRepository;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    @Override
    public String getBatchName() {
        return "퇴직금 발생 배치";
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
            log.info("퇴직금 발생 배치가 오늘 이미 실행되었습니다. 중복 실행을 건너뜁니다.");
            return;
        }

        // 배치 실행 이력 생성
        BatchExecutionHistory history = createExecutionHistory();
        batchExecutionHistoryRepository.save(history);

        try {
            updateAllSeverancePayEligibility();
            history.markAsCompleted();
            log.info("퇴직금 발생 배치 완료");
        } catch (Exception e) {
            history.markAsFailed(e.getMessage());
            log.error("퇴직금 발생 배치 실패: {}", e.getMessage(), e);
            throw e;
        } finally {
            batchExecutionHistoryRepository.save(history);
        }
    }

    @Transactional
    public void updateAllSeverancePayEligibility() {
        log.info("퇴직금 발생 배치 시작");

        // 한국 시간 기준 현재 시점
        LocalDate now = LocalDate.now(AppConstants.KOREA_ZONE);
        // 45일 전 날짜 (최근 출근 기록 확인용)
        LocalDate fortyFiveDaysAgo = now.minusDays(45);
        // 지난달 1일 (지난달 근로시간 계산 시작점)
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        // 지난달 마지막일 (지난달 근로시간 계산 종료점)
        LocalDate lastMonthEnd = now.withDayOfMonth(1).minusDays(1);

        laborRepository.findEligibleLaborsForSeverancePayCalculation(LaborType.DIRECT_CONTRACT, LaborType.ETC).stream()
                .forEach(labor -> updateLaborSeverancePayEligibility(labor, fortyFiveDaysAgo,
                        lastMonthStart, lastMonthEnd));
    }

    private void updateLaborSeverancePayEligibility(Labor labor, LocalDate fortyFiveDaysAgo,
            LocalDate lastMonthStart, LocalDate lastMonthEnd) {

        // 중복 쿼리 방지를 위해 한 번만 조회
        boolean hasRecentWorkRecord = dailyReportRepository.hasWorkRecordSince(labor.getId(),
                DateTimeFormatUtils.toUtcStartOfDay(fortyFiveDaysAgo));

        Double lastMonthWorkHours = dailyReportRepository.calculateLastMonthWorkHours(
                labor.getId(),
                DateTimeFormatUtils.toUtcStartOfDay(lastMonthStart),
                DateTimeFormatUtils.toUtcEndOfDay(lastMonthEnd));

        // 1. 기준일 갱신 조건 확인
        boolean shouldUpdateEligibilityDate = shouldUpdateEligibilityDate(labor, hasRecentWorkRecord,
                lastMonthWorkHours, lastMonthStart);

        // 2. 퇴직금 발생 여부 계산
        Boolean isEligible;
        if (shouldUpdateEligibilityDate) {
            // 기준일이 갱신되면 퇴직금 발생 불가 (새로운 1년 시작)
            isEligible = false;
        } else {
            // 기준일 갱신이 없을 때만 퇴직금 발생 여부 계산
            isEligible = calculateSeverancePayEligibility(labor, hasRecentWorkRecord, lastMonthWorkHours);
        }

        // 3. 업데이트 필요 여부 확인 및 처리
        if (shouldUpdateEligibilityDate || !Boolean.valueOf(isEligible).equals(labor.getIsSeverancePayEligible())) {
            if (shouldUpdateEligibilityDate) {
                // 퇴직금 발생 기준일을 현재 날짜로 갱신
                LocalDate newEligibilityDate = LocalDate.now(AppConstants.KOREA_ZONE);
                labor.setSeverancePayEligibilityDate(DateTimeFormatUtils.toOffsetDateTime(newEligibilityDate));
            }

            // 퇴직금 발생 여부 업데이트
            labor.setIsSeverancePayEligible(isEligible);
            laborRepository.save(labor);

            log.info("인력 {} - 퇴직금 발생 여부: {} → {}, 기준일 갱신: {}",
                    labor.getName(), labor.getIsSeverancePayEligible(), isEligible, shouldUpdateEligibilityDate);
        }
    }

    /**
     * 퇴직금 산정 기준일 갱신 조건 확인
     * - 최근 45일 이내 출근 기록 없음 OR 지난달 근로시간 60시간 미만 OR 신규 입사자
     */
    private boolean shouldUpdateEligibilityDate(Labor labor, boolean hasRecentWorkRecord, Double lastMonthWorkHours,
            LocalDate lastMonthStart) {
        // 1. 최근 45일 이내 출근 기록 없음 → 기준일 갱신
        if (!hasRecentWorkRecord) {
            return true;
        }

        // 2. 지난달 근로시간 60시간(7.5일) 미만 → 기준일 갱신
        if (lastMonthWorkHours == null || lastMonthWorkHours < 7.5) {
            return true;
        }

        // 모든 조건을 만족하면 기준일 갱신하지 않음
        return false;
    }

    /**
     * 퇴직금 발생 여부 계산
     * - 1년 이상 근속 AND 최근 45일 이내 출근 기록 있음 AND 지난달 근로시간 60시간 이상
     */
    private boolean calculateSeverancePayEligibility(Labor labor, boolean hasRecentWorkRecord,
            Double lastMonthWorkHours) {
        // 1. 퇴직금 발생 기준일로부터 1년 이상 경과 확인
        LocalDate eligibilityDateKorea = DateTimeFormatUtils.toKoreaLocalDate(labor.getSeverancePayEligibilityDate());
        LocalDate oneYearFromEligibilityDate = eligibilityDateKorea.plusYears(1);
        LocalDate now = LocalDate.now(AppConstants.KOREA_ZONE);

        if (now.isBefore(oneYearFromEligibilityDate)) {
            // 기준일로부터 1년이 지나지 않았으면 퇴직금 발생 불가
            return false;
        }

        // 2. 최근 45일 이내 출근 기록 있어야 함
        if (!hasRecentWorkRecord) {
            return false;
        }

        // 3. 지난달 근로시간 60시간(7.5일) 이상이어야 함
        if (lastMonthWorkHours == null || lastMonthWorkHours < 7.5) {
            return false;
        }

        // 모든 조건을 만족하면 퇴직금 발생 가능
        return true;
    }
}
