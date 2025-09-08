package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 출역일보 자동 마감 배치 서비스
 * 매일 자정에 실행되어 전날의 PENDING 상태 출역일보를 AUTO_COMPLETED로 변경합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyReportAutoCompleteBatchService implements BatchService {

    private final DailyReportRepository dailyReportRepository;

    @Override
    public String getBatchName() {
        return "출역일보 자동 마감 배치";
    }

    /**
     * 출역일보 자동 마감 배치를 실행합니다.
     * 전날 날짜의 PENDING 상태 출역일보들을 모두 AUTO_COMPLETED로 변경합니다.
     */
    @Override
    @Transactional
    public void execute() {
        log.info("출역일보 자동 마감 배치 시작");

        try {
            // 전날 날짜 계산 (한국 시간 기준)
            LocalDate yesterday = LocalDate.now(AppConstants.KOREA_ZONE).minusDays(1);
            OffsetDateTime yesterdayStart = DateTimeFormatUtils.toUtcStartOfDay(yesterday);
            OffsetDateTime yesterdayEnd = DateTimeFormatUtils.toUtcEndOfDay(yesterday);

            log.info("자동 마감 대상 날짜: {}", yesterday);

            // 전날의 PENDING 상태 출역일보 조회
            List<DailyReport> pendingReports = dailyReportRepository
                    .findByReportDateBetweenAndStatus(yesterdayStart, yesterdayEnd, DailyReportStatus.PENDING);

            if (pendingReports.isEmpty()) {
                log.info("자동 마감할 출역일보가 없습니다. (날짜: {})", yesterday);
                return;
            }

            log.info("자동 마감 대상 출역일보 {}건 발견", pendingReports.size());

            // 각 출역일보를 자동 마감 처리
            int completedCount = 0;
            for (DailyReport report : pendingReports) {
                try {
                    report.autoComplete();
                    dailyReportRepository.save(report);
                    completedCount++;

                    log.debug("출역일보 자동 마감 완료 - ID: {}, 현장: {}, 공정: {}, 날짜: {}",
                            report.getId(),
                            report.getSite().getName(),
                            report.getSiteProcess().getName(),
                            DateTimeFormatUtils.toKoreaLocalDate(report.getReportDate()));
                } catch (Exception e) {
                    log.error("출역일보 자동 마감 실패 - ID: {}, 오류: {}", report.getId(), e.getMessage(), e);
                }
            }

            log.info("출역일보 자동 마감 배치 완료 - 총 {}건 중 {}건 성공", pendingReports.size(), completedCount);

        } catch (Exception e) {
            log.error("출역일보 자동 마감 배치 실행 중 오류 발생", e);
            throw e;
        }
    }
}
