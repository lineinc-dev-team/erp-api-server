package com.lineinc.erp.api.server.domain.managementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManagementCostFile is a Querydsl query type for ManagementCostFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagementCostFile extends EntityPathBase<ManagementCostFile> {

    private static final long serialVersionUID = -2070982264L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManagementCostFile managementCostFile = new QManagementCostFile("managementCostFile");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QManagementCost managementCost;

    public final StringPath memo = createString("memo");

    public final StringPath originalFileName = createString("originalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QManagementCostFile(String variable) {
        this(ManagementCostFile.class, forVariable(variable), INITS);
    }

    public QManagementCostFile(Path<? extends ManagementCostFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManagementCostFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManagementCostFile(PathMetadata metadata, PathInits inits) {
        this(ManagementCostFile.class, metadata, inits);
    }

    public QManagementCostFile(Class<? extends ManagementCostFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.managementCost = inits.isInitialized("managementCost") ? new QManagementCost(forProperty("managementCost"), inits.get("managementCost")) : null;
    }

}

