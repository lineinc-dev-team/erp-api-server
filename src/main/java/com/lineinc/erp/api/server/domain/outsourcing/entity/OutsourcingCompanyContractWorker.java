package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractWorker extends BaseEntity {

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
    private String note; // 비고
}