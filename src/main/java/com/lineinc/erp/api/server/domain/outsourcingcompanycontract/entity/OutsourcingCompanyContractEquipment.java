package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractEquipmentUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
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
public class OutsourcingCompanyContractEquipment extends BaseEntity {
    private static final String SEQUENCE_NAME = "outsourcing_company_contract_equipment_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
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
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_ID, nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    @DiffInclude
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<OutsourcingCompanyContractSubEquipment> subEquipments = new ArrayList<>();

    @DiffInclude
    @Column
    private Long unitPrice;

    /**
     * 이전단가
     */
    @DiffIgnore
    @Column
    private Long previousUnitPrice;

    @DiffInclude
    @Column
    private Long subtotal;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String taskDescription; // 작업내용

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractCategoryType type;

    @Transient
    @DiffInclude
    private String typeName;

    public void syncTransientFields() {
        this.typeName = this.type != null ? this.type.getLabel() : null;
    }

    /**
     * 장비 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractEquipmentUpdateRequest request) {
        this.specification = request.specification();
        this.vehicleNumber = request.vehicleNumber();
        this.category = request.category();
        this.unitPrice = request.unitPrice();
        this.subtotal = request.subtotal();
        this.taskDescription = request.taskDescription();
        this.memo = request.memo();
        this.type = request.type();

        // 보조장비 정보 동기화
        if (request.subEquipments() != null) {
            EntitySyncUtils.syncList(
                    this.subEquipments,
                    request.subEquipments(),
                    (subDto) -> OutsourcingCompanyContractSubEquipment.builder()
                            .equipment(this)
                            .type(subDto.type())
                            .taskDescription(subDto.taskDescription())
                            .unitPrice(subDto.unitPrice())
                            .description(subDto.description())
                            .memo(subDto.memo())
                            .build());
        }

        syncTransientFields();
    }

    /**
     * 이전단가를 업데이트합니다.
     */
    public void updatePreviousUnitPrice(final Long previousUnitPrice) {
        this.previousUnitPrice = previousUnitPrice;
    }
}