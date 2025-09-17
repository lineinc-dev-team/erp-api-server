package com.lineinc.erp.api.server.domain.client.entity;

import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyContactUpdateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = { @Index(columnList = "name"), @Index(columnList = "department"), @Index(columnList = "position"),
        @Index(columnList = "phoneNumber"), @Index(columnList = "email") })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class ClientCompanyContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_contact_seq")
    @SequenceGenerator(name = "client_company_contact_seq", sequenceName = "client_company_contact_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 담당자가 속한 발주처 엔티티
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany;

    /**
     * 담당자의 이름
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * 담당자의 부서
     */
    @DiffInclude
    @Column
    private String department;

    /**
     * 담당자의 직급
     */
    @DiffInclude
    @Column
    private String position;

    @DiffInclude
    @Column
    private String landlineNumber;

    @DiffInclude
    @Column
    private String phoneNumber;

    @DiffInclude
    @Column
    private String email;

    /**
     * 담당자에 대한 비고 또는 추가 메모
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 대표 담당자인지 여부
     */
    @DiffInclude
    @Column
    @Builder.Default
    private Boolean isMain = false;

    public void updateFrom(final ClientCompanyContactUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.position()).ifPresent(val -> this.position = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.isMain()).ifPresent(val -> this.isMain = val);
    }

}
