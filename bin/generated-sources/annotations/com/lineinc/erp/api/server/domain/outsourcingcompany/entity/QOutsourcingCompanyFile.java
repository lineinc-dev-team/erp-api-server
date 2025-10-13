package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyFile is a Querydsl query type for OutsourcingCompanyFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyFile extends EntityPathBase<OutsourcingCompanyFile> {

    private static final long serialVersionUID = 384986518L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyFile outsourcingCompanyFile = new QOutsourcingCompanyFile("outsourcingCompanyFile");

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

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    public final QOutsourcingCompany outsourcingCompany;

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyFileType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyFileType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyFile(String variable) {
        this(OutsourcingCompanyFile.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyFile(Path<? extends OutsourcingCompanyFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyFile(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyFile.class, metadata, inits);
    }

    public QOutsourcingCompanyFile(Class<? extends OutsourcingCompanyFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
    }

}

