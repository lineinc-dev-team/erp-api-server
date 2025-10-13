package com.lineinc.erp.api.server.domain.managementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManagementCostDetail is a Querydsl query type for ManagementCostDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagementCostDetail extends EntityPathBase<ManagementCostDetail> {

    private static final long serialVersionUID = -1704812195L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManagementCostDetail managementCostDetail = new QManagementCostDetail("managementCostDetail");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeductible = createBoolean("isDeductible");

    public final QManagementCost managementCost;

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Long> supplyPrice = createNumber("supplyPrice", Long.class);

    public final NumberPath<Long> total = createNumber("total", Long.class);

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> vat = createNumber("vat", Long.class);

    public QManagementCostDetail(String variable) {
        this(ManagementCostDetail.class, forVariable(variable), INITS);
    }

    public QManagementCostDetail(Path<? extends ManagementCostDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManagementCostDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManagementCostDetail(PathMetadata metadata, PathInits inits) {
        this(ManagementCostDetail.class, metadata, inits);
    }

    public QManagementCostDetail(Class<? extends ManagementCostDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.managementCost = inits.isInitialized("managementCost") ? new QManagementCost(forProperty("managementCost"), inits.get("managementCost")) : null;
    }

}

