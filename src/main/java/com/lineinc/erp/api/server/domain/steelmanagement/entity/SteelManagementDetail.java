package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SteelManagementDetail extends BaseEntity {

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
}
