package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractSubEquipment is a Querydsl query type for OutsourcingCompanyContractSubEquipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractSubEquipment extends EntityPathBase<OutsourcingCompanyContractSubEquipment> {

    private static final long serialVersionUID = -705091316L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractSubEquipment outsourcingCompanyContractSubEquipment = new QOutsourcingCompanyContractSubEquipment("outsourcingCompanyContractSubEquipment");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final QOutsourcingCompanyContractEquipment equipment;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final NumberPath<Long> previousUnitPrice = createNumber("previousUnitPrice", Long.class);

    public final StringPath taskDescription = createString("taskDescription");

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContactSubEquipmentType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContactSubEquipmentType.class);

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractSubEquipment(String variable) {
        this(OutsourcingCompanyContractSubEquipment.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractSubEquipment(Path<? extends OutsourcingCompanyContractSubEquipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractSubEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractSubEquipment(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractSubEquipment.class, metadata, inits);
    }

    public QOutsourcingCompanyContractSubEquipment(Class<? extends OutsourcingCompanyContractSubEquipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.equipment = inits.isInitialized("equipment") ? new QOutsourcingCompanyContractEquipment(forProperty("equipment"), inits.get("equipment")) : null;
    }

}

