package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportWorkContentUpdateRequest;
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
public class DailyReportWorkContent extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_work_content_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DAILY_REPORT_ID, nullable = false)
    private DailyReport dailyReport; // 출역일보

    @Column(columnDefinition = "TEXT")
    private String workName; // 작업명

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    @Column(columnDefinition = "TEXT")
    private String personnelAndEquipment; // 인원 및 장비

    private Boolean isToday; // 금일 여부 (true: 금일, false: 명일)

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(
            final DailyReportWorkContentUpdateRequest.WorkContentUpdateInfo request) {
        this.workName = request.workName();
        this.content = request.content();
        this.personnelAndEquipment = request.personnelAndEquipment();
        this.isToday = request.isToday();
    }
}
