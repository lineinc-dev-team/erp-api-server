package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ManagementCost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_seq")
    @SequenceGenerator(name = "management_cost_seq", sequenceName = "management_cost_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id", nullable = false)
    private SiteProcess siteProcess;

    /**
     * 품목 타입 (예: 전기, 수도 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    /**
     * 품목 설명 (예: 6월 전기요금 등)
     */
    @Column(nullable = false)
    private String itemDescription;

    /**
     * 관리비가 발생(결제)된 날짜
     */
    @Column(nullable = false)
    private OffsetDateTime paymentDate;

    @Column
    private String businessNumber;

    @Column
    private String ceoName;

    @Column
    private String accountNumber;

    @Column
    private String accountHolder;

    @Column
    private String bankName;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "managementCost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostFile> files = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "managementCost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostDetail> details = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String memo;
}