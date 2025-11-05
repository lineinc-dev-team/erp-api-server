package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractConstruction is a Querydsl query type for OutsourcingCompanyContractConstruction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractConstruction extends EntityPathBase<OutsourcingCompanyContractConstruction> {

    private static final long serialVersionUID = -1577653585L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractConstruction outsourcingCompanyContractConstruction = new QOutsourcingCompanyContractConstruction("outsourcingCompanyContractConstruction");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final QOutsourcingCompanyContractConstructionGroup constructionGroup;

    public final NumberPath<Long> contractPrice = createNumber("contractPrice", Long.class);

    public final NumberPath<Integer> contractQuantity = createNumber("contractQuantity", Integer.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath item = createString("item");

    public final StringPath memo = createString("memo");

    public final QOutsourcingCompanyContract outsourcingCompanyContract;

    public final NumberPath<Long> outsourcingContractPrice = createNumber("outsourcingContractPrice", Long.class);

    public final NumberPath<Integer> outsourcingContractQuantity = createNumber("outsourcingContractQuantity", Integer.class);

    public final NumberPath<Long> outsourcingContractUnitPrice = createNumber("outsourcingContractUnitPrice", Long.class);

    public final StringPath specification = createString("specification");

    public final StringPath unit = createString("unit");

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractConstruction(String variable) {
        this(OutsourcingCompanyContractConstruction.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractConstruction(Path<? extends OutsourcingCompanyContractConstruction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractConstruction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractConstruction(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractConstruction.class, metadata, inits);
    }

    public QOutsourcingCompanyContractConstruction(Class<? extends OutsourcingCompanyContractConstruction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.constructionGroup = inits.isInitialized("constructionGroup") ? new QOutsourcingCompanyContractConstructionGroup(forProperty("constructionGroup"), inits.get("constructionGroup")) : null;
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

