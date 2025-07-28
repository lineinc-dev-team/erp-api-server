package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyType;
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
public class OutsourcingCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_seq")
    @SequenceGenerator(name = "outsourcing_company_seq", sequenceName = "outsourcing_company_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name; // 업체명

    @Column
    private String businessNumber; // 사업자등록번호

    @Enumerated(EnumType.STRING)
    @Column
    private OutsourcingCompanyType type; // 구분

    @Column
    private String typeDescription;

    @Column
    private String ceoName; // 대표자명

    @Column
    private String address; // 주소

    @Column
    private String detailAddress;

    @Column
    private String phoneNumber; // 전화번호

    @Column
    private String email; // 이메일

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true; // 사용 여부

    @Column
    private String defaultDeductionItem; // 기본공제 항목

    @Column
    private String accountInfo; // 계좌정보

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모
}