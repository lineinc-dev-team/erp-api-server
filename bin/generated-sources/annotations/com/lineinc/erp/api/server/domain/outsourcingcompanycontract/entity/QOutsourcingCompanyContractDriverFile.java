package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContractDriverFile is a Querydsl query type for OutsourcingCompanyContractDriverFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContractDriverFile extends EntityPathBase<OutsourcingCompanyContractDriverFile> {

    private static final long serialVersionUID = 1532847906L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContractDriverFile outsourcingCompanyContractDriverFile = new QOutsourcingCompanyContractDriverFile("outsourcingCompanyContractDriverFile");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractDriverDocumentType> documentType = createEnum("documentType", com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractDriverDocumentType.class);

    public final QOutsourcingCompanyContractDriver driver;

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalFileName = createString("originalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QOutsourcingCompanyContractDriverFile(String variable) {
        this(OutsourcingCompanyContractDriverFile.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContractDriverFile(Path<? extends OutsourcingCompanyContractDriverFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContractDriverFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContractDriverFile(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContractDriverFile.class, metadata, inits);
    }

    public QOutsourcingCompanyContractDriverFile(Class<? extends OutsourcingCompanyContractDriverFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.driver = inits.isInitialized("driver") ? new QOutsourcingCompanyContractDriver(forProperty("driver"), inits.get("driver")) : null;
    }

}

