package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMaterialManagementFile is a Querydsl query type for MaterialManagementFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaterialManagementFile extends EntityPathBase<MaterialManagementFile> {

    private static final long serialVersionUID = 1093380540L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMaterialManagementFile materialManagementFile = new QMaterialManagementFile("materialManagementFile");

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

    public final QMaterialManagement materialManagement;

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMaterialManagementFile(String variable) {
        this(MaterialManagementFile.class, forVariable(variable), INITS);
    }

    public QMaterialManagementFile(Path<? extends MaterialManagementFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMaterialManagementFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMaterialManagementFile(PathMetadata metadata, PathInits inits) {
        this(MaterialManagementFile.class, metadata, inits);
    }

    public QMaterialManagementFile(Class<? extends MaterialManagementFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.materialManagement = inits.isInitialized("materialManagement") ? new QMaterialManagement(forProperty("materialManagement"), inits.get("materialManagement")) : null;
    }

}

