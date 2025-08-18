package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

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
import jakarta.persistence.Transient;
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
public class FuelAggregation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuel_aggregation_seq")
    @SequenceGenerator(name = "fuel_aggregation_seq", sequenceName = "fuel_aggregation_seq", allocationSize = 1)
    private Long id;

    /**
     * 형장 (사이트)
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    /**
     * 공정 (사이트 프로세스)
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    private SiteProcess siteProcess;

    /**
     * 일자
     */
    @DiffInclude
    @Column(nullable = false)
    private OffsetDateTime date;

    /**
     * 날씨
     */
    @DiffInclude
    @Enumerated(EnumType.STRING)
    @Column
    private WeatherType weather;

    /**
     * 비고
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Transient
    @DiffInclude
    private String siteName;

    @Transient
    @DiffInclude
    private String processName;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
    }

    // public void updateFrom(FuelAggregationUpdateRequest request, Site site,
    // SiteProcess siteProcess) {
    // this.site = site;
    // this.siteProcess = siteProcess;

    // if (request.date() != null) {
    // this.date = request.date();
    // }
    // if (request.weather() != null) {
    // this.weather = request.weather();
    // }
    // if (request.memo() != null) {
    // this.memo = request.memo();
    // }

    // syncTransientFields();
    // }
}
