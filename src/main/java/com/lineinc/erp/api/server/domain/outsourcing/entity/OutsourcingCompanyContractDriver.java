package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffInclude;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutsourcingCompanyContractDriver extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_driver_seq")
    @SequenceGenerator(
            name = "outsourcing_company_contract_driver_seq",
            sequenceName = "outsourcing_company_contract_driver_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    // 1. 기사자격증 파일
    @DiffInclude
    @Column(nullable = false)
    private String driverLicenseName;

    @DiffInclude
    @Column(nullable = false)
    private String driverLicenseFileUrl;

    @DiffInclude
    @Column
    private String driverLicenseOriginalFileName;

    // 2. 안전교육 파일
    @DiffInclude
    @Column(nullable = false)
    private String safetyEducationName;

    @DiffInclude
    @Column(nullable = false)
    private String safetyEducationFileUrl;

    @DiffInclude
    @Column
    private String safetyEducationOriginalFileName;

    // 3. 기타서류 파일
    @DiffInclude
    @Column(nullable = false)
    private String etcDocumentName;

    @DiffInclude
    @Column(nullable = false)
    private String etcDocumentFileUrl;

    @DiffInclude
    @Column
    private String etcDocumentOriginalFileName;


    @Column(columnDefinition = "TEXT")
    private String memo;
}
