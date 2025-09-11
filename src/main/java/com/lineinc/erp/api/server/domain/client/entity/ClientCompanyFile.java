package com.lineinc.erp.api.server.domain.client.entity;

import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyFileType;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileUpdateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "originalFileName")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class ClientCompanyFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_file_seq")
    @SequenceGenerator(name = "client_company_file_seq", sequenceName = "client_company_file_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 파일이 연결된 발주처 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany;

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
     * 파일 타입 (사업자등록증, 일반 등)
     */
    @Enumerated(EnumType.STRING)
    @Column
    private ClientCompanyFileType type;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모

    public void updateFrom(ClientCompanyFileUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.fileUrl()).ifPresent(val -> this.fileUrl = val);
        Optional.ofNullable(request.originalFileName()).ifPresent(val -> this.originalFileName = val);
        Optional.ofNullable(request.type()).ifPresent(val -> this.type = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

}
