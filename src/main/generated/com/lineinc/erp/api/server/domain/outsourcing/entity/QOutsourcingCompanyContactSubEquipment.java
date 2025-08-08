package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContactSubEquipment is a Querydsl query type for OutsourcingCompanyContactSubEquipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContactSubEquipment extends EntityPathBase<OutsourcingCompanyContactSubEquipment> {

    private static final long serialVersionUID = -2058165149L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContactSubEquipment outsourcingCompanyContactSubEquipment = new QOutsourcingCompanyContactSubEquipment("outsourcingCompanyContactSubEquipment");

    public final QOutsourcingCompanyContractEquipment equipment;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContactSubEquipmentType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContactSubEquipmentType.class);

    public QOutsourcingCompanyContactSubEquipment(String variable) {
        this(OutsourcingCompanyContactSubEquipment.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContactSubEquipment(Path<? extends OutsourcingCompanyContactSubEquipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContactSubEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContactSubEquipment(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContactSubEquipment.class, metadata, inits);
    }

    public QOutsourcingCompanyContactSubEquipment(Class<? extends OutsourcingCompanyContactSubEquipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.equipment = inits.isInitialized("equipment") ? new QOutsourcingCompanyContractEquipment(forProperty("equipment"), inits.get("equipment")) : null;
    }

}

