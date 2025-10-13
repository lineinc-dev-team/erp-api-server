package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractWorkerFile is a Querydsl query type for OutsourcingCompanyContractWorkerFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractWorkerFile extends EntityPathBase<OutsourcingCompanyContractWorkerFile> {

    private static final long serialVersionUID = 554272632L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractWorkerFile outsourcingCompanyContractWorkerFile = new QOutsourcingCompanyContractWorkerFile("outsourcingCompanyContractWorkerFile");

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

    public final StringPath originalFileName = createString("originalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final QOutsourcingCompanyContractWorker worker;

    public QOutsourcingCompanyContractWorkerFile(String variable) {
        this(OutsourcingCompanyContractWorkerFile.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractWorkerFile(Path<? extends OutsourcingCompanyContractWorkerFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractWorkerFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractWorkerFile(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractWorkerFile.class, metadata, inits);
    }

    public QOutsourcingCompanyContractWorkerFile(Class<? extends OutsourcingCompanyContractWorkerFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.worker = inits.isInitialized("worker") ? new QOutsourcingCompanyContractWorker(forProperty("worker"), inits.get("worker")) : null;
    }

}

