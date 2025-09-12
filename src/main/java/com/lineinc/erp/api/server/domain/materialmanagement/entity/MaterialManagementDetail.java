package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementDetailUpdateRequest;

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
public class MaterialManagementDetail extends BaseEntity
        implements UpdatableFrom<MaterialManagementDetailUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_management_detail_seq")
    @SequenceGenerator(name = "material_management_detail_seq", sequenceName = "material_management_detail_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_management_id", nullable = false)
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

    @Override
    public void updateFrom(MaterialManagementDetailUpdateRequest request) {
        java.util.Optional.ofNullable(request.name()).ifPresent(v -> this.name = v);
        java.util.Optional.ofNullable(request.standard()).ifPresent(v -> this.standard = v);
        java.util.Optional.ofNullable(request.usage()).ifPresent(v -> this.usage = v);
        java.util.Optional.ofNullable(request.quantity()).ifPresent(v -> this.quantity = v);
        java.util.Optional.ofNullable(request.unitPrice()).ifPresent(v -> this.unitPrice = v);
        java.util.Optional.ofNullable(request.supplyPrice()).ifPresent(v -> this.supplyPrice = v);
        java.util.Optional.ofNullable(request.vat()).ifPresent(v -> this.vat = v);
        java.util.Optional.ofNullable(request.total()).ifPresent(v -> this.total = v);
        java.util.Optional.ofNullable(request.memo()).ifPresent(v -> this.memo = v);
    }
}