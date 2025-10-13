package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContact is a Querydsl query type for OutsourcingCompanyContact
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContact extends EntityPathBase<OutsourcingCompanyContact> {

    private static final long serialVersionUID = -917668410L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContact outsourcingCompanyContact = new QOutsourcingCompanyContact("outsourcingCompanyContact");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

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

    public final QOutsourcingCompany outsourcingCompany;

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath position = createString("position");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContact(String variable) {
        this(OutsourcingCompanyContact.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContact(Path<? extends OutsourcingCompanyContact> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContact(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContact(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContact.class, metadata, inits);
    }

    public QOutsourcingCompanyContact(Class<? extends OutsourcingCompanyContact> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
    }

}

