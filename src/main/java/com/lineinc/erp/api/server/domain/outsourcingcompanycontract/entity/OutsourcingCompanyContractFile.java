package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractFileType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractFileUpdateRequest;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_file_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_file_seq", sequenceName = "outsourcing_company_contract_file_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @DiffIgnore
    @Column
    private String fileUrl; // S3 경로

    /**
     * 업로드된 파일의 원본 파일명
     */
    @DiffInclude
    @Column
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모

    /**
     * 파일 타입 (계약서, 견적서, 기타 등)
     */
    @Enumerated(EnumType.STRING)
    @DiffIgnore
    @Column
    private OutsourcingCompanyContractFileType type;

    /**
     * DTO의 정보로 엔티티를 업데이트합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractFileUpdateRequest request) {
        if (request.name() != null) {
            this.name = request.name();
        }
        if (request.fileUrl() != null) {
            this.fileUrl = request.fileUrl();
        }
        if (request.originalFileName() != null) {
            this.originalFileName = request.originalFileName();
        }
        if (request.memo() != null) {
            this.memo = request.memo();
        }
        if (request.type() != null) {
            this.type = request.type();
        }
    }
}
