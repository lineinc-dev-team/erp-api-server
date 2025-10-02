package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileUpdateRequest.FileUpdateInfo;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class DailyReportFile extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_file_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 이 사진이 연결된 출역일보 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DAILY_REPORT_ID, nullable = false)
    private DailyReport dailyReport;

    /**
     * S3 또는 외부 스토리지에 저장된 사진의 URL
     */
    private String fileUrl;

    /**
     * 업로드된 사진의 원본 파일명
     */
    private String originalFileName;

    /**
     * 사진에 대한 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 사진에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final FileUpdateInfo request) {
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.description = request.description();
        this.memo = request.memo();
    }
}
