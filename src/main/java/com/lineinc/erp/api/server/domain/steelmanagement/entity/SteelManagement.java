package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

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
}
