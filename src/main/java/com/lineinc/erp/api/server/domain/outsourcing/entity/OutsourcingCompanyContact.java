package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contact_seq")
    @SequenceGenerator(name = "outsourcing_company_contact_seq", sequenceName = "outsourcing_company_contact_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 담당자가 속한 발주처 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id", nullable = false)
    private OutsourcingCompany outsourcingCompany;

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
}