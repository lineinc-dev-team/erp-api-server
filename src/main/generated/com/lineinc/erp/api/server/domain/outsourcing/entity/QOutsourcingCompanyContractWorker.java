package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOutsourcingCompanyContractWorker is a Querydsl query type for OutsourcingCompanyContractWorker
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractWorker extends EntityPathBase<OutsourcingCompanyContractWorker> {

    private static final long serialVersionUID = -1163433701L;

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

    public final StringPath taskDescription = createString("taskDescription");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractWorker(String variable) {
        super(OutsourcingCompanyContractWorker.class, forVariable(variable));
    }

    public QOutsourcingCompanyContractWorker(Path<? extends OutsourcingCompanyContractWorker> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOutsourcingCompanyContractWorker(PathMetadata metadata) {
        super(OutsourcingCompanyContractWorker.class, metadata);
    }

}

