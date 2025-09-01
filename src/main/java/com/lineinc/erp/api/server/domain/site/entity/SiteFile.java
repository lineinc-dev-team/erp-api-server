package com.lineinc.erp.api.server.domain.site.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.domain.site.enums.SiteFileType;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteFileRequest;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
@Table(indexes = {
        @Index(columnList = "type"),
        @Index(columnList = "original_file_name")
})
public class SiteFile extends BaseEntity implements UpdatableFrom<UpdateSiteFileRequest> {

    @Id
    @DiffInclude
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_file_seq")
    @SequenceGenerator(name = "site_file_seq", sequenceName = "site_file_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_contract_id")
    private SiteContract siteContract;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @Column
    private String fileUrl; // S3 경로

    /**
     * 업로드된 파일의 원본 파일명
     */
    @DiffIgnore
    @Column
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모

    /**
     * 파일 유형 (계약서, 현장도면, 보증서류, 인허가 서류, 기타 등)
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SiteFileType type;

    @Override
    public void updateFrom(UpdateSiteFileRequest request) {
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.memo = request.memo();
        this.type = request.type();
    }
}
