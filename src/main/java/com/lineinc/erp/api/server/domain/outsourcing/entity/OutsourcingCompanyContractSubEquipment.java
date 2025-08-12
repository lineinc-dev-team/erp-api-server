package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContactSubEquipmentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class OutsourcingCompanyContractSubEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contact_sub_equipment_seq")
    @SequenceGenerator(name = "outsourcing_company_contact_sub_equipment_seq", sequenceName = "outsourcing_company_contact_sub_equipment_seq", allocationSize = 1)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContactSubEquipmentType type; // 구분값

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_equipment_id", nullable = false)
    private OutsourcingCompanyContractEquipment equipment;

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}
