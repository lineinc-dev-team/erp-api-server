package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(indexes = {
        @Index(columnList = "name")
})
@SQLRestriction("deleted = false")
public class MaterialManagementDetail extends BaseEntity {

    private static final String SEQUENCE_NAME = "material_management_detail_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.MATERIAL_MANAGEMENT_ID, nullable = false)
    private MaterialManagement materialManagement;

    /**
     * 품명
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * 규격
     */
    @DiffInclude
    @Column
    private String standard;

    /**
     * 사용용도
     */
    @DiffInclude
    @Column
    private String usage;

    /**
     * 수량
     */
    @DiffInclude
    @Column
    private Integer quantity;

    /**
     * 단가
     */
    @DiffInclude
    @Column
    private Integer unitPrice;

    /**
     * 공급가
     */
    @DiffInclude
    @Column
    private Integer supplyPrice;

    /**
     * 부가세
     */
    @DiffInclude
    @Column
    private Integer vat;

    /**
     * 합계
     */
    @DiffInclude
    @Column
    private Integer total;

    /**
     * 비고
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(final MaterialManagementDetailUpdateRequest request) {
        this.name = request.name();
        this.standard = request.standard();
        this.usage = request.usage();
        this.quantity = request.quantity();
        this.unitPrice = request.unitPrice();
        this.supplyPrice = request.supplyPrice();
        this.vat = request.vat();
        this.total = request.total();
        this.memo = request.memo();
    }
}