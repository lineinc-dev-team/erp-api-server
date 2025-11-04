package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFuelInfo is a Querydsl query type for FuelInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFuelInfo extends EntityPathBase<FuelInfo> {

    private static final long serialVersionUID = 944348894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFuelInfo fuelInfo = new QFuelInfo("fuelInfo");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final EnumPath<com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoCategoryType> categoryType = createEnum("categoryType", com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoCategoryType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractDriver driver;

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractEquipment equipment;

    public final StringPath fileUrl = createString("fileUrl");

    public final QFuelAggregation fuelAggregation;

    public final NumberPath<Long> fuelAmount = createNumber("fuelAmount", Long.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType> fuelType = createEnum("fuelType", com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath originalFileName = createString("originalFileName");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final ListPath<FuelInfoSubEquipment, QFuelInfoSubEquipment> subEquipments = this.<FuelInfoSubEquipment, QFuelInfoSubEquipment>createList("subEquipments", FuelInfoSubEquipment.class, QFuelInfoSubEquipment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QFuelInfo(String variable) {
        this(FuelInfo.class, forVariable(variable), INITS);
    }

    public QFuelInfo(Path<? extends FuelInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFuelInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFuelInfo(PathMetadata metadata, PathInits inits) {
        this(FuelInfo.class, metadata, inits);
    }

    public QFuelInfo(Class<? extends FuelInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.driver = inits.isInitialized("driver") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractDriver(forProperty("driver"), inits.get("driver")) : null;
        this.equipment = inits.isInitialized("equipment") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractEquipment(forProperty("equipment"), inits.get("equipment")) : null;
        this.fuelAggregation = inits.isInitialized("fuelAggregation") ? new QFuelAggregation(forProperty("fuelAggregation"), inits.get("fuelAggregation")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
    }

}

