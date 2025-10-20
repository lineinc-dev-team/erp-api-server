package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLaborPayrollChangeHistory is a Querydsl query type for LaborPayrollChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLaborPayrollChangeHistory extends EntityPathBase<LaborPayrollChangeHistory> {

    private static final long serialVersionUID = -869739090L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLaborPayrollChangeHistory laborPayrollChangeHistory = new QLaborPayrollChangeHistory("laborPayrollChangeHistory");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath changes = createString("changes");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLaborPayrollSummary laborPayrollSummary;

    public final StringPath memo = createString("memo");

    public final EnumPath<com.lineinc.erp.api.server.domain.laborpayroll.enums.LaborPayrollChangeType> type = createEnum("type", com.lineinc.erp.api.server.domain.laborpayroll.enums.LaborPayrollChangeType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QLaborPayrollChangeHistory(String variable) {
        this(LaborPayrollChangeHistory.class, forVariable(variable), INITS);
    }

    public QLaborPayrollChangeHistory(Path<? extends LaborPayrollChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLaborPayrollChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLaborPayrollChangeHistory(PathMetadata metadata, PathInits inits) {
        this(LaborPayrollChangeHistory.class, metadata, inits);
    }

    public QLaborPayrollChangeHistory(Class<? extends LaborPayrollChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.laborPayrollSummary = inits.isInitialized("laborPayrollSummary") ? new QLaborPayrollSummary(forProperty("laborPayrollSummary"), inits.get("laborPayrollSummary")) : null;
    }

}

