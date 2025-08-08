package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractEquipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_equipment_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_equipment_seq", sequenceName = "outsourcing_company_contract_equipment_seq", allocationSize = 1)
    private Long id;

    @Column
    private String specification; // 규격

    @Column
    private String vehicleNumber; // 차량번호

    @Column
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;
    
    @Builder.Default
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyContactSubEquipment> subEquipments = new ArrayList<>();

    @Column
    private Long unitPrice;

    @Column
    private Long subtotal;

    @Column(columnDefinition = "TEXT")
    private String taskDescription; // 작업내용

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}