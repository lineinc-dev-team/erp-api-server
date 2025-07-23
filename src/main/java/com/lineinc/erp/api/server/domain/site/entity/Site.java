package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteUpdateRequest;

import java.util.Optional;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(indexes = {
        @Index(columnList = "city"),
        @Index(columnList = "district")
})
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

    @Column
    private String detailAddress; // 상세 주소

    @Column
    private String city; // 시

    @Column
    private String district; // 구

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SiteType type; // 현장 유형

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id")
    private ClientCompany clientCompany; // 발주처

    @Column
    private OffsetDateTime startedAt; // 사업 시작일

    @Column
    private OffsetDateTime endedAt; // 사업 종료일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 본사 담당자

    @Column
    private Long contractAmount; // 도급금액

    @Builder.Default
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteContract> contracts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteProcess> processes = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    public void updateFrom(SiteUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.address()).ifPresent(val -> this.address = val);
        Optional.ofNullable(request.detailAddress()).ifPresent(val -> this.detailAddress = val);
        Optional.ofNullable(request.city()).ifPresent(val -> this.city = val);
        Optional.ofNullable(request.district()).ifPresent(val -> this.district = val);
        Optional.ofNullable(request.type()).ifPresent(val -> this.type = val);
        Optional.ofNullable(request.startedAt())
                .map(DateTimeFormatUtils::toOffsetDateTime)
                .ifPresent(val -> this.startedAt = val);
        Optional.ofNullable(request.endedAt())
                .map(DateTimeFormatUtils::toOffsetDateTime)
                .ifPresent(val -> this.endedAt = val);
        Optional.ofNullable(request.contractAmount()).ifPresent(val -> this.contractAmount = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    public void changeUser(User user) {
        this.user = user;
    }

    public void changeClientCompany(ClientCompany clientCompany) {
        this.clientCompany = clientCompany;
    }
}