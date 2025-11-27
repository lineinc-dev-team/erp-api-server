package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lineinc.erp.api.server.domain.batch.enums.BatchName;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 퇴직금 발생 여부 계산 배치 서비스
 * 매월 2일에 실행되어 최근 6개월 노무비명세서 데이터를 기반으로 퇴직금 발생 여부를 업데이트합니다.
 * 직영, 용역 인력만 계산 대상입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeverancePayEligibilityBatchService implements BatchService {

    private final LaborPayrollRepository laborPayrollRepository;

    // 최근 6개월 연속 근무 기준 시간
    private static final BigDecimal MINIMUM_MONTHLY_HOURS = new BigDecimal("7.5");
    private static final int REQUIRED_MONTHS = 3;

    @Override
    public BatchName getBatchName() {
        return BatchName.SEVERANCE_PAY_ELIGIBILITY_CALCULATION;
    }

    @Override
    @Transactional
    public void execute() {
        // 한국 시간 기준으로 최근 6개월 범위 계산
        final LocalDate now = LocalDate.now(AppConstants.KOREA_ZONE);
        final LocalDate endMonth = now.minusMonths(1); // 전월
        final LocalDate startMonth = endMonth.minusMonths(REQUIRED_MONTHS - 1); // 6개월 전

        final String startYearMonth = startMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        final String endYearMonth = endMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        log.info("퇴직금 발생 여부 계산 배치 시작 - 대상 기간: {} ~ {}", startYearMonth, endYearMonth);

        // 최근 6개월 노무비명세서 데이터 조회
        final List<LaborPayroll> payrolls = laborPayrollRepository.findByYearMonth(endYearMonth);

        if (payrolls.isEmpty()) {
            log.info("전월 노무비명세서 데이터가 없습니다. - 대상 월: {}", endYearMonth);
            return;
        }

        // 전월에 근무한 직영/용역 인력 목록 추출
        final Set<Long> targetLaborIds = new HashSet<>();
        final Map<Long, Labor> laborMap = new HashMap<>();

        for (final LaborPayroll payroll : payrolls) {
            if (payroll.getLabor() == null) {
                continue;
            }

            final Labor labor = payroll.getLabor();

            // 직영, 용역만 계산 대상
            if (labor.getType() != LaborType.DIRECT_CONTRACT && labor.getType() != LaborType.OUTSOURCING) {
                continue;
            }

            targetLaborIds.add(labor.getId());
            laborMap.put(labor.getId(), labor);
        }

        log.info("전월 근무 인력 수: {}명 (직영/용역)", targetLaborIds.size());

        // 통계
        int processedCount = 0;
        int severancePayEnabledCount = 0;
        int severancePayDisabledCount = 0;

        // 각 인력별로 최근 6개월 근무 이력 체크
        for (final Long laborId : targetLaborIds) {
            final Labor labor = laborMap.get(laborId);
            final boolean isSeverancePayEligible = checkSixMonthsEligibility(laborId, startYearMonth, endYearMonth);

            // 퇴직금 발생 여부 업데이트
            final boolean previousStatus =
                    labor.getIsSeverancePayEligible() != null && labor.getIsSeverancePayEligible();
            labor.setIsSeverancePayEligible(isSeverancePayEligible);

            processedCount++;

            if (isSeverancePayEligible) {
                severancePayEnabledCount++;
                if (!previousStatus) {
                    log.debug("인력 ID {} 퇴직금 발생 여부 true로 설정 (최근 6개월 조건 충족)", laborId);
                }
            } else {
                severancePayDisabledCount++;
                if (previousStatus) {
                    log.debug("인력 ID {} 퇴직금 발생 여부 false로 설정 (최근 6개월 조건 미충족)", laborId);
                }
            }
        }

        log.info("퇴직금 발생 여부 계산 배치 완료 - 처리된 인력: {}명, 퇴직금 발생: {}명, 퇴직금 미발생: {}명", processedCount,
                severancePayEnabledCount, severancePayDisabledCount);
    }

    /**
     * 최근 6개월 동안 매월 7.5시간 이상 근무했는지 확인합니다.
     *
     * @param laborId        인력 ID
     * @param startYearMonth 시작 년월 (yyyy-MM)
     * @param endYearMonth   종료 년월 (yyyy-MM)
     * @return 6개월 연속 조건 충족 여부
     */
    private boolean checkSixMonthsEligibility(final Long laborId, final String startYearMonth,
            final String endYearMonth) {
        try {
            // 최근 6개월 데이터 조회
            final List<LaborPayroll> payrolls =
                    laborPayrollRepository.findByLaborIdAndYearMonthBetween(laborId, startYearMonth, endYearMonth);

            // 월별 총 근무시간 집계
            final Map<String, BigDecimal> monthlyHours = new HashMap<>();

            for (final LaborPayroll payroll : payrolls) {
                final String yearMonth = payroll.getYearMonth();
                final BigDecimal totalHours =
                        payroll.getTotalWorkHours() != null ? payroll.getTotalWorkHours() : BigDecimal.ZERO;

                monthlyHours.merge(yearMonth, totalHours, BigDecimal::add);
            }

            // 6개월 연속 체크
            final List<String> requiredMonths = generateMonthList(startYearMonth, endYearMonth);

            if (requiredMonths.size() != REQUIRED_MONTHS) {
                log.warn("인력 ID {} - 월 목록 생성 오류: 예상 {}개월, 실제 {}개월", laborId, REQUIRED_MONTHS, requiredMonths.size());
                return false;
            }

            // 각 월마다 7.5시간 이상 근무했는지 확인
            for (final String month : requiredMonths) {
                final BigDecimal hours = monthlyHours.getOrDefault(month, BigDecimal.ZERO);

                if (hours.compareTo(MINIMUM_MONTHLY_HOURS) < 0) {
                    log.debug("인력 ID {} - {}월 근무시간 부족: {}시간 (최소 {}시간 필요)", laborId, month, hours,
                            MINIMUM_MONTHLY_HOURS);
                    return false;
                }
            }

            log.debug("인력 ID {} - 최근 6개월 조건 충족 (모든 월 {}시간 이상 근무)", laborId, MINIMUM_MONTHLY_HOURS);
            return true;

        } catch (final Exception e) {
            log.error("인력 ID {} 퇴직금 발생 여부 처리 중 오류 발생", laborId, e);
            return false;
        }
    }

    /**
     * 시작 월부터 종료 월까지의 월 목록을 생성합니다.
     *
     * @param startYearMonth 시작 년월 (yyyy-MM)
     * @param endYearMonth   종료 년월 (yyyy-MM)
     * @return 월 목록 (yyyy-MM 형식)
     */
    private List<String> generateMonthList(final String startYearMonth, final String endYearMonth) {
        final List<String> months = new ArrayList<>();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDate current = LocalDate.parse(startYearMonth + "-01");
        final LocalDate end = LocalDate.parse(endYearMonth + "-01");

        while (!current.isAfter(end)) {
            months.add(current.format(formatter));
            current = current.plusMonths(1);
        }

        return months;
    }
}
