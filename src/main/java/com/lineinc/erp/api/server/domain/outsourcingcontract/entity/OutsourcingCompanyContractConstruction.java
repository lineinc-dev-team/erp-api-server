package com.lineinc.erp.api.server.domain.outsourcingcontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionUpdateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractConstruction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_construction_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_construction_seq", sequenceName = "outsourcing_company_contract_construction_seq", allocationSize = 1)
    private Long id;

    @DiffInclude
    @Column(nullable = false)
    private String item; // 항목

    @DiffInclude
    @Column
    private String specification; // 규격

    @DiffInclude
    @Column
    private String unit; // 단위

    @DiffInclude
    @Column
    private Long unitPrice; // 도급단가

    @DiffInclude
    @Column
    private Integer contractQuantity; // 도급금액 수량

    @DiffInclude
    @Column
    private Long contractPrice; // 도급금액 금액

    @DiffInclude
    @Column
    private Integer outsourcingContractQuantity; // 외주계약금액 수량

    @DiffInclude
    @Column
    private Long outsourcingContractPrice; // 외주계약금액 금액

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 메모

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    /**
     * 공사항목 정보를 수정합니다.
     */
    public void updateFrom(OutsourcingCompanyContractConstructionUpdateRequest request) {
        if (request.item() != null) {
            this.item = request.item();
        }
        if (request.specification() != null) {
            this.specification = request.specification();
        }
        if (request.unit() != null) {
            this.unit = request.unit();
        }
        if (request.unitPrice() != null) {
            this.unitPrice = request.unitPrice();
        }
        if (request.contractQuantity() != null) {
            this.contractQuantity = request.contractQuantity();
        }
        if (request.contractPrice() != null) {
            this.contractPrice = request.contractPrice();
        }
        if (request.outsourcingContractQuantity() != null) {
            this.outsourcingContractQuantity = request.outsourcingContractQuantity();
        }
        if (request.outsourcingContractPrice() != null) {
            this.outsourcingContractPrice = request.outsourcingContractPrice();
        }
        if (request.memo() != null) {
            this.memo = request.memo();
        }
    }
}
