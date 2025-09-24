package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "date"),
        @Index(columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
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
    @DiffIgnore
    @Column
    private OffsetDateTime date;

    /**
     * 날씨
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column
    private FuelAggregationWeatherType weather;

    @Transient
    @DiffInclude
    private String siteName;

    @Transient
    @DiffInclude
    private String processName;

    @Transient
    @DiffInclude
    private String dateFormat;

    @Transient
    @DiffInclude
    private String weatherName;

    /**
     * 유류정보 목록
     */
    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = "fuelAggregation", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<FuelInfo> fuelInfos = new ArrayList<>();

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
        this.dateFormat = this.date != null ? DateTimeFormatUtils.formatKoreaLocalDate(this.getDate()) : null;
        this.weatherName = this.weather != null ? this.weather.getLabel() : null;
    }

    public void updateFrom(final FuelAggregationUpdateRequest request, final Site site, final SiteProcess siteProcess) {
        this.site = site;
        this.siteProcess = siteProcess;

        if (request.date() != null) {
            this.date = DateTimeFormatUtils.toOffsetDateTime(request.date());
        }
        if (request.weather() != null) {
            this.weather = request.weather();
        }
        syncTransientFields();
    }
}
