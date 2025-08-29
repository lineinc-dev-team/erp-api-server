package com.lineinc.erp.api.server.domain.client.entity;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.domain.client.enums.PaymentMethod;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "businessNumber"),
        @Index(columnList = "paymentMethod"),
        @Index(columnList = "email"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "phoneNumber")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class ClientCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_seq")
    @SequenceGenerator(name = "client_company_seq", sequenceName = "client_company_seq", allocationSize = 1)
    private Long id;

    @DiffInclude
    @Column(nullable = false)
    private String name;

    @DiffInclude
    @Column
    private String businessNumber;

    @DiffInclude
    @Column
    private String ceoName;

    @DiffInclude
    @Column
    private String address;

    @DiffInclude
    @Column
    private String detailAddress;

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
     * 결제 방식 (현금/어음)
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMethod paymentMethod;

    /**
     * 결제 유예 기간
     */
    @DiffInclude
    @Column
    private String paymentPeriod;

    @Setter
    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 본사 담당자

    /**
     * 발주처 담당자 목록
     */
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @DiffIgnore
    @Builder.Default
    private List<ClientCompanyContact> contacts = new ArrayList<>();

    /**
     * 발주처 관련 첨부파일 목록
     */
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @DiffIgnore
    @Builder.Default
    private List<ClientCompanyFile> files = new ArrayList<>();

    /**
     * 사용 여부
     */
    @DiffInclude
    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    /**
     * 비고 / 메모
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
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

    public void updateFrom(ClientCompanyUpdateRequest request, UserRepository userRepository) {
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

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
            this.setUser(user);
            this.userName = user.getUsername();
        }
        syncTransientFields();
    }

}