package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
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
public class OutsourcingCompanyContractConstruction extends BaseEntity {
    private static final String SEQUENCE_NAME = "outsourcing_company_contract_construction_seq";

    @Id
    @DiffInclude
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME,
            allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
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
    private Double contractQuantity; // 도급금액 수량

    @DiffInclude
    @Column
    private Long contractPrice; // 도급금액 금액

    @DiffInclude
    @Column
    private Double outsourcingContractQuantity; // 외주계약금액 수량

    @DiffInclude
    @Column
    private Long outsourcingContractUnitPrice; // 외주계약금액 단가

    @DiffInclude
    @Column
    private Long outsourcingContractPrice; // 외주계약금액 금액

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 메모

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_ID, nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_GROUP_ID)
    private OutsourcingCompanyContractConstructionGroup constructionGroup;

    /**
     * 공사항목 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractConstructionUpdateRequest request) {
        this.item = request.item();
        this.specification = request.specification();
        this.unit = request.unit();
        this.unitPrice = request.unitPrice();
        this.contractQuantity = request.contractQuantity();
        this.contractPrice = request.contractPrice();
        this.outsourcingContractQuantity = request.outsourcingContractQuantity();
        this.outsourcingContractUnitPrice = request.outsourcingContractUnitPrice();
        this.outsourcingContractPrice = request.outsourcingContractPrice();
        this.memo = request.memo();
    }
}
