package com.lineinc.erp.api.server.domain.dailyreport.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "reportDate"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class DailyReport extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID)
    private Site site; // 현장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    private SiteProcess siteProcess; // 공정

    private OffsetDateTime reportDate; // 출역일보 일자

    @Setter
    @Enumerated(EnumType.STRING)
    private FuelAggregationWeatherType weather; // 날씨

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DailyReportStatus status = DailyReportStatus.PENDING; // 출역일보 상태

    private OffsetDateTime completedAt; // 마감 일시

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportEmployee> employees = new ArrayList<>(); // 출역일보 직원 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportDirectContract> directContracts = new ArrayList<>(); // 직영/용역 출역일보 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportDirectContractOutsourcing> directContractOutsourcings = new ArrayList<>();
    // 직영/용역 용역 출역일보 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportDirectContractOutsourcingContract> directContractOutsourcingContracts = new ArrayList<>();
    // 직영/용역 외주 출역일보 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportOutsourcing> outsourcings = new ArrayList<>(); // 외주 출역일보 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportOutsourcingEquipment> outsourcingEquipments = new ArrayList<>(); // 외주업체계약 장비 출역일보 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportFuel> fuels = new ArrayList<>(); // 유류 출역일보 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportOutsourcingCompany> outsourcingCompanies = new ArrayList<>(); // 외주업체 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportFile> files = new ArrayList<>(); // 현장 사진 등록 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportEvidenceFile> evidenceFiles = new ArrayList<>(); // 증빙 파일 목록

    // 공사일보 관련
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportWork> works = new ArrayList<>(); // 작업내용 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportMainProcess> mainProcesses = new ArrayList<>(); // 주요공정 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportInputStatus> inputStatuses = new ArrayList<>(); // 투입현황 목록

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportMaterialStatus> materialStatuses = new ArrayList<>(); // 자재현황 목록

    @Setter
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    // 직원 관련
    private Double employeeWorkQuantitySum; // 직원 공수합

    @Builder.Default
    private Boolean employeeEvidenceSubmitted = false; // 직원 증빙 여부

    // 직영/용역 관련
    private Double directContractWorkQuantitySum; // 직영/용역 공수합

    // 직영/용역 용역 관련
    private Double directContractOutsourcingWorkQuantitySum; // 직영/용역 용역 공수합

    @Builder.Default
    private Boolean directContractEvidenceSubmitted = false; // 직영/용역 증빙 여부

    // 외주 관련
    private Double outsourcingWorkQuantitySum; // 외주 공수합

    @Builder.Default
    private Boolean outsourcingEvidenceSubmitted = false; // 외주 증빙 여부

    @Builder.Default
    private Boolean isConstructionReport = false; // 공사일보 작성 여부

    // 장비 관련
    private Double equipmentTotalHours; // 장비 총 가동 시간

    @Builder.Default
    private Boolean equipmentEvidenceSubmitted = false; // 장비 증빙 여부

    // 외주(공사) 관련
    private Integer outsourcingConstructionItemCount; // 외주(공사) 항목 개수

    @Builder.Default
    private Boolean outsourcingConstructionEvidenceSubmitted = false; // 외주(공사) 증빙 여부

    // 현장 사진 관련
    @Builder.Default
    private Boolean sitePhotoSubmitted = false; // 현장 사진 여부

    // 유류 관련
    private Double gasolineTotalAmount; // 휘발유 총 주유량

    private Double dieselTotalAmount; // 경유 총 주유량

    private Double ureaTotalAmount; // 요소수 총 주유량

    private Double etcTotalAmount; // 기타 총 주유량

    @Builder.Default
    private Boolean fuelEvidenceSubmitted = false; // 유류 증빙 여부

    /**
     * 출역일보를 수동 마감 처리합니다.
     */
    public void complete() {
        this.status = DailyReportStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now(AppConstants.KOREA_ZONE);
    }

    /**
     * 출역일보를 자동 마감 처리합니다.
     */
    public void autoComplete() {
        this.status = DailyReportStatus.AUTO_COMPLETED;
        this.completedAt = OffsetDateTime.now(AppConstants.KOREA_ZONE);
    }

    /**
     * 직원 공수합 계산 및 업데이트
     */
    public void updateEmployeeWorkQuantitySum() {
        this.employeeWorkQuantitySum = employees.stream()
                .filter(employee -> !employee.isDeleted())
                .mapToDouble(employee -> employee.getWorkQuantity() != null ? employee.getWorkQuantity() : 0.0)
                .sum();
    }

    /**
     * 직영/용역 직영 공수합 계산 및 업데이트
     */
    public void updateDirectContractWorkQuantitySum() {
        this.directContractWorkQuantitySum = directContracts.stream()
                .filter(contract -> !contract.isDeleted())
                .mapToDouble(contract -> contract.getWorkQuantity() != null ? contract.getWorkQuantity() : 0.0)
                .sum();
    }

    /**
     * 직영/용역 용역 공수합 계산 및 업데이트
     */
    public void updateDirectContractOutsourcingWorkQuantitySum() {
        this.directContractOutsourcingWorkQuantitySum = directContractOutsourcings.stream()
                .filter(outsourcing -> !outsourcing.isDeleted())
                .mapToDouble(outsourcing -> outsourcing.getWorkQuantity() != null ? outsourcing.getWorkQuantity() : 0.0)
                .sum();
    }

    /**
     * 외주 공수합 계산 및 업데이트
     */
    public void updateOutsourcingWorkQuantitySum() {
        this.outsourcingWorkQuantitySum = outsourcings.stream()
                .filter(outsourcing -> !outsourcing.isDeleted())
                .mapToDouble(outsourcing -> outsourcing.getWorkQuantity() != null ? outsourcing.getWorkQuantity() : 0.0)
                .sum();
    }

    /**
     * 장비 총시간 계산 및 업데이트
     */
    public void updateEquipmentTotalHours() {
        this.equipmentTotalHours = outsourcingEquipments.stream()
                .filter(equipment -> !equipment.isDeleted())
                .mapToDouble(equipment -> equipment.getWorkHours() != null ? equipment.getWorkHours() : 0.0)
                .sum();
    }

    /**
     * 현장 사진 여부 업데이트
     */
    public void updateSitePhotoSubmitted() {
        this.sitePhotoSubmitted = files.stream()
                .anyMatch(file -> !file.isDeleted());
    }

    /**
     * 직원 증빙 여부 업데이트
     */
    public void updateEmployeeEvidenceSubmitted() {
        this.employeeEvidenceSubmitted = evidenceFiles.stream()
                .filter(file -> !file.isDeleted())
                .anyMatch(file -> file.getFileType() == DailyReportEvidenceFileType.EMPLOYEE);
    }

    /**
     * 직영/용역 증빙 여부 업데이트
     */
    public void updateDirectContractEvidenceSubmitted() {
        this.directContractEvidenceSubmitted = evidenceFiles.stream()
                .filter(file -> !file.isDeleted())
                .anyMatch(file -> file.getFileType() == DailyReportEvidenceFileType.DIRECT_CONTRACT);
    }

    /**
     * 외주 증빙 여부 업데이트
     */
    public void updateOutsourcingEvidenceSubmitted() {
        this.outsourcingEvidenceSubmitted = evidenceFiles.stream()
                .filter(file -> !file.isDeleted())
                .anyMatch(file -> file.getFileType() == DailyReportEvidenceFileType.OUTSOURCING);
    }

    /**
     * 장비 증빙 여부 업데이트
     */
    public void updateEquipmentEvidenceSubmitted() {
        this.equipmentEvidenceSubmitted = evidenceFiles.stream()
                .filter(file -> !file.isDeleted())
                .anyMatch(file -> file.getFileType() == DailyReportEvidenceFileType.EQUIPMENT);
    }

    /**
     * 외주(공사) 항목 개수 계산 및 업데이트
     */
    public void updateOutsourcingConstructionItemCount() {
        this.outsourcingConstructionItemCount = (int) outsourcingCompanies.stream()
                .filter(company -> !company.isDeleted())
                .flatMap(company -> company.getConstructionGroups().stream())
                .filter(group -> !group.isDeleted())
                .flatMap(group -> group.getConstructions().stream())
                .filter(construction -> !construction.isDeleted())
                .count();
    }

    /**
     * 외주(공사) 증빙 여부 업데이트
     */
    public void updateOutsourcingConstructionEvidenceSubmitted() {
        this.outsourcingConstructionEvidenceSubmitted = evidenceFiles.stream()
                .filter(file -> !file.isDeleted())
                .anyMatch(file -> file.getFileType() == DailyReportEvidenceFileType.OUTSOURCING_CONSTRUCTION);
    }

    /**
     * 휘발유 총 주유량 계산 및 업데이트
     */
    public void updateGasolineTotalAmount() {
        this.gasolineTotalAmount = fuels.stream()
                .filter(fuel -> !fuel.isDeleted())
                .mapToDouble(fuel -> fuel.getFuelAggregation() != null
                        ? fuel.getFuelAggregation().getFuelInfos().stream()
                                .filter(info -> !info.isDeleted())
                                .filter(info -> info.getFuelType() != null
                                        && info.getFuelType() == FuelInfoFuelType.GASOLINE)
                                .mapToDouble(info -> info.getFuelAmount() != null ? info.getFuelAmount() : 0.0)
                                .sum()
                        : 0.0)
                .sum();
    }

    /**
     * 경유 총 주유량 계산 및 업데이트
     */
    public void updateDieselTotalAmount() {
        this.dieselTotalAmount = fuels.stream()
                .filter(fuel -> !fuel.isDeleted())
                .mapToDouble(fuel -> fuel.getFuelAggregation() != null
                        ? fuel.getFuelAggregation().getFuelInfos().stream()
                                .filter(info -> !info.isDeleted())
                                .filter(info -> info.getFuelType() != null
                                        && info.getFuelType() == FuelInfoFuelType.DIESEL)
                                .mapToDouble(info -> info.getFuelAmount() != null ? info.getFuelAmount() : 0.0)
                                .sum()
                        : 0.0)
                .sum();
    }

    /**
     * 요소수 총 주유량 계산 및 업데이트
     */
    public void updateUreaTotalAmount() {
        this.ureaTotalAmount = fuels.stream()
                .filter(fuel -> !fuel.isDeleted())
                .mapToDouble(fuel -> fuel.getFuelAggregation() != null
                        ? fuel.getFuelAggregation().getFuelInfos().stream()
                                .filter(info -> !info.isDeleted())
                                .filter(info -> info.getFuelType() != null
                                        && info.getFuelType() == FuelInfoFuelType.UREA)
                                .mapToDouble(info -> info.getFuelAmount() != null ? info.getFuelAmount() : 0.0)
                                .sum()
                        : 0.0)
                .sum();
    }

    /**
     * 기타 총 주유량 계산 및 업데이트
     */
    public void updateEtcTotalAmount() {
        this.etcTotalAmount = fuels.stream()
                .filter(fuel -> !fuel.isDeleted())
                .mapToDouble(fuel -> fuel.getFuelAggregation() != null
                        ? fuel.getFuelAggregation().getFuelInfos().stream()
                                .filter(info -> !info.isDeleted())
                                .filter(info -> info.getFuelType() != null
                                        && info.getFuelType() == FuelInfoFuelType.ETC)
                                .mapToDouble(info -> info.getFuelAmount() != null ? info.getFuelAmount() : 0.0)
                                .sum()
                        : 0.0)
                .sum();
    }

    /**
     * 유류 증빙 여부 업데이트
     */
    public void updateFuelEvidenceSubmitted() {
        this.fuelEvidenceSubmitted = evidenceFiles.stream()
                .filter(file -> !file.isDeleted())
                .anyMatch(file -> file.getFileType() == DailyReportEvidenceFileType.FUEL);
    }

    /**
     * 공사일보 작성 여부 업데이트
     * 작업내용, 주요공정, 투입현황, 자재현황 중 하나라도 삭제되지 않은 데이터가 있으면 true
     */
    public void updateConstructionReportStatus() {
        final boolean hasWorkContent = works.stream().anyMatch(work -> !work.isDeleted());
        final boolean hasMainProcess = mainProcesses.stream().anyMatch(process -> !process.isDeleted());
        final boolean hasInputStatus = inputStatuses.stream().anyMatch(status -> !status.isDeleted());
        final boolean hasMaterialStatus = materialStatuses.stream().anyMatch(status -> !status.isDeleted());

        this.isConstructionReport = hasWorkContent || hasMainProcess || hasInputStatus || hasMaterialStatus;
    }

    /**
     * 모든 집계 데이터 업데이트
     */
    public void updateAllAggregatedData() {
        updateEmployeeWorkQuantitySum();
        updateDirectContractWorkQuantitySum();
        updateDirectContractOutsourcingWorkQuantitySum();
        updateOutsourcingWorkQuantitySum();
        updateEquipmentTotalHours();
        updateOutsourcingConstructionItemCount();
        updateSitePhotoSubmitted();
        updateEmployeeEvidenceSubmitted();
        updateDirectContractEvidenceSubmitted();
        updateOutsourcingEvidenceSubmitted();
        updateEquipmentEvidenceSubmitted();
        updateOutsourcingConstructionEvidenceSubmitted();
        updateGasolineTotalAmount();
        updateDieselTotalAmount();
        updateUreaTotalAmount();
        updateEtcTotalAmount();
        updateFuelEvidenceSubmitted();
        updateConstructionReportStatus();
    }
}
