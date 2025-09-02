package com.lineinc.erp.api.server.domain.outsourcingcontract.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 외주업체 계약 이력 엔티티
 * 외주업체와 외주계약의 매핑 및 계약 상태 변경 이력을 추적합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Builder
public class OutsourcingCompanyContractHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_history_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_history_seq", sequenceName = "outsourcing_company_contract_history_seq", allocationSize = 1)
    private Long id;

    /**
     * 외주업체
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id", nullable = false)
    private OutsourcingCompany outsourcingCompany;

    /**
     * 외주계약
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract contract;
}
