package com.lineinc.erp.api.server.domain.labor.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLaborFile is a Querydsl query type for LaborFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLaborFile extends EntityPathBase<LaborFile> {

    private static final long serialVersionUID = 753294382L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLaborFile laborFile = new QLaborFile("laborFile");

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

    public final QLabor labor;

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    public final EnumPath<com.lineinc.erp.api.server.domain.labor.enums.LaborFileType> type = createEnum("type", com.lineinc.erp.api.server.domain.labor.enums.LaborFileType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QLaborFile(String variable) {
        this(LaborFile.class, forVariable(variable), INITS);
    }

    public QLaborFile(Path<? extends LaborFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLaborFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLaborFile(PathMetadata metadata, PathInits inits) {
        this(LaborFile.class, metadata, inits);
    }

    public QLaborFile(Class<? extends LaborFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.labor = inits.isInitialized("labor") ? new QLabor(forProperty("labor"), inits.get("labor")) : null;
    }

}

