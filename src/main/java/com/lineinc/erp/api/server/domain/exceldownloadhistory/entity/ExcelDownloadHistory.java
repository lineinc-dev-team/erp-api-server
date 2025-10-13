package com.lineinc.erp.api.server.domain.exceldownloadhistory.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 엑셀 다운로드 이력 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(indexes = {
        @Index(columnList = "download_type"),
        @Index(columnList = "created_at")
})
public class ExcelDownloadHistory extends BaseEntity {
    private static final String SEQUENCE_NAME = "excel_download_history_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 다운로드 타입
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExcelDownloadHistoryType downloadType;

    /**
     * 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.USER_ID)
    private User user;

    /**
     * 파일 URL
     */
    private String fileUrl;
}
