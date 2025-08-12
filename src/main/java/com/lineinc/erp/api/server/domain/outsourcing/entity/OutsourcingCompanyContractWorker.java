package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractWorker extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_worker_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_worker_seq", sequenceName = "outsourcing_company_contract_worker_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;
    
    @Column(nullable = false)
    private String name;

    @Column
    private String category;

    @Column(columnDefinition = "TEXT")
    private String taskDescription; // 작업내용

    @Column
    private String fileName;

    @Column
    private String fileUrl;

    @DiffInclude
    private String originalFileName;

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}