package com.lineinc.erp.api.server.domain.clientcompany.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyPaymentMethod;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
        @Index(columnList = "ceoName"),
        @Index(columnList = "name"),
        @Index(columnList = "phoneNumber"),
        @Index(columnList = "email"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "businessNumber"),
})
public class ClientCompany extends BaseEntity {

    private static final String SEQUENCE_NAME = "client_company_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
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
    private String faxNumber;

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
    @DiffInclude
    private String paymentPeriod;

    @Setter
    @ManyToOne
    @JoinColumn
    @DiffIgnore
    private User user; // 본사 담당자

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.CLIENT_COMPANY_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ClientCompanyContact> contacts = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.CLIENT_COMPANY_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ClientCompanyFile> files = new ArrayList<>();

    @Column
    @DiffInclude
    @Builder.Default
    private boolean isActive = true;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column
    @DiffInclude
    private String homepageUrl;

    @Column
    @DiffInclude
    private String homepageLoginId;

    @Column
    @DiffInclude
    private String homepagePassword;

    @Transient
    @DiffInclude
    private String userName;

    @Transient
    @DiffInclude
    private String paymentMethodName;

    public void syncTransientFields() {
        this.userName = Optional.ofNullable(this.user)
                .map(User::getUsername)
                .orElse(null);
        this.paymentMethodName = Optional.ofNullable(this.paymentMethod)
                .map(ClientCompanyPaymentMethod::getLabel)
                .orElse(null);
    }

    public void updateFrom(final ClientCompanyUpdateRequest request, final User user) {
        this.user = user;
        this.name = request.name();
        this.memo = request.memo();
        this.email = request.email();
        this.ceoName = request.ceoName();
        this.address = request.address();
        this.isActive = request.isActive();
        this.phoneNumber = request.phoneNumber();
        this.paymentMethod = request.paymentMethod();
        this.paymentPeriod = request.paymentPeriod();
        this.detailAddress = request.detailAddress();
        this.landlineNumber = request.landlineNumber();
        this.faxNumber = request.faxNumber();
        this.businessNumber = request.businessNumber();
        this.homepageUrl = request.homepageUrl();
        this.homepageLoginId = request.homepageLoginId();
        this.homepagePassword = request.homepagePassword();
        syncTransientFields();
    }

    public static ClientCompany createFrom(final ClientCompanyCreateRequest request, final User user) {
        return ClientCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .ceoName(request.ceoName())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .landlineNumber(request.landlineNumber())
                .faxNumber(request.faxNumber())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .user(user)
                .paymentMethod(request.paymentMethod())
                .paymentPeriod(request.paymentPeriod())
                .memo(request.memo())
                .isActive(request.isActive())
                .homepageUrl(request.homepageUrl())
                .homepageLoginId(request.homepageLoginId())
                .homepagePassword(request.homepagePassword())
                .build();
    }

    // 유효한 파일 URL이 있는지 확인
    public boolean hasValidFiles() {
        return files.stream().anyMatch(file -> StringUtils.hasText(file.getFileUrl()));
    }

    // 메인 연락처 목록 반환
    public List<ClientCompanyContact> getMainContacts() {
        return contacts.stream()
                .filter(contact -> contact.getIsMain())
                .toList();
    }

}
