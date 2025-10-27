package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractConstructionGroup is a Querydsl query type for OutsourcingCompanyContractConstructionGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractConstructionGroup extends EntityPathBase<OutsourcingCompanyContractConstructionGroup> {

    private static final long serialVersionUID = -1534496496L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractConstructionGroup outsourcingCompanyContractConstructionGroup = new QOutsourcingCompanyContractConstructionGroup("outsourcingCompanyContractConstructionGroup");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final ListPath<OutsourcingCompanyContractConstruction, QOutsourcingCompanyContractConstruction> constructions = this.<OutsourcingCompanyContractConstruction, QOutsourcingCompanyContractConstruction>createList("constructions", OutsourcingCompanyContractConstruction.class, QOutsourcingCompanyContractConstruction.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemName = createString("itemName");

    public final QOutsourcingCompanyContract outsourcingCompanyContract;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractConstructionGroup(String variable) {
        this(OutsourcingCompanyContractConstructionGroup.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractConstructionGroup(Path<? extends OutsourcingCompanyContractConstructionGroup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractConstructionGroup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractConstructionGroup(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractConstructionGroup.class, metadata, inits);
    }

    public QOutsourcingCompanyContractConstructionGroup(Class<? extends OutsourcingCompanyContractConstructionGroup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

