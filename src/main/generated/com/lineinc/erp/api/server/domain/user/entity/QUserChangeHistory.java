package com.lineinc.erp.api.server.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserChangeHistory is a Querydsl query type for UserChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserChangeHistory extends EntityPathBase<UserChangeHistory> {

    private static final long serialVersionUID = -1445790L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserChangeHistory userChangeHistory = new QUserChangeHistory("userChangeHistory");

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

    public final EnumPath<com.lineinc.erp.api.server.domain.user.enums.UserChangeType> type = createEnum("type", com.lineinc.erp.api.server.domain.user.enums.UserChangeType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final QUser user;

    public QUserChangeHistory(String variable) {
        this(UserChangeHistory.class, forVariable(variable), INITS);
    }

    public QUserChangeHistory(Path<? extends UserChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserChangeHistory(PathMetadata metadata, PathInits inits) {
        this(UserChangeHistory.class, metadata, inits);
    }

    public QUserChangeHistory(Class<? extends UserChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

