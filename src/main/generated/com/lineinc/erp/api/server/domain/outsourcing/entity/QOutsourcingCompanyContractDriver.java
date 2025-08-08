package com.lineinc.erp.api.server.domain.outsourcing.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractDriver is a Querydsl query type for OutsourcingCompanyContractDriver
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractDriver extends EntityPathBase<OutsourcingCompanyContractDriver> {

    private static final long serialVersionUID = -1704874555L;

    private static final PathInits INITS = PathInits.DIRECT2;

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

    public final QOutsourcingCompanyContract outsourcingCompanyContract;

    public final StringPath safetyEducationFileUrl = createString("safetyEducationFileUrl");

    public final StringPath safetyEducationName = createString("safetyEducationName");

    public final StringPath safetyEducationOriginalFileName = createString("safetyEducationOriginalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractDriver(String variable) {
        this(OutsourcingCompanyContractDriver.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractDriver(Path<? extends OutsourcingCompanyContractDriver> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractDriver(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractDriver(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractDriver.class, metadata, inits);
    }

    public QOutsourcingCompanyContractDriver(Class<? extends OutsourcingCompanyContractDriver> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

