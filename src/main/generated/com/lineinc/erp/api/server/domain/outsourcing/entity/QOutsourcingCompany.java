package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompany is a Querydsl query type for OutsourcingCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompany extends EntityPathBase<OutsourcingCompany> {

    private static final long serialVersionUID = -62320373L;

    public static final QOutsourcingCompany outsourcingCompany = new QOutsourcingCompany("outsourcingCompany");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath accountHolder = createString("accountHolder");

    public final StringPath accountNumber = createString("accountNumber");

    public final StringPath address = createString("address");

    public final StringPath bankName = createString("bankName");

    public final StringPath businessNumber = createString("businessNumber");

    public final StringPath ceoName = createString("ceoName");

    public final ListPath<OutsourcingCompanyContact, QOutsourcingCompanyContact> contacts = this.<OutsourcingCompanyContact, QOutsourcingCompanyContact>createList("contacts", OutsourcingCompanyContact.class, QOutsourcingCompanyContact.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType> defaultDeductions = createEnum("defaultDeductions", com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType.class);

    public final StringPath defaultDeductionsDescription = createString("defaultDeductionsDescription");

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath detailAddress = createString("detailAddress");

    public final StringPath email = createString("email");

    public final ListPath<OutsourcingCompanyFile, QOutsourcingCompanyFile> files = this.<OutsourcingCompanyFile, QOutsourcingCompanyFile>createList("files", OutsourcingCompanyFile.class, QOutsourcingCompanyFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath landlineNumber = createString("landlineNumber");

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType.class);

    public final StringPath typeDescription = createString("typeDescription");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompany(String variable) {
        super(OutsourcingCompany.class, forVariable(variable));
    }

    public QOutsourcingCompany(Path<? extends OutsourcingCompany> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOutsourcingCompany(PathMetadata metadata) {
        super(OutsourcingCompany.class, metadata);
    }

}

