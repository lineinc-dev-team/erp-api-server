package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType;

import java.util.ArrayList;
import java.util.List;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class MaterialManagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_management_seq")
    @SequenceGenerator(name = "material_management_seq", sequenceName = "material_management_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id", nullable = false)
    private SiteProcess siteProcess;

    @Column(nullable = false)
    private MaterialManagementInputType inputType;

    /**
     * 투입구분 상세 설명 (예: 자재 직접 반입, 외주 업체 제공 등)
     */
    @Column
    private String inputTypeDescription;

    /**
     * 납품일자
     */
    @Column(nullable = false)
    private OffsetDateTime deliveryDate;

    /**
     * 비고
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    @OneToMany(mappedBy = "materialManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementDetail> details = new ArrayList<>();

    @OneToMany(mappedBy = "materialManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementFile> files = new ArrayList<>();


    public void updateFrom(MaterialManagementUpdateRequest request) {
        Optional.ofNullable(request.inputType())
                .ifPresent(value -> this.inputType = value);
        Optional.ofNullable(request.inputTypeDescription())
                .ifPresent(value -> this.inputTypeDescription = value);
        Optional.ofNullable(request.deliveryDate())
                .ifPresent(value -> this.deliveryDate = DateTimeFormatUtils.toOffsetDateTime(value));
        Optional.ofNullable(request.memo())
                .ifPresent(value -> this.memo = value);
    }

    public void changeSite(Site site) {
        this.site = site;
    }

    public void changeSiteProcess(SiteProcess siteProcess) {
        this.siteProcess = siteProcess;
    }
}