package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.CascadeType;
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
    private static final String SEQUENCE_NAME = "fuel_aggregation_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    private SiteProcess siteProcess;

    private OffsetDateTime date;

    @Enumerated(EnumType.STRING)
    private FuelAggregationWeatherType weather;

    /**
     * 휘발유 가격
     */
    @DiffInclude
    private Long gasolinePrice;

    /**
     * 경유 가격
     */
    @DiffInclude
    private Long dieselPrice;

    /**
     * 요소수 가격
     */
    @DiffInclude
    private Long ureaPrice;

    /**
     * 유류정보 목록
     */
    @Setter
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.FUEL_AGGREGATION_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FuelInfo> fuelInfos = new ArrayList<>();
}
