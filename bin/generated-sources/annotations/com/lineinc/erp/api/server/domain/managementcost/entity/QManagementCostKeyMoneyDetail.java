package com.lineinc.erp.api.server.domain.managementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManagementCostKeyMoneyDetail is a Querydsl query type for ManagementCostKeyMoneyDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagementCostKeyMoneyDetail extends EntityPathBase<ManagementCostKeyMoneyDetail> {

    private static final long serialVersionUID = 158464606L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManagementCostKeyMoneyDetail managementCostKeyMoneyDetail = new QManagementCostKeyMoneyDetail("managementCostKeyMoneyDetail");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath account = createString("account");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QManagementCost managementCost;

    public final StringPath memo = createString("memo");

    public final NumberPath<Integer> personnelCount = createNumber("personnelCount", Integer.class);

    public final StringPath purpose = createString("purpose");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QManagementCostKeyMoneyDetail(String variable) {
        this(ManagementCostKeyMoneyDetail.class, forVariable(variable), INITS);
    }

    public QManagementCostKeyMoneyDetail(Path<? extends ManagementCostKeyMoneyDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManagementCostKeyMoneyDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManagementCostKeyMoneyDetail(PathMetadata metadata, PathInits inits) {
        this(ManagementCostKeyMoneyDetail.class, metadata, inits);
    }

    public QManagementCostKeyMoneyDetail(Class<? extends ManagementCostKeyMoneyDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.managementCost = inits.isInitialized("managementCost") ? new QManagementCost(forProperty("managementCost"), inits.get("managementCost")) : null;
    }

}

