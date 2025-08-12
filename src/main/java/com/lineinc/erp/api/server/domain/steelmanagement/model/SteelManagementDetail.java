package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementDetailUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SteelManagementDetail extends BaseEntity implements UpdatableFrom<SteelManagementDetailUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steel_management_detail_seq")
    @SequenceGenerator(name = "steel_management_detail_seq", sequenceName = "steel_management_detail_seq", allocationSize = 1)
    private Long id;

    /**
     * 강재수불부 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "steel_management_id", nullable = false)
    private SteelManagement steelManagement;

    /**
     * 규격
     */
    @Column
    private String standard;

    /**
     * 품명
     */
    @Column
    private String name;

    /**
     * 단위
     */
    @Column
    private String unit;

    /**
     * 본
     */
    @Column
    private Integer count;

    /**
     * 길이
     */
    @Column
    private Double length;

    /**
     * 총 길이
     */
    private Double totalLength;

    /**
     * 단위중량
     */
    private Double unitWeight;

    /**
     * 수량
     */
    @Column
    private Integer quantity;

    /**
     * 단가
     */
    @Column
    private Integer unitPrice;

    /**
     * 공급가
     */
    private Integer supplyPrice;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Override
    public void updateFrom(SteelManagementDetailUpdateRequest request) {
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
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}
