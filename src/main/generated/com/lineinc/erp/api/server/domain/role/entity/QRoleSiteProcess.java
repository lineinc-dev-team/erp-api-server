package com.lineinc.erp.api.server.domain.role.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoleSiteProcess is a Querydsl query type for RoleSiteProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoleSiteProcess extends EntityPathBase<RoleSiteProcess> {

    private static final long serialVersionUID = -2140273744L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoleSiteProcess roleSiteProcess = new QRoleSiteProcess("roleSiteProcess");

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

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess process;

    public final QRole role;

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QRoleSiteProcess(String variable) {
        this(RoleSiteProcess.class, forVariable(variable), INITS);
    }

    public QRoleSiteProcess(Path<? extends RoleSiteProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoleSiteProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoleSiteProcess(PathMetadata metadata, PathInits inits) {
        this(RoleSiteProcess.class, metadata, inits);
    }

    public QRoleSiteProcess(Class<? extends RoleSiteProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.process = inits.isInitialized("process") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("process"), inits.get("process")) : null;
        this.role = inits.isInitialized("role") ? new QRole(forProperty("role")) : null;
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
    }

}

