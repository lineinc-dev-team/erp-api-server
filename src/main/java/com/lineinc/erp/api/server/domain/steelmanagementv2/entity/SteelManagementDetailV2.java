package com.lineinc.erp.api.server.domain.steelmanagementv2.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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

/**
 * 강재수불부 V2 상세 엔티티
 */
@Entity
@Table(indexes = {
        @Index(columnList = "name"), // 품명 검색 최적화
        @Index(columnList = "type"), // 타입별 조회 최적화
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SteelManagementDetailV2 extends BaseEntity {

    private static final String SEQUENCE_NAME = "steel_management_detail_v2_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 강재수불부 V2 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "steel_management_v2_id", nullable = false)
    private SteelManagementV2 steelManagementV2;

    /**
     * 타입: 입고, 출고, 사장, 고청
     */
    @DiffInclude
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SteelManagementDetailV2Type type;

    /**
     * 품명
     */
    @DiffInclude
    @Column
    private String name;

    /**
     * 규격
     */
    @DiffInclude
    @Column
    private String specification;

    /**
     * 무게
     */
    @DiffInclude
    @Column
    private Double weight;

    /**
     * 본
     */
    @DiffInclude
    @Column
    private Integer count;

    /**
     * 총무게
     */
    @DiffInclude
    @Column
    private Double totalWeight;

    /**
     * 단가
     */
    @DiffInclude
    @Column
    private Integer unitPrice;

    /**
     * 금액
     */
    @DiffInclude
    @Column
    private Integer amount;

    /**
     * 구분: 자사자재, 구매, 임대
     */
    @DiffInclude
    @Enumerated(EnumType.STRING)
    @Column
    private SteelManagementDetailV2Category category;

    /**
     * 파일 URL
     */
    @DiffInclude
    @Column
    private String fileUrl;

    /**
     * 원본 파일명
     */
    @DiffInclude
    @Column
    private String originalFileName;

    /**
     * 메모
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;
}
