package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReport is a Querydsl query type for DailyReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReport extends EntityPathBase<DailyReport> {

    private static final long serialVersionUID = 1657962450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReport dailyReport = new QDailyReport("dailyReport");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final DateTimePath<java.time.OffsetDateTime> completedAt = createDateTime("completedAt", java.time.OffsetDateTime.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Double> dieselTotalAmount = createNumber("dieselTotalAmount", Double.class);

    public final BooleanPath directContractEvidenceSubmitted = createBoolean("directContractEvidenceSubmitted");

    public final ListPath<DailyReportDirectContract, QDailyReportDirectContract> directContracts = this.<DailyReportDirectContract, QDailyReportDirectContract>createList("directContracts", DailyReportDirectContract.class, QDailyReportDirectContract.class, PathInits.DIRECT2);

    public final NumberPath<Double> directContractWorkQuantitySum = createNumber("directContractWorkQuantitySum", Double.class);

    public final BooleanPath employeeEvidenceSubmitted = createBoolean("employeeEvidenceSubmitted");

    public final ListPath<DailyReportEmployee, QDailyReportEmployee> employees = this.<DailyReportEmployee, QDailyReportEmployee>createList("employees", DailyReportEmployee.class, QDailyReportEmployee.class, PathInits.DIRECT2);

    public final NumberPath<Double> employeeWorkQuantitySum = createNumber("employeeWorkQuantitySum", Double.class);

    public final BooleanPath equipmentEvidenceSubmitted = createBoolean("equipmentEvidenceSubmitted");

    public final NumberPath<Double> equipmentTotalHours = createNumber("equipmentTotalHours", Double.class);

    public final NumberPath<Double> etcTotalAmount = createNumber("etcTotalAmount", Double.class);

    public final ListPath<DailyReportEvidenceFile, QDailyReportEvidenceFile> evidenceFiles = this.<DailyReportEvidenceFile, QDailyReportEvidenceFile>createList("evidenceFiles", DailyReportEvidenceFile.class, QDailyReportEvidenceFile.class, PathInits.DIRECT2);

    public final ListPath<DailyReportFile, QDailyReportFile> files = this.<DailyReportFile, QDailyReportFile>createList("files", DailyReportFile.class, QDailyReportFile.class, PathInits.DIRECT2);

    public final BooleanPath fuelEvidenceSubmitted = createBoolean("fuelEvidenceSubmitted");

    public final ListPath<DailyReportFuel, QDailyReportFuel> fuels = this.<DailyReportFuel, QDailyReportFuel>createList("fuels", DailyReportFuel.class, QDailyReportFuel.class, PathInits.DIRECT2);

    public final NumberPath<Double> gasolineTotalAmount = createNumber("gasolineTotalAmount", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<DailyReportInputStatus, QDailyReportInputStatus> inputStatuses = this.<DailyReportInputStatus, QDailyReportInputStatus>createList("inputStatuses", DailyReportInputStatus.class, QDailyReportInputStatus.class, PathInits.DIRECT2);

    public final ListPath<DailyReportMainProcess, QDailyReportMainProcess> mainProcesses = this.<DailyReportMainProcess, QDailyReportMainProcess>createList("mainProcesses", DailyReportMainProcess.class, QDailyReportMainProcess.class, PathInits.DIRECT2);

    public final ListPath<DailyReportMaterialStatus, QDailyReportMaterialStatus> materialStatuses = this.<DailyReportMaterialStatus, QDailyReportMaterialStatus>createList("materialStatuses", DailyReportMaterialStatus.class, QDailyReportMaterialStatus.class, PathInits.DIRECT2);

    public final StringPath memo = createString("memo");

    public final ListPath<DailyReportOutsourcingConstruction, QDailyReportOutsourcingConstruction> outsourcingConstructions = this.<DailyReportOutsourcingConstruction, QDailyReportOutsourcingConstruction>createList("outsourcingConstructions", DailyReportOutsourcingConstruction.class, QDailyReportOutsourcingConstruction.class, PathInits.DIRECT2);

    public final ListPath<DailyReportOutsourcingEquipment, QDailyReportOutsourcingEquipment> outsourcingEquipments = this.<DailyReportOutsourcingEquipment, QDailyReportOutsourcingEquipment>createList("outsourcingEquipments", DailyReportOutsourcingEquipment.class, QDailyReportOutsourcingEquipment.class, PathInits.DIRECT2);

    public final BooleanPath outsourcingEvidenceSubmitted = createBoolean("outsourcingEvidenceSubmitted");

    public final ListPath<DailyReportOutsourcing, QDailyReportOutsourcing> outsourcings = this.<DailyReportOutsourcing, QDailyReportOutsourcing>createList("outsourcings", DailyReportOutsourcing.class, QDailyReportOutsourcing.class, PathInits.DIRECT2);

    public final NumberPath<Double> outsourcingWorkQuantitySum = createNumber("outsourcingWorkQuantitySum", Double.class);

    public final DateTimePath<java.time.OffsetDateTime> reportDate = createDateTime("reportDate", java.time.OffsetDateTime.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final BooleanPath sitePhotoSubmitted = createBoolean("sitePhotoSubmitted");

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    public final EnumPath<com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus> status = createEnum("status", com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Double> ureaTotalAmount = createNumber("ureaTotalAmount", Double.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType> weather = createEnum("weather", com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType.class);

    public final ListPath<DailyReportWork, QDailyReportWork> works = this.<DailyReportWork, QDailyReportWork>createList("works", DailyReportWork.class, QDailyReportWork.class, PathInits.DIRECT2);

    public QDailyReport(String variable) {
        this(DailyReport.class, forVariable(variable), INITS);
    }

    public QDailyReport(Path<? extends DailyReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReport(PathMetadata metadata, PathInits inits) {
        this(DailyReport.class, metadata, inits);
    }

    public QDailyReport(Class<? extends DailyReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

