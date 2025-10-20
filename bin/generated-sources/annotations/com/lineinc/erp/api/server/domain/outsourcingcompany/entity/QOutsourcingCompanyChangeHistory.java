package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyChangeHistory is a Querydsl query type for OutsourcingCompanyChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyChangeHistory extends EntityPathBase<OutsourcingCompanyChangeHistory> {

    private static final long serialVersionUID = -1663113238L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyChangeHistory outsourcingCompanyChangeHistory = new QOutsourcingCompanyChangeHistory("outsourcingCompanyChangeHistory");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath changes = createString("changes");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final QOutsourcingCompany outsourcingCompany;

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyChangeHistoryType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyChangeHistoryType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyChangeHistory(String variable) {
        this(OutsourcingCompanyChangeHistory.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyChangeHistory(Path<? extends OutsourcingCompanyChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyChangeHistory(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyChangeHistory.class, metadata, inits);
    }

    public QOutsourcingCompanyChangeHistory(Class<? extends OutsourcingCompanyChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
    }

}

