package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDocumentCreateRequest;
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
public class DailyReportEmployeeFile extends BaseEntity {

    private static final String SEQUENCE_NAME = "daily_report_employee_file_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 이 사진이 연결된 출역일보 직원 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_employee_id", nullable = false)
    @DiffIgnore
    private DailyReportEmployee dailyReportEmployee;

    @Column
    @DiffInclude
    private String name;

    @Column
    @DiffIgnore
    private String fileUrl;

    @Column
    @DiffInclude
    private String originalFileName;

    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final DailyReportDocumentCreateRequest request) {
        this.name = request.name();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.memo = request.memo();
    }
}
