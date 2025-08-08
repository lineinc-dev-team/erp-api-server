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

    public final StringPath note = createString("note");

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
        super(OutsourcingCompanyContractEquipment.class, forVariable(variable));
    }

    public QOutsourcingCompanyContractEquipment(Path<? extends OutsourcingCompanyContractEquipment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOutsourcingCompanyContractEquipment(PathMetadata metadata) {
        super(OutsourcingCompanyContractEquipment.class, metadata);
    }

}

