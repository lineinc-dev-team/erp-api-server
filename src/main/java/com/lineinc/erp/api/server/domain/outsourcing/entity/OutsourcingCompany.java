package com.lineinc.erp.api.server.domain.outsourcing.entity;

import java.util.ArrayList;
import java.util.List;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
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
    private String name;

    @Column
    private String businessNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyType type;

    @Column
    private String typeDescription;

    @Column
    private String ceoName;

    @Column
    private String address;

    @Column
    private String detailAddress;

    @Column
    private String landlineNumber;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    /**
     * 기본공제 항목
     */
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyDefaultDeductionsType defaultDeductions;

    @Column
    private String defaultDeductionsDescription;

    /**
     * 계좌 정보 - 은행
     */
    @Column(nullable = false)
    private String bankName;

    /**
     * 계좌 정보 - 계좌번호
     */
    @Column(nullable = false)
    private String accountNumber;

    /**
     * 계좌 정보 - 예금주
     */
    @Column(nullable = false)
    private String accountHolder;

    @Builder.Default
    @OneToMany(mappedBy = "outsourcingCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyContact> contacts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "outsourcingCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyFile> files = new ArrayList<>();
}