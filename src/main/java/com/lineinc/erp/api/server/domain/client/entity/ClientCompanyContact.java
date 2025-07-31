package com.lineinc.erp.api.server.domain.client.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ClientCompanyContact extends BaseEntity implements UpdatableFrom<ClientCompanyContactUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_contact_seq")
    @SequenceGenerator(name = "client_company_contact_seq", sequenceName = "client_company_contact_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 담당자가 속한 발주처 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany;

    /**
     * 담당자의 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 담당자의 부서
     */
    @Column
    private String department;

    /**
     * 담당자의 직급
     */
    @Column
    private String position;

    @Column
    private String landlineNumber;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    /**
     * 담당자에 대한 비고 또는 추가 메모
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 대표 담당자인지 여부
     */
    @Column
    @Builder.Default
    private Boolean isMain = false;

    @Override
    public void updateFrom(ClientCompanyContactUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.position()).ifPresent(val -> this.position = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

}