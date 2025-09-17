package com.lineinc.erp.api.server.domain.client.entity;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyPaymentMethod;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyUpdateRequest;

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
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "businessNumber"),
        @Index(columnList = "ceoName"),
        @Index(columnList = "email"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "phoneNumber")
})
public class ClientCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_seq")
    @SequenceGenerator(name = "client_company_seq", sequenceName = "client_company_seq", allocationSize = 1)
    private Long id;

    @Column
    @DiffInclude
    private String name;

    @Column
    @DiffInclude
    private String businessNumber;

    @Column
    @DiffInclude
    private String ceoName;

    @Column
    @DiffInclude
    private String address;

    @Column
    @DiffInclude
    private String detailAddress;

    @Column
    @DiffInclude
    private String landlineNumber;

    @Column
    @DiffInclude
    private String phoneNumber;

    @Column
    @DiffInclude
    private String email;

    @Column
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    private ClientCompanyPaymentMethod paymentMethod;

    @Column
    @DiffIgnore
    private String paymentPeriod;

    @Setter
    @ManyToOne
    @JoinColumn
    @DiffIgnore
    private User user; // 본사 담당자

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ClientCompanyContact> contacts = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ClientCompanyFile> files = new ArrayList<>();

    @Column
    @DiffInclude
    @Builder.Default
    private boolean isActive = true;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Transient
    @DiffInclude
    private String userName;

    @Transient
    @DiffInclude
    private String paymentMethodName;

    public void syncTransientFields() {
        this.userName = this.user != null ? this.user.getUsername() : null;
        this.paymentMethodName = this.paymentMethod != null ? this.paymentMethod.getLabel() : null;
    }

    public void updateFrom(final ClientCompanyUpdateRequest request, final User user) {
        this.name = request.name();
        this.businessNumber = request.businessNumber();
        this.ceoName = request.ceoName();
        this.address = request.address();
        this.detailAddress = request.detailAddress();
        this.landlineNumber = request.landlineNumber();
        this.phoneNumber = request.phoneNumber();
        this.email = request.email();
        this.paymentMethod = request.paymentMethod();
        this.paymentPeriod = request.paymentPeriod();
        this.memo = request.memo();
        this.isActive = request.isActive();
        this.user = user;
        syncTransientFields();
    }

}
