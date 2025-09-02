package com.lineinc.erp.api.server.domain.outsourcingcontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractWorkerFileUpdateRequest;

import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractWorkerFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_worker_file_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_worker_file_seq", sequenceName = "outsourcing_company_contract_worker_file_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_worker_id", nullable = false)
    private OutsourcingCompanyContractWorker worker;

    @DiffInclude
    @Column
    private String fileUrl; // 파일 URL

    @DiffInclude
    @Column
    private String originalFileName; // 원본 파일명

    /**
     * DTO의 정보로 엔티티를 업데이트합니다.
     */
    public void updateFrom(OutsourcingCompanyContractWorkerFileUpdateRequest request) {
        if (request.fileUrl() != null) {
            this.fileUrl = request.fileUrl();
        }
        if (request.originalFileName() != null) {
            this.originalFileName = request.originalFileName();
        }
    }
}
