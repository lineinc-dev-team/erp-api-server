package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class Site extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_seq")
    @SequenceGenerator(name = "site_seq", sequenceName = "site_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name; // 현장명

    @Column
    private String address; // 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SiteType type; // 현장 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id")
    private ClientCompany clientCompany; // 발주처

    @Column
    private OffsetDateTime startDate; // 사업 시작일

    @Column
    private OffsetDateTime endDate; // 사업 종료일

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 본사 담당자

    @Column
    private Long contractAmount; // 도급금액

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}