package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SteelManagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steel_management_seq")
    @SequenceGenerator(name = "steel_management_seq", sequenceName = "steel_management_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id", nullable = false)
    private SiteProcess siteProcess;

    @OneToMany(mappedBy = "steelManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteelManagementFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "steelManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteelManagementDetail> details = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Setter
    @Column
    private SteelManagementType type;

    @Column
    private OffsetDateTime paymentDate;

    /**
     * 용도
     */
    @Column
    private String usage;

    /**
     * 비고
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(SteelManagementUpdateRequest request) {
        Optional.ofNullable(request.usage()).ifPresent(val -> this.usage = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.paymentDate())
                .map(DateTimeFormatUtils::toOffsetDateTime)
                .ifPresent(val -> this.paymentDate = val);
    }

    public void changeSite(Site site) {
        this.site = site;
    }

    public void changeSiteProcess(SiteProcess siteProcess) {
        this.siteProcess = siteProcess;
    }
}
