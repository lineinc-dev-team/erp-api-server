package com.lineinc.erp.api.server.domain.client.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClientCompanyContact extends BaseEntity {

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
     * 담당자의 직급 또는 부서
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
}