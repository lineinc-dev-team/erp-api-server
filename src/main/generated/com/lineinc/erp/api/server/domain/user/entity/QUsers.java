package com.lineinc.erp.api.server.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUsers is a Querydsl query type for Users
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsers extends EntityPathBase<Users> {

    private static final long serialVersionUID = -656012495L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUsers users = new QUsers("users");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final com.lineinc.erp.api.server.domain.company.entity.QCompany company;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.OffsetDateTime.class);

    public final StringPath loginId = createString("loginId");

    public final StringPath passwordHash = createString("passwordHash");

    public final DateTimePath<java.time.OffsetDateTime> passwordResetAt = createDateTime("passwordResetAt", java.time.OffsetDateTime.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final SetPath<com.lineinc.erp.api.server.domain.roles.entity.Roles, com.lineinc.erp.api.server.domain.roles.entity.QRoles> roles = this.<com.lineinc.erp.api.server.domain.roles.entity.Roles, com.lineinc.erp.api.server.domain.roles.entity.QRoles>createSet("roles", com.lineinc.erp.api.server.domain.roles.entity.Roles.class, com.lineinc.erp.api.server.domain.roles.entity.QRoles.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath username = createString("username");

    public QUsers(String variable) {
        this(Users.class, forVariable(variable), INITS);
    }

    public QUsers(Path<? extends Users> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUsers(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUsers(PathMetadata metadata, PathInits inits) {
        this(Users.class, metadata, inits);
    }

    public QUsers(Class<? extends Users> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new com.lineinc.erp.api.server.domain.company.entity.QCompany(forProperty("company")) : null;
    }

}

