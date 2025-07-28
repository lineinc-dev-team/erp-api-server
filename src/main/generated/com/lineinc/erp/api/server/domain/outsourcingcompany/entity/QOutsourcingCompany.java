package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOutsourcingCompany is a Querydsl query type for OutsourcingCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompany extends EntityPathBase<OutsourcingCompany> {

    private static final long serialVersionUID = 1654001914L;

    public static final QOutsourcingCompany outsourcingCompany = new QOutsourcingCompany("outsourcingCompany");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath accountInfo = createString("accountInfo");

    public final StringPath address = createString("address");

    public final StringPath businessNumber = createString("businessNumber");

    public final StringPath ceoName = createString("ceoName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath defaultDeductionItem = createString("defaultDeductionItem");

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath detailAddress = createString("detailAddress");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyType.class);

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

