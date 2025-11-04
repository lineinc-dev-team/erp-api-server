package com.lineinc.erp.api.server.domain.steelmanagementv2.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementDetailV2UpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

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
@Table(name = "steel_management_detail_v2", indexes = {
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
    @JoinColumn(name = AppConstants.STEEL_MANAGEMENT_V2_ID, nullable = false)
    private SteelManagementV2 steelManagementV2;

    /**
     * 외주업체 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany;

    /**
     * 타입: 입고, 출고, 사장, 고청
     */
    @DiffIgnore
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
    private Long unitPrice;

    /**
     * 공급가
     */
    @DiffInclude
    @Column
    private Long amount;

    /**
     * 부가세
     */
    @DiffInclude
    private Long vat;

    /**
     * 합계 (공급가 + 부가세)
     */
    @DiffInclude
    private Long total;

    /**
     * 구분: 자사자재, 구매, 임대
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column
    private SteelManagementDetailV2Category category;

    /**
     * 파일 URL
     */
    @DiffIgnore
    @Column
    private String fileUrl;

    /**
     * 원본 파일명
     */
    @DiffInclude
    @Column
    private String originalFileName;

    /**
     * 입고일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime incomingDate;

    /**
     * 출고일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime outgoingDate;

    /**
     * 판매일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime salesDate;

    /**
     * 메모
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 요청 DTO로부터 엔티티 업데이트
     */
    public void updateFrom(
            final SteelManagementDetailV2UpdateRequest request) {
        this.weight = request.weight();
        this.count = request.count();
        this.totalWeight = request.totalWeight();
        this.unitPrice = request.unitPrice();
        this.amount = request.amount();
        this.vat = request.vat();
        this.total = request.total();
        this.category = request.category();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.incomingDate = DateTimeFormatUtils.toOffsetDateTime(request.incomingDate());
        this.outgoingDate = DateTimeFormatUtils.toOffsetDateTime(request.outgoingDate());
        this.salesDate = DateTimeFormatUtils.toOffsetDateTime(request.salesDate());
        this.memo = request.memo();
    }
}
