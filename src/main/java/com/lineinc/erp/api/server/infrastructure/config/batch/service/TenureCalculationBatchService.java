package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 근속기간 계산 배치 서비스
 * 매월 2일에 실행되어 전월 노무비명세서 데이터를 기반으로 근속기간을 업데이트합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenureCalculationBatchService implements BatchService {

    private final LaborPayrollRepository laborPayrollRepository;

    @Override
    public String getBatchName() {
        return "근속기간 계산 배치";
    }

    @Transactional
    public void execute() {
        // 한국 시간 기준으로 전월 계산 (현재 월에서 1개월 빼기)
        LocalDate lastMonth = LocalDate.now(AppConstants.KOREA_ZONE).minusMonths(1);
        String yearMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        log.info("근속기간 계산 배치 시작 - 대상 월: {}", yearMonth);

        // 전월 노무비명세서 데이터 조회
        List<LaborPayroll> payrolls = laborPayrollRepository.findByYearMonth(yearMonth);

        if (payrolls.isEmpty()) {
            log.info("전월 노무비명세서 데이터가 없습니다. - 대상 월: {}", yearMonth);
            return;
        }

        // 인력별로 총 근무시간 합산
        Map<Long, BigDecimal> laborTotalHours = new HashMap<>();
        Map<Long, Labor> laborMap = new HashMap<>();

        for (LaborPayroll payroll : payrolls) {
            if (payroll.getLabor() == null) {
                continue;
            }

            Long laborId = payroll.getLabor().getId();
            Labor labor = payroll.getLabor();

            // 인력 정보 저장
            laborMap.put(laborId, labor);

            // 총 근무시간 합산
            if (payroll.getTotalWorkHours() != null) {
                BigDecimal currentHours = laborTotalHours.getOrDefault(laborId, BigDecimal.ZERO);
                laborTotalHours.put(laborId, currentHours.add(payroll.getTotalWorkHours()));
            }
        }

        int processedCount = 0;
        int tenureIncreasedCount = 0;
        int tenureResetCount = 0;
        int severancePayEnabledCount = 0;

        // 각 인력별로 근속기간 처리
        for (Map.Entry<Long, Labor> entry : laborMap.entrySet()) {
            Long laborId = entry.getKey();
            Labor labor = entry.getValue();
            BigDecimal totalHours = laborTotalHours.get(laborId);

            boolean isProcessed = processLaborTenure(labor, totalHours);

            if (isProcessed) {
                processedCount++;

                // 근속기간 증가/초기화 통계 (총 근무시간 기준)
                if (totalHours != null && totalHours.compareTo(new BigDecimal("7.5")) >= 0) {
                    tenureIncreasedCount++;
                } else {
                    tenureResetCount++;
                }

                // 퇴직금 발생 여부 확인
                if (labor.getTenureMonths() != null && labor.getTenureMonths() >= 6) {
                    severancePayEnabledCount++;
                }
            }
        }

        log.info("근속기간 계산 배치 완료 - 처리된 인력: {}명, 근속기간 증가: {}명, 근속기간 초기화: {}명, 퇴직금 발생 대상: {}명",
                processedCount, tenureIncreasedCount, tenureResetCount, severancePayEnabledCount);
    }

    /**
     * 개별 인력의 근속기간을 처리합니다.
     * 
     * @param labor      인력 정보
     * @param totalHours 해당 인력의 전월 총 근무시간 (모든 현장/공정 합산)
     * @return 처리 여부
     */
    private boolean processLaborTenure(Labor labor, BigDecimal totalHours) {
        try {
            // 총 근무시간 확인
            if (totalHours == null) {
                log.warn("인력 ID {}의 총 근무시간이 null입니다.", labor.getId());
                return false;
            }

            // 7.5시간 이상인 경우 근속기간 증가
            if (totalHours.compareTo(new BigDecimal("7.5")) >= 0) {
                int currentTenureMonths = labor.getTenureMonths() != null ? labor.getTenureMonths() : 0;
                labor.setTenureMonths(currentTenureMonths + 1);

                log.debug("인력 ID {} 근속기간 증가: {}개월 -> {}개월 (전월 총 근무시간: {}시간)",
                        labor.getId(), currentTenureMonths, labor.getTenureMonths(), totalHours);
            } else {
                // 7.5시간 미만인 경우 근속기간 초기화 및 퇴직금 발생 여부 false로 설정
                labor.setTenureMonths(0);
                labor.setIsSeverancePayEligible(false);

                log.debug("인력 ID {} 근속기간 초기화 및 퇴직금 발생 여부 false로 설정 (전월 총 근무시간: {}시간)",
                        labor.getId(), totalHours);
            }

            // 근속기간이 6개월 이상이면 퇴직금 발생 여부 true로 설정
            if (labor.getTenureMonths() != null && labor.getTenureMonths() >= 6) {
                labor.setIsSeverancePayEligible(true);
                log.debug("인력 ID {} 퇴직금 발생 여부 true로 설정 (근속기간: {}개월)",
                        labor.getId(), labor.getTenureMonths());
            }

            return true;
        } catch (Exception e) {
            log.error("인력 ID {} 근속기간 처리 중 오류 발생", labor.getId(), e);
            return false;
        }
    }
}
