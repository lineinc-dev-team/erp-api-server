package com.lineinc.erp.api.server.domain.client.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "businessNumber"),
        @Index(columnList = "ceoName"),
        @Index(columnList = "email"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "phoneNumber")
})
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_seq")
    @SequenceGenerator(name = "client_company_seq", sequenceName = "client_company_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
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

    /**
     * 결제 방식 (현금/어음)
     */
    @Column
    @Enumerated(EnumType.STRING)
    @DiffIgnore
    private ClientCompanyPaymentMethod paymentMethod;

    /**
     * 결제 유예 기간
     */
    @Column
    @DiffIgnore
    private String paymentPeriod;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    @DiffIgnore
    private User user; // 본사 담당자

    /**
     * 발주처 담당자 목록
     */
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @DiffIgnore
    private final List<ClientCompanyContact> contacts = new ArrayList<>();

    /**
     * 발주처 관련 첨부파일 목록
     */
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @DiffIgnore
    private final List<ClientCompanyFile> files = new ArrayList<>();

    /**
     * 사용 여부
     */
    @Column(nullable = false)
    @Builder.Default
    @DiffInclude
    private boolean isActive = true;

    /**
     * 비고 / 메모
     */
    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    @Transient
    @DiffInclude
    private String userName;

    @Transient
    @DiffInclude
    private String paymentMethodName;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.userName = this.user != null ? this.user.getUsername() : null;
        this.paymentMethodName = this.paymentMethod != null ? this.paymentMethod.getDisplayName() : null;
    }

    public void updateFrom(final ClientCompanyUpdateRequest request, final User user) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.businessNumber()).ifPresent(val -> this.businessNumber = val);
        Optional.ofNullable(request.ceoName()).ifPresent(val -> this.ceoName = val);
        Optional.ofNullable(request.address()).ifPresent(val -> this.address = val);
        Optional.ofNullable(request.detailAddress()).ifPresent(val -> this.detailAddress = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.paymentMethod()).ifPresent(val -> this.paymentMethod = val);
        Optional.ofNullable(request.paymentPeriod()).ifPresent(val -> this.paymentPeriod = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        Optional.ofNullable(user).ifPresent(val -> this.user = val);
        syncTransientFields();
    }

}
