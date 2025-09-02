package com.lineinc.erp.api.server.domain.outsourcingcontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractDriverDocumentType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractDriverFileUpdateRequest;

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
import jakarta.persistence.Transient;
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

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_driver_id", nullable = false)
    private OutsourcingCompanyContractDriver driver;

    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutsourcingCompanyContractDriverDocumentType documentType; // 서류 타입

    @DiffIgnore
    @Column
    private String fileUrl; // 파일 URL

    @DiffInclude
    @Column
    private String originalFileName; // 원본 파일명

    @Transient
    @DiffInclude
    private String documentTypeName; // 서류 타입 라벨

    /**
     * 파일 정보를 수정합니다.
     */
    public void updateFrom(OutsourcingCompanyContractDriverFileUpdateRequest request) {
        if (request.documentType() != null) {
            this.documentType = request.documentType();
        }
        if (request.fileUrl() != null) {
            this.fileUrl = request.fileUrl();
        }
        if (request.originalFileName() != null) {
            this.originalFileName = request.originalFileName();
        }

        // transient 필드 동기화
        syncTransientFields();
    }

    /**
     * Transient 필드들을 동기화합니다.
     */
    public void syncTransientFields() {
        this.documentTypeName = this.documentType != null ? this.documentType.getLabel() : null;
    }
}
