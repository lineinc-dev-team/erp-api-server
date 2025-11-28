package com.lineinc.erp.api.server.domain.managementcost.entity;

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
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostFileType;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostFileUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
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
public class ManagementCostFile extends BaseEntity {

    private static final String SEQUENCE_NAME = "management_cost_file_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME,
            allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 이 파일이 연결된 관리비 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.MANAGEMENT_COST_ID, nullable = false)
    @DiffIgnore
    private ManagementCost managementCost;

    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @DiffInclude
    @Column
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @Column
    @DiffIgnore
    private String fileUrl;

    /**
     * 업로드된 파일의 원본 파일명
     */
    @Column
    @DiffInclude
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    /**
     * 파일 타입 (기본/공과금)
     */
    @Enumerated(EnumType.STRING)
    @DiffIgnore
    @Column
    private ManagementCostFileType type;

    public void updateFrom(
            final ManagementCostFileUpdateRequest request) {
        this.name = request.name();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.memo = request.memo();
        this.type = request.type();
    }
}
