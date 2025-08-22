package com.lineinc.erp.api.server.domain.dailyreport.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.shared.enums.WeatherType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
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
    private LocalDate reportDate; // 출역일보 일자

    @Enumerated(EnumType.STRING)
    private WeatherType weather; // 날씨
}
