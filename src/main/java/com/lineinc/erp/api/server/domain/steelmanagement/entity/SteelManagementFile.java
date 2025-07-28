package com.lineinc.erp.api.server.domain.steelmanagement.entity;


import java.util.Optional;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementFileUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SteelManagementFile extends BaseEntity implements UpdatableFrom<SteelManagementFileUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steel_management_file_seq")
    @SequenceGenerator(name = "steel_management_file_seq", sequenceName = "steel_management_file_seq", allocationSize = 1)
    private Long id;

    /**
     * 강재수불부 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "steel_management_id", nullable = false)
    private SteelManagement steelManagement;


    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @Column(nullable = false)
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @Column(nullable = false)
    private String fileUrl; // S3 경로

    /**
     * 업로드된 파일의 원본 파일명
     */
    @Column
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모

    @Override
    public void updateFrom(SteelManagementFileUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.fileUrl()).ifPresent(val -> this.fileUrl = val);
        Optional.ofNullable(request.originalFileName()).ifPresent(val -> this.originalFileName = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}
