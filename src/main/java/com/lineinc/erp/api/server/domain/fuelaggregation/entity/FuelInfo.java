package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoCategoryType;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest.FuelInfoUpdateRequest;
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
@SQLRestriction("deleted = false")
public class FuelInfo extends BaseEntity {
    private static final String SEQUENCE_NAME = "fuel_info_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 유류집계 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.FUEL_AGGREGATION_ID, nullable = false)
    private FuelAggregation fuelAggregation;

    /**
     * 업체 (외주업체)
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany;

    /**
     * 기사
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_DRIVER_ID)
    private OutsourcingCompanyContractDriver driver;

    /**
     * 장비
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_ID)
    private OutsourcingCompanyContractEquipment equipment;

    /**
     * 구분 (장비/외주)
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    private FuelInfoCategoryType categoryType;

    /**
     * 유종
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    private FuelInfoFuelType fuelType;

    /**
     * 주유량 (리터)
     */
    @DiffInclude
    private Long fuelAmount;

    /**
     * 비고 (메모)
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Transient
    @DiffInclude
    private String outsourcingCompanyName;

    @Transient
    @DiffInclude
    private String driverName;

    @Transient
    @DiffInclude
    private String equipmentSpecification;

    @Transient
    @DiffInclude
    private String fuelTypeName;

    @Transient
    @DiffInclude
    private String categoryTypeName;

    // ID만 저장할 필드들 추가
    @Transient
    private Long outsourcingCompanyId;

    @Transient
    @DiffInclude
    private String vehicleNumber;

    @Transient
    private Long driverId;

    @Transient
    private Long equipmentId;

    @DiffIgnore
    private String fileUrl;

    @DiffInclude
    private String originalFileName;

    /**
     * 서브장비 목록
     */
    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "fuelInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FuelInfoSubEquipment> subEquipments = new ArrayList<>();

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;
        this.driverName = this.driver != null ? this.driver.getName() : null;
        this.equipmentSpecification = this.equipment != null ? this.equipment.getSpecification() : null;
        this.fuelTypeName = this.fuelType != null ? this.fuelType.getLabel() : null;
        this.categoryTypeName = this.categoryType != null ? this.categoryType.getLabel() : null;
        this.vehicleNumber = this.equipment != null ? this.equipment.getVehicleNumber() : null;
    }

    /**
     * 요청 객체와 관련 엔티티로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final FuelInfoUpdateRequest request, final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContractDriver driver, final OutsourcingCompanyContractEquipment equipment) {
        this.outsourcingCompany = outsourcingCompany;
        this.driver = driver;
        this.equipment = equipment;
        this.outsourcingCompanyId = request.outsourcingCompanyId();
        this.driverId = request.driverId();
        this.equipmentId = request.equipmentId();
        this.categoryType = request.categoryType();
        this.fuelType = request.fuelType();
        this.fuelAmount = request.fuelAmount();
        this.memo = request.memo();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();

        // 서브장비 목록 동기화
        if (request.subEquipments() != null) {
            EntitySyncUtils.syncList(
                    this.subEquipments,
                    request.subEquipments(),
                    (subEquipmentDto) -> FuelInfoSubEquipment.builder()
                            .fuelInfo(this)
                            .outsourcingCompanyContractSubEquipment(
                                    subEquipmentDto.outsourcingCompanyContractSubEquipmentId() != null
                                            ? OutsourcingCompanyContractSubEquipment.builder()
                                                    .id(subEquipmentDto.outsourcingCompanyContractSubEquipmentId())
                                                    .build()
                                            : null)
                            .fuelType(subEquipmentDto.fuelType())
                            .fuelAmount(subEquipmentDto.fuelAmount())
                            .memo(subEquipmentDto.memo())
                            .build());
        }
    }

}
