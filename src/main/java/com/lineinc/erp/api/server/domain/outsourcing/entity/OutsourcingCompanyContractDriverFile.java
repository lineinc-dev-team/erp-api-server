package com.lineinc.erp.api.server.domain.outsourcing.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDriverDocumentType;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Builder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractDriverFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_driver_file_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_driver_file_seq", sequenceName = "outsourcing_company_contract_driver_file_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_driver_id", nullable = false)
    private OutsourcingCompanyContractDriver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutsourcingCompanyContractDriverDocumentType documentType; // 서류 타입

    @Column
    private String fileUrl; // 파일 URL

    @DiffInclude
    @Column
    private String originalFileName; // 원본 파일명
}
