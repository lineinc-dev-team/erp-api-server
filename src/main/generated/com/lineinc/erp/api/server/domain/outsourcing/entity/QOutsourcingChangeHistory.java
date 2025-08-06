package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingChangeHistory is a Querydsl query type for OutsourcingChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingChangeHistory extends EntityPathBase<OutsourcingChangeHistory> {

    private static final long serialVersionUID = -997419822L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingChangeHistory outsourcingChangeHistory = new QOutsourcingChangeHistory("outsourcingChangeHistory");

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

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingChangeHistory(String variable) {
        this(OutsourcingChangeHistory.class, forVariable(variable), INITS);
    }

    public QOutsourcingChangeHistory(Path<? extends OutsourcingChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingChangeHistory(PathMetadata metadata, PathInits inits) {
        this(OutsourcingChangeHistory.class, metadata, inits);
    }

    public QOutsourcingChangeHistory(Class<? extends OutsourcingChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
    }

}

