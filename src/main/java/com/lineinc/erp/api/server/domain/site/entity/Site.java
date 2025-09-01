package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.domain.client.service.CompanyService;
import com.lineinc.erp.api.server.domain.user.service.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteRequest;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import java.util.Optional;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(indexes = {
        @Index(columnList = "city"),
        @Index(columnList = "district"),
        @Index(columnList = "type"),
        @Index(columnList = "started_at"),
        @Index(columnList = "ended_at")
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Site extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_seq")
    @SequenceGenerator(name = "site_seq", sequenceName = "site_seq", allocationSize = 1)
    private Long id;

    @DiffInclude
    @Column(nullable = false)
    private String name; // 현장명

    @DiffInclude
    @Column
    private String address; // 주소

    @DiffInclude
    @Column
    private String detailAddress; // 상세 주소

    @DiffIgnore
    @Column
    private String city; // 시

    @DiffIgnore
    @Column
    private String district; // 구

    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SiteType type; // 현장 유형

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "client_company_id")
    private ClientCompany clientCompany; // 발주처

    @DiffIgnore
    @Column
    private OffsetDateTime startedAt; // 사업 시작일

    @DiffIgnore
    @Column
    private OffsetDateTime endedAt; // 사업 종료일

    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "user_id")
    private User user; // 본사 담당자

    @DiffInclude
    @Column
    private Long contractAmount; // 도급금액

    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteContract> contracts = new ArrayList<>();

    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteProcess> processes = new ArrayList<>();

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    @Transient
    @DiffInclude
    private String userName;

    @Transient
    @DiffInclude
    private String clientCompanyName;

    @Transient
    @DiffInclude
    private String typeName;

    @Transient
    @DiffInclude
    private String startedAtFormat;

    @Transient
    @DiffInclude
    private String endedAtFormat;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.userName = this.user != null ? this.user.getUsername() : null;
        this.clientCompanyName = this.clientCompany != null ? this.clientCompany.getName() : null;
        this.typeName = this.type != null ? this.type.getLabel() : null;
        this.startedAtFormat = this.startedAt != null ? DateTimeFormatUtils.formatKoreaLocalDate(this.startedAt) : null;
        this.endedAtFormat = this.endedAt != null ? DateTimeFormatUtils.formatKoreaLocalDate(this.endedAt) : null;
    }

    public void updateFrom(UpdateSiteRequest request, UserService userService, CompanyService companyService) {
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
        Optional.ofNullable(request.userId())
                .map(userService::getUserByIdOrThrow)
                .ifPresent(this::changeUser);
        Optional.ofNullable(request.clientCompanyId())
                .map(companyService::getClientCompanyByIdOrThrow)
                .ifPresent(this::changeClientCompany);
        syncTransientFields();
    }

    public void changeUser(User user) {
        this.user = user;
    }

    public void changeClientCompany(ClientCompany clientCompany) {
        this.clientCompany = clientCompany;
    }
}