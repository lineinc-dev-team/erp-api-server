package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementFileUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class MaterialManagementFile extends BaseEntity implements UpdatableFrom<MaterialManagementFileUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_management_file_seq")
    @SequenceGenerator(name = "material_management_file_seq", sequenceName = "material_management_file_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_management_id", nullable = false)
    private MaterialManagement materialManagement;

    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @Column(nullable = false)
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @Column(nullable = false)
    private String fileUrl;

    /**
     * 업로드된 파일의 원본 파일명
     */
    @Column
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(MaterialManagementFileUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(value -> this.name = value);
        Optional.ofNullable(request.fileUrl()).ifPresent(value -> this.fileUrl = value);
        Optional.ofNullable(request.originalFileName()).ifPresent(value -> this.originalFileName = value);
        Optional.ofNullable(request.memo()).ifPresent(value -> this.memo = value);
    }
}