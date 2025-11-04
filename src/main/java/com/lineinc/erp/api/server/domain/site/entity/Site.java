package com.lineinc.erp.api.server.domain.site.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.experimental.SuperBuilder;

@Table(indexes = {
        @Index(columnList = "city"),
        @Index(columnList = "district"),
        @Index(columnList = "type"),
        @Index(columnList = "started_at"),
        @Index(columnList = "ended_at"),
        @Index(columnList = "created_at"),
        @Index(columnList = "created_by"),
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Site extends BaseEntity {
    private static final String SEQUENCE_NAME = "site_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
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
    @Column
    private SiteType type; // 현장 유형

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = AppConstants.CLIENT_COMPANY_ID)
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
    private Long contractAmount; // 계약금액

    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = AppConstants.SITE_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteContract> contracts = new ArrayList<>();

    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = AppConstants.SITE_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void updateFrom(final UpdateSiteRequest request, final User user, final ClientCompany clientCompany) {
        this.name = request.name();
        this.address = request.address();
        this.detailAddress = request.detailAddress();
        this.city = request.city();
        this.district = request.district();
        this.type = request.type();
        this.startedAt = DateTimeFormatUtils.toOffsetDateTime(request.startedAt());
        this.endedAt = DateTimeFormatUtils.toOffsetDateTime(request.endedAt());
        this.contractAmount = request.contractAmount();
        this.memo = request.memo();
        this.user = user;
        this.clientCompany = clientCompany;
        syncTransientFields();
    }
}