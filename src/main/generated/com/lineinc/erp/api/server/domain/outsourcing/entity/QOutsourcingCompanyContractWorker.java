package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractWorker is a Querydsl query type for OutsourcingCompanyContractWorker
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractWorker extends EntityPathBase<OutsourcingCompanyContractWorker> {

    private static final long serialVersionUID = -1163433701L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractWorker outsourcingCompanyContractWorker = new QOutsourcingCompanyContractWorker("outsourcingCompanyContractWorker");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath fileName = createString("fileName");

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    public final QOutsourcingCompanyContract outsourcingCompanyContract;

    public final StringPath taskDescription = createString("taskDescription");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractWorker(String variable) {
        this(OutsourcingCompanyContractWorker.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractWorker(Path<? extends OutsourcingCompanyContractWorker> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractWorker(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractWorker(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractWorker.class, metadata, inits);
    }

    public QOutsourcingCompanyContractWorker(Class<? extends OutsourcingCompanyContractWorker> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

