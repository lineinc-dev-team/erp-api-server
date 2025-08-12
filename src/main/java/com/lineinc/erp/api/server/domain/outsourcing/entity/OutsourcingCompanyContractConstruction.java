package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutsourcingCompanyContractConstruction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_construction_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_construction_seq", sequenceName = "outsourcing_company_contract_construction_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String item; // 항목

    @Column
    private String specification; // 규격

    @Column
    private String unit; // 단위

    @Column
    private Long unitPrice; // 도급단가

    @Column
    private Integer contractQuantity; // 도급금액 수량

    @Column
    private Long contractPrice; // 도급금액 금액

    @Column
    private Integer outsourcingContractQuantity; // 외주계약금액 수량

    @Column
    private Long outsourcingContractPrice; // 외주계약금액 금액


    @Column(columnDefinition = "TEXT")
    private String memo; // 메모

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;
}
