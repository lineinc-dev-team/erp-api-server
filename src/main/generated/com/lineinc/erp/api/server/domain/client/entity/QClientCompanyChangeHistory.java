package com.lineinc.erp.api.server.domain.client.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClientCompanyChangeHistory is a Querydsl query type for ClientCompanyChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClientCompanyChangeHistory extends EntityPathBase<ClientCompanyChangeHistory> {

    private static final long serialVersionUID = -541379479L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClientCompanyChangeHistory clientCompanyChangeHistory = new QClientCompanyChangeHistory("clientCompanyChangeHistory");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath changes = createString("changes");

    public final QClientCompany clientCompany;

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

    public final EnumPath<com.lineinc.erp.api.server.domain.client.enums.ChangeType> type = createEnum("type", com.lineinc.erp.api.server.domain.client.enums.ChangeType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QClientCompanyChangeHistory(String variable) {
        this(ClientCompanyChangeHistory.class, forVariable(variable), INITS);
    }

    public QClientCompanyChangeHistory(Path<? extends ClientCompanyChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClientCompanyChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClientCompanyChangeHistory(PathMetadata metadata, PathInits inits) {
        this(ClientCompanyChangeHistory.class, metadata, inits);
    }

    public QClientCompanyChangeHistory(Class<? extends ClientCompanyChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.clientCompany = inits.isInitialized("clientCompany") ? new QClientCompany(forProperty("clientCompany"), inits.get("clientCompany")) : null;
    }

}

