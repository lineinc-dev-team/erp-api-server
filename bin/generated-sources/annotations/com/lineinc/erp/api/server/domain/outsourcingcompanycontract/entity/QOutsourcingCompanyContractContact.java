package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractContact is a Querydsl query type for OutsourcingCompanyContractContact
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractContact extends EntityPathBase<OutsourcingCompanyContractContact> {

    private static final long serialVersionUID = -142403710L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractContact outsourcingCompanyContractContact = new QOutsourcingCompanyContractContact("outsourcingCompanyContractContact");

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

    public final QOutsourcingCompanyContract outsourcingCompanyContract;

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath position = createString("position");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractContact(String variable) {
        this(OutsourcingCompanyContractContact.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractContact(Path<? extends OutsourcingCompanyContractContact> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractContact(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractContact(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractContact.class, metadata, inits);
    }

    public QOutsourcingCompanyContractContact(Class<? extends OutsourcingCompanyContractContact> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

