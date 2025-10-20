package com.lineinc.erp.api.server.domain.labor.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLaborChangeHistory is a Querydsl query type for LaborChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLaborChangeHistory extends EntityPathBase<LaborChangeHistory> {

    private static final long serialVersionUID = -619584430L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLaborChangeHistory laborChangeHistory = new QLaborChangeHistory("laborChangeHistory");

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

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLabor labor;

    public final StringPath memo = createString("memo");

    public final EnumPath<com.lineinc.erp.api.server.domain.labor.enums.LaborChangeType> type = createEnum("type", com.lineinc.erp.api.server.domain.labor.enums.LaborChangeType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QLaborChangeHistory(String variable) {
        this(LaborChangeHistory.class, forVariable(variable), INITS);
    }

    public QLaborChangeHistory(Path<? extends LaborChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLaborChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLaborChangeHistory(PathMetadata metadata, PathInits inits) {
        this(LaborChangeHistory.class, metadata, inits);
    }

    public QLaborChangeHistory(Class<? extends LaborChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.labor = inits.isInitialized("labor") ? new QLabor(forProperty("labor"), inits.get("labor")) : null;
    }

}

