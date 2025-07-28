package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
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
public class MaterialManagementDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_management_detail_seq")
    @SequenceGenerator(name = "material_management_detail_seq", sequenceName = "material_management_detail_seq", allocationSize = 1)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_management_id", nullable = false)
    private MaterialManagement materialManagement;

    /**
     * 품명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 규격
     */
    @Column
    private String standard;

    /**
     * 사용용도
     */
    @Column
    private String usage;

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
    @Column
    private Integer supplyPrice;

    /**
     * 부가세
     */
    @Column
    private Integer vat;

    /**
     * 합계
     */
    @Column
    private Integer total;

    /**
     * 비고
     */
    @Column(columnDefinition = "TEXT")
    private String memo;
}