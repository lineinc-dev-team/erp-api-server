package com.lineinc.erp.api.server.domain.exceldownloadhistory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.exceldownloadhistory.entity.ExcelDownloadHistory;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.repository.ExcelDownloadHistoryRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

/**
 * 엑셀 다운로드 이력 서비스
 */
@Service
@RequiredArgsConstructor
public class ExcelDownloadHistoryService {

    private final ExcelDownloadHistoryRepository excelDownloadHistoryRepository;

    /**
     * 엑셀 다운로드 이력 저장
     *
     * @param downloadType 다운로드 타입
     * @param user         사용자
     * @param fileUrl      S3 파일 URL
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordDownload(final ExcelDownloadHistoryType downloadType, final User user, final String fileUrl) {
        final ExcelDownloadHistory history = ExcelDownloadHistory.builder()
                .downloadType(downloadType)
                .user(user)
                .fileUrl(fileUrl)
                .build();

        excelDownloadHistoryRepository.save(history);
    }

    /**
     * 엑셀 다운로드 이력 생성 및 반환
     *
     * @param downloadType 다운로드 타입
     * @param user         사용자
     * @param fileUrl      S3 파일 URL
     * @return 생성된 이력
     */
    @Transactional
    public ExcelDownloadHistory createHistory(final ExcelDownloadHistoryType downloadType, final User user,
            final String fileUrl) {
        final ExcelDownloadHistory history = ExcelDownloadHistory.builder()
                .downloadType(downloadType)
                .user(user)
                .fileUrl(fileUrl)
                .build();

        return excelDownloadHistoryRepository.save(history);
    }
}
