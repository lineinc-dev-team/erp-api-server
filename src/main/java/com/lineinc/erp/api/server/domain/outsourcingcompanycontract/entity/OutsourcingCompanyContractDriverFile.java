package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractDriverDocumentType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractDriverFileUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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

    private static final String SEQUENCE_NAME = "outsourcing_company_contract_driver_file_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_DRIVER_ID, nullable = false)
    private OutsourcingCompanyContractDriver driver;

    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutsourcingCompanyContractDriverDocumentType documentType; // 서류 타입

    @DiffIgnore
    @Column
    private String fileUrl; // 파일 URL

    @DiffIgnore
    @Column
    private String originalFileName; // 원본 파일명

    @Transient
    @DiffInclude
    private String documentTypeName; // 서류 타입 라벨

    /**
     * 파일 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractDriverFileUpdateRequest request) {
        this.documentType = request.documentType();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();

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
