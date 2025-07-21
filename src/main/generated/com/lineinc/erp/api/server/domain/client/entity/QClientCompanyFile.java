package com.lineinc.erp.api.server.domain.client.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClientCompanyFile is a Querydsl query type for ClientCompanyFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClientCompanyFile extends EntityPathBase<ClientCompanyFile> {

    private static final long serialVersionUID = 1148798519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClientCompanyFile clientCompanyFile = new QClientCompanyFile("clientCompanyFile");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final QClientCompany clientCompany;

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

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QClientCompanyFile(String variable) {
        this(ClientCompanyFile.class, forVariable(variable), INITS);
    }

    public QClientCompanyFile(Path<? extends ClientCompanyFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClientCompanyFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClientCompanyFile(PathMetadata metadata, PathInits inits) {
        this(ClientCompanyFile.class, metadata, inits);
    }

    public QClientCompanyFile(Class<? extends ClientCompanyFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.clientCompany = inits.isInitialized("clientCompany") ? new QClientCompany(forProperty("clientCompany"), inits.get("clientCompany")) : null;
    }

}

