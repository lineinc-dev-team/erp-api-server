package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOutsourcingCompanyContractDriver is a Querydsl query type for OutsourcingCompanyContractDriver
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractDriver extends EntityPathBase<OutsourcingCompanyContractDriver> {

    private static final long serialVersionUID = -1704874555L;

    public static final QOutsourcingCompanyContractDriver outsourcingCompanyContractDriver = new QOutsourcingCompanyContractDriver("outsourcingCompanyContractDriver");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath driverLicenseFileUrl = createString("driverLicenseFileUrl");

    public final StringPath driverLicenseName = createString("driverLicenseName");

    public final StringPath driverLicenseOriginalFileName = createString("driverLicenseOriginalFileName");

    public final StringPath etcDocumentFileUrl = createString("etcDocumentFileUrl");

    public final StringPath etcDocumentName = createString("etcDocumentName");

    public final StringPath etcDocumentOriginalFileName = createString("etcDocumentOriginalFileName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath safetyEducationFileUrl = createString("safetyEducationFileUrl");

    public final StringPath safetyEducationName = createString("safetyEducationName");

    public final StringPath safetyEducationOriginalFileName = createString("safetyEducationOriginalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractDriver(String variable) {
        super(OutsourcingCompanyContractDriver.class, forVariable(variable));
    }

    public QOutsourcingCompanyContractDriver(Path<? extends OutsourcingCompanyContractDriver> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOutsourcingCompanyContractDriver(PathMetadata metadata) {
        super(OutsourcingCompanyContractDriver.class, metadata);
    }

}

