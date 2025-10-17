package com.lineinc.erp.api.server.domain.clientcompany.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClientCompanyContact is a Querydsl query type for ClientCompanyContact
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClientCompanyContact extends EntityPathBase<ClientCompanyContact> {

    private static final long serialVersionUID = 481731598L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClientCompanyContact clientCompanyContact = new QClientCompanyContact("clientCompanyContact");

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

    public final StringPath department = createString("department");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isMain = createBoolean("isMain");

    public final StringPath landlineNumber = createString("landlineNumber");

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath position = createString("position");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QClientCompanyContact(String variable) {
        this(ClientCompanyContact.class, forVariable(variable), INITS);
    }

    public QClientCompanyContact(Path<? extends ClientCompanyContact> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClientCompanyContact(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClientCompanyContact(PathMetadata metadata, PathInits inits) {
        this(ClientCompanyContact.class, metadata, inits);
    }

    public QClientCompanyContact(Class<? extends ClientCompanyContact> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.clientCompany = inits.isInitialized("clientCompany") ? new QClientCompany(forProperty("clientCompany"), inits.get("clientCompany")) : null;
    }

}

