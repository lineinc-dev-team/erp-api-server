package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.lineinc.erp.api.server.domain.batch.enums.BatchName;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.laborpayroll.service.v1.LaborPayrollSyncService;
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
    private final LaborPayrollSyncService laborPayrollSyncService;

    @Override
    public BatchName getBatchName() {
        return BatchName.DAILY_REPORT_AUTO_COMPLETE;
    }

    /**
     * 출역일보 자동 마감 배치를 실행합니다.
     * 오늘 이전 날짜의 모든 PENDING 상태 출역일보들을 AUTO_COMPLETED로 변경합니다.
     */
    @Override
    public void execute() {
        log.info("출역일보 자동 마감 배치 시작");

        try {
            // 오늘 날짜 계산 (한국 시간 기준)
            final LocalDate today = LocalDate.now(AppConstants.KOREA_ZONE);

            // 오늘로부터 2일 전 날짜 계산
            final LocalDate twoDaysAgo = today.minusDays(2);
            final OffsetDateTime cutoffDate = DateTimeFormatUtils.toUtcStartOfDay(twoDaysAgo);

            log.info("자동 마감 대상: {} 이전 날짜의 모든 출역일보 (오늘: {})", twoDaysAgo, today);

            // 오늘로부터 2일 전보다 이전의 모든 PENDING 상태 출역일보 조회
            final List<DailyReport> pendingReports = dailyReportRepository
                    .findByReportDateBeforeAndStatus(cutoffDate, DailyReportStatus.PENDING);

            if (pendingReports.isEmpty()) {
                log.info("자동 마감할 출역일보가 없습니다. (기준일: {})", today);
                return;
            }

            log.info("자동 마감 대상 출역일보 {}건 발견", pendingReports.size());

            // 각 출역일보를 자동 마감 처리
            int completedCount = 0;
            for (final DailyReport report : pendingReports) {
                try {
                    report.autoComplete();
                    final DailyReport savedReport = dailyReportRepository.save(report);
                    completedCount++;

                    // 노무비 명세서 동기화 (자동 마감 시에만 실행)
                    // 배치에서는 userId를 null로 전달 (시스템 자동 처리)
                    try {
                        laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedReport, null);
                    } catch (final Exception syncException) {
                        log.warn("노무비 명세서 동기화 실패 - 출역일보 ID: {}, 오류: {}", savedReport.getId(),
                                syncException.getMessage());
                        // 동기화 실패해도 마감 처리는 완료된 것으로 간주
                    }

                    log.debug("출역일보 자동 마감 완료 - ID: {}, 현장: {}, 공정: {}, 날짜: {}", report.getId(),
                            report.getSite().getName(), report.getSiteProcess().getName(),
                            DateTimeFormatUtils.toKoreaLocalDate(report.getReportDate()));
                } catch (final Exception e) {
                    log.error("출역일보 자동 마감 실패 - ID: {}, 오류: {}", report.getId(), e.getMessage(), e);
                }
            }

            log.info("출역일보 자동 마감 배치 완료 - 총 {}건 중 {}건 성공", pendingReports.size(), completedCount);

        } catch (final Exception e) {
            log.error("출역일보 자동 마감 배치 실행 중 오류 발생", e);
            throw e;
        }
    }
}
