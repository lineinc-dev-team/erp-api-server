package com.lineinc.erp.api.server.domain.client.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyContactUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
@Getter
@Builder
@AllArgsConstructor
@SQLRestriction("deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "department"),
        @Index(columnList = "position"),
        @Index(columnList = "phoneNumber"),
        @Index(columnList = "email")
})
public class ClientCompanyContact extends BaseEntity {
    private static final String SEQUENCE_NAME = "client_company_contact_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.CLIENT_COMPANY_ID, nullable = false)
    private ClientCompany clientCompany;

    @Column
    @DiffInclude
    private String name;

    @Column
    @DiffInclude
    private String department;

    @Column
    @DiffInclude
    private String position;

    @Column
    @DiffInclude
    private String landlineNumber;

    @Column
    @DiffInclude
    private String phoneNumber;

    @Column
    @DiffInclude
    private String email;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column
    @DiffInclude
    @Builder.Default
    private final Boolean isMain = false;

    public void updateFrom(final ClientCompanyContactUpdateRequest request) {
        this.name = request.name();
        this.department = request.department();
        this.position = request.position();
        this.landlineNumber = request.landlineNumber();
        this.phoneNumber = request.phoneNumber();
        this.email = request.email();
        this.memo = request.memo();
    }

}
