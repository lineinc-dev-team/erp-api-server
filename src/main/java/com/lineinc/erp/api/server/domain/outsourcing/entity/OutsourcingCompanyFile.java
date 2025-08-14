package com.lineinc.erp.api.server.domain.outsourcing.entity;

import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyFileUpdateRequest;

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
public class OutsourcingCompanyFile extends BaseEntity implements UpdatableFrom<OutsourcingCompanyFileUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_file_seq")
    @SequenceGenerator(name = "outsourcing_company_file_seq", sequenceName = "outsourcing_company_file_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 파일이 연결된 발주처 엔티티
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id", nullable = false)
    private OutsourcingCompany outsourcingCompany;

    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @DiffInclude
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

    @Override
    public void updateFrom(OutsourcingCompanyFileUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.fileUrl()).ifPresent(val -> this.fileUrl = val);
        Optional.ofNullable(request.originalFileName()).ifPresent(val -> this.originalFileName = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}
