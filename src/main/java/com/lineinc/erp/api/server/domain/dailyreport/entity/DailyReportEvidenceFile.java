package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEvidenceFileUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class DailyReportEvidenceFile extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_evidence_file_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 이 증빙파일이 연결된 출역일보 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DAILY_REPORT_ID, nullable = false)
    private DailyReport dailyReport;

    /**
     * 증빙파일 타입 (어떤 탭의 파일인지 구분)
     */
    @Enumerated(EnumType.STRING)
    private DailyReportEvidenceFileType fileType;

    /**
     * 파일명
     */
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    private String fileUrl;

    /**
     * 업로드된 파일의 원본 파일명
     */
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(final DailyReportEvidenceFileUpdateRequest.FileUpdateInfo request) {
        this.name = request.name();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.memo = request.memo();
    }
}
