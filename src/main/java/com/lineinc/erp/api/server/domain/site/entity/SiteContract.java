package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
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
public class SiteContract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_contract_seq")
    @SequenceGenerator(name = "site_contract_seq", sequenceName = "site_contract_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;  // 현장

    @Column(nullable = false)
    private String name;  // 계약명

    @Column
    private Long amount;  // 계약금액

    @Column
    private String memo;  // 비고
}