package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFuelInfoSubEquipment is a Querydsl query type for FuelInfoSubEquipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFuelInfoSubEquipment extends EntityPathBase<FuelInfoSubEquipment> {

    private static final long serialVersionUID = -1925061332L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFuelInfoSubEquipment fuelInfoSubEquipment = new QFuelInfoSubEquipment("fuelInfoSubEquipment");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> fuelAmount = createNumber("fuelAmount", Long.class);

    public final QFuelInfo fuelInfo;

    public final EnumPath<com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType> fuelType = createEnum("fuelType", com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractSubEquipment outsourcingCompanyContractSubEquipment;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QFuelInfoSubEquipment(String variable) {
        this(FuelInfoSubEquipment.class, forVariable(variable), INITS);
    }

    public QFuelInfoSubEquipment(Path<? extends FuelInfoSubEquipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFuelInfoSubEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFuelInfoSubEquipment(PathMetadata metadata, PathInits inits) {
        this(FuelInfoSubEquipment.class, metadata, inits);
    }

    public QFuelInfoSubEquipment(Class<? extends FuelInfoSubEquipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fuelInfo = inits.isInitialized("fuelInfo") ? new QFuelInfo(forProperty("fuelInfo"), inits.get("fuelInfo")) : null;
        this.outsourcingCompanyContractSubEquipment = inits.isInitialized("outsourcingCompanyContractSubEquipment") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractSubEquipment(forProperty("outsourcingCompanyContractSubEquipment"), inits.get("outsourcingCompanyContractSubEquipment")) : null;
    }

}

