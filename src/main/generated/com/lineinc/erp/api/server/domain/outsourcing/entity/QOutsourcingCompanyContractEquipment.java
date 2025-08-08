package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractEquipment is a Querydsl query type for OutsourcingCompanyContractEquipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractEquipment extends EntityPathBase<OutsourcingCompanyContractEquipment> {

    private static final long serialVersionUID = 513150673L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment = new QOutsourcingCompanyContractEquipment("outsourcingCompanyContractEquipment");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final QOutsourcingCompanyContract outsourcingCompanyContract;

    public final StringPath specification = createString("specification");

    public final ListPath<OutsourcingCompanyContactSubEquipment, QOutsourcingCompanyContactSubEquipment> subEquipments = this.<OutsourcingCompanyContactSubEquipment, QOutsourcingCompanyContactSubEquipment>createList("subEquipments", OutsourcingCompanyContactSubEquipment.class, QOutsourcingCompanyContactSubEquipment.class, PathInits.DIRECT2);

    public final NumberPath<Long> subtotal = createNumber("subtotal", Long.class);

    public final StringPath taskDescription = createString("taskDescription");

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath vehicleNumber = createString("vehicleNumber");

    public QOutsourcingCompanyContractEquipment(String variable) {
        this(OutsourcingCompanyContractEquipment.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractEquipment(Path<? extends OutsourcingCompanyContractEquipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractEquipment(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractEquipment.class, metadata, inits);
    }

    public QOutsourcingCompanyContractEquipment(Class<? extends OutsourcingCompanyContractEquipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

