package com.lineinc.erp.api.server.domain.dailyreport.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lineinc.erp.api.server.shared.constant.AppConstants;

@Entity
@Table(indexes = {
        @Index(columnList = "reportDate"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class DailyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_report_seq")
    @SequenceGenerator(name = "daily_report_seq", sequenceName = "daily_report_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site; // 현장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    private SiteProcess siteProcess; // 공정

    @Column
    private OffsetDateTime reportDate; // 출역일보 일자

    @Enumerated(EnumType.STRING)
    private FuelAggregationWeatherType weather; // 날씨

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DailyReportStatus status = DailyReportStatus.PENDING; // 출역일보 상태

    @Column
    private OffsetDateTime completedAt; // 마감 일시

    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportEmployee> employees = new ArrayList<>(); // 출역일보 직원 목록

    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportDirectContract> directContracts = new ArrayList<>(); // 직영/계약직 출역일보 목록

    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportOutsourcing> outsourcings = new ArrayList<>(); // 외주 출역일보 목록

    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportOutsourcingEquipment> outsourcingEquipments = new ArrayList<>(); // 외주업체계약 장비 출역일보 목록

    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportFuel> fuels = new ArrayList<>(); // 유류 출역일보 목록

    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportFile> files = new ArrayList<>(); // 현장 사진 등록 목록

    /**
     * 출역일보를 수동 마감 처리합니다.
     */
    public void complete() {
        this.status = DailyReportStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now(AppConstants.KOREA_ZONE);
    }

    /**
     * 출역일보를 자동 마감 처리합니다.
     */
    public void autoComplete() {
        this.status = DailyReportStatus.AUTO_COMPLETED;
        this.completedAt = OffsetDateTime.now(AppConstants.KOREA_ZONE);
    }
}
