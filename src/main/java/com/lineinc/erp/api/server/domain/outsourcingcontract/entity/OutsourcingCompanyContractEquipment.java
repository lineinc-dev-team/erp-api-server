package com.lineinc.erp.api.server.domain.outsourcingcontract.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractEquipmentUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    @DiffInclude
    @Column
    private String specification; // 규격

    @DiffInclude
    @Column
    private String vehicleNumber; // 차량번호

    @DiffInclude
    @Column
    private String category;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    @DiffInclude
    @Builder.Default
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<OutsourcingCompanyContractSubEquipment> subEquipments = new ArrayList<>();

    @DiffInclude
    @Column
    private Long unitPrice;

    @DiffInclude
    @Column
    private Long subtotal;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String taskDescription; // 작업내용

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 장비 정보를 수정합니다.
     */
    public void updateFrom(OutsourcingCompanyContractEquipmentUpdateRequest request) {
        if (request.specification() != null) {
            this.specification = request.specification();
        }
        if (request.vehicleNumber() != null) {
            this.vehicleNumber = request.vehicleNumber();
        }
        if (request.category() != null) {
            this.category = request.category();
        }
        if (request.unitPrice() != null) {
            this.unitPrice = request.unitPrice();
        }
        if (request.subtotal() != null) {
            this.subtotal = request.subtotal();
        }
        if (request.taskDescription() != null) {
            this.taskDescription = request.taskDescription();
        }
        if (request.memo() != null) {
            this.memo = request.memo();
        }

        // 보조장비 정보 동기화
        if (request.subEquipments() != null) {
            EntitySyncUtils.syncList(
                    this.subEquipments,
                    request.subEquipments(),
                    (subDto) -> OutsourcingCompanyContractSubEquipment.builder()
                            .equipment(this)
                            .type(subDto.type())
                            .description(subDto.description())
                            .memo(subDto.memo())
                            .build());
        }
    }
}