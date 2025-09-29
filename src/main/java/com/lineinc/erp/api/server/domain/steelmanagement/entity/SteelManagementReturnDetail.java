package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementReturnDetailUpdateRequest;
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
@Table(indexes = {
        @Index(columnList = "name"), // 품명 검색 최적화
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SteelManagementReturnDetail extends BaseEntity {

    private static final String SEQUENCE_NAME = "steel_management_return_detail_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "steel_management_id", nullable = false)
    private SteelManagement steelManagement;

    /**
     * 규격
     */
    @DiffInclude
    @Column
    private String standard;

    /**
     * 품명
     */
    @DiffInclude
    @Column
    private String name;

    /**
     * 단위
     */
    @DiffInclude
    @Column
    private String unit;

    /**
     * 본
     */
    @DiffInclude
    @Column
    private Integer count;

    /**
     * 길이
     */
    @DiffInclude
    @Column
    private Double length;

    /**
     * 총 길이
     */
    @DiffInclude
    private Double totalLength;

    /**
     * 단위중량
     */
    @DiffInclude
    private Double unitWeight;

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
    private Integer supplyPrice;

    /**
     * 부가세
     */
    @DiffInclude
    @Column
    private Integer vat;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(final SteelManagementReturnDetailUpdateRequest request) {
        Optional.ofNullable(request.standard()).ifPresent(val -> this.standard = val);
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.unit()).ifPresent(val -> this.unit = val);
        Optional.ofNullable(request.count()).ifPresent(val -> this.count = val);
        Optional.ofNullable(request.length()).ifPresent(val -> this.length = val);
        Optional.ofNullable(request.totalLength()).ifPresent(val -> this.totalLength = val);
        Optional.ofNullable(request.unitWeight()).ifPresent(val -> this.unitWeight = val);
        Optional.ofNullable(request.quantity()).ifPresent(val -> this.quantity = val);
        Optional.ofNullable(request.unitPrice()).ifPresent(val -> this.unitPrice = val);
        Optional.ofNullable(request.supplyPrice()).ifPresent(val -> this.supplyPrice = val);
        Optional.ofNullable(request.vat()).ifPresent(val -> this.vat = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}
