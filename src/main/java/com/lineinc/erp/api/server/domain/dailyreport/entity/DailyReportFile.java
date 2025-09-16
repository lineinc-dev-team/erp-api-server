package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;

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

import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileUpdateRequest.FileUpdateInfo;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class DailyReportFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_report_file_seq")
    @SequenceGenerator(name = "daily_report_file_seq", sequenceName = "daily_report_file_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 사진이 연결된 출역일보 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_id", nullable = false)
    @DiffIgnore
    private DailyReport dailyReport;

    /**
     * S3 또는 외부 스토리지에 저장된 사진의 URL
     */
    @Column
    @DiffIgnore
    private String fileUrl;

    /**
     * 업로드된 사진의 원본 파일명
     */
    @Column
    @DiffInclude
    private String originalFileName;

    /**
     * 사진에 대한 설명
     */
    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String description;

    /**
     * 사진에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(FileUpdateInfo request) {
        Optional.ofNullable(request.fileUrl()).ifPresent(val -> this.fileUrl = val);
        Optional.ofNullable(request.originalFileName()).ifPresent(val -> this.originalFileName = val);
        Optional.ofNullable(request.description()).ifPresent(val -> this.description = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}
