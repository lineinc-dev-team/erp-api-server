package com.lineinc.erp.api.server.domain.client.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClientCompany is a Querydsl query type for ClientCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClientCompany extends EntityPathBase<ClientCompany> {

    private static final long serialVersionUID = 735863323L;

    public static final QClientCompany clientCompany = new QClientCompany("clientCompany");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    public final StringPath businessNumber = createString("businessNumber");

    public final StringPath ceoName = createString("ceoName");

    public final ListPath<ClientCompanyContact, QClientCompanyContact> contacts = this.<ClientCompanyContact, QClientCompanyContact>createList("contacts", ClientCompanyContact.class, QClientCompanyContact.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final ListPath<ClientCompanyFile, QClientCompanyFile> files = this.<ClientCompanyFile, QClientCompanyFile>createList("files", ClientCompanyFile.class, QClientCompanyFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath landlineNumber = createString("landlineNumber");

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final EnumPath<com.lineinc.erp.api.server.domain.client.enums.PaymentMethod> paymentMethod = createEnum("paymentMethod", com.lineinc.erp.api.server.domain.client.enums.PaymentMethod.class);

    public final StringPath paymentPeriod = createString("paymentPeriod");

    public final StringPath phoneNumber = createString("phoneNumber");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QClientCompany(String variable) {
        super(ClientCompany.class, forVariable(variable));
    }

    public QClientCompany(Path<? extends ClientCompany> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClientCompany(PathMetadata metadata) {
        super(ClientCompany.class, metadata);
    }

}

