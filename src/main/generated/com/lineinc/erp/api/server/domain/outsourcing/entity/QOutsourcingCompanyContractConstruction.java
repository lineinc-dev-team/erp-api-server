package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOutsourcingCompanyContractConstruction is a Querydsl query type for OutsourcingCompanyContractConstruction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractConstruction extends EntityPathBase<OutsourcingCompanyContractConstruction> {

    private static final long serialVersionUID = -23348050L;

    public static final QOutsourcingCompanyContractConstruction outsourcingCompanyContractConstruction = new QOutsourcingCompanyContractConstruction("outsourcingCompanyContractConstruction");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

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

    public final NumberPath<Long> outsourcingContractPrice = createNumber("outsourcingContractPrice", Long.class);

    public final NumberPath<Integer> outsourcingContractQuantity = createNumber("outsourcingContractQuantity", Integer.class);

    public final StringPath specification = createString("specification");

    public final StringPath unit = createString("unit");

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractConstruction(String variable) {
        super(OutsourcingCompanyContractConstruction.class, forVariable(variable));
    }

    public QOutsourcingCompanyContractConstruction(Path<? extends OutsourcingCompanyContractConstruction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOutsourcingCompanyContractConstruction(PathMetadata metadata) {
        super(OutsourcingCompanyContractConstruction.class, metadata);
    }

}

