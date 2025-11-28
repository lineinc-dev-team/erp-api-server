package com.lineinc.erp.api.server.domain.dailyreport.service.v1;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContractOutsourcing;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContractOutsourcingContract;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEvidenceFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFuel;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportInputStatus;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportMainProcess;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportMaterialStatus;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcing;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstruction;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstructionGroup;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipmentSubEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportWork;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportWorkDetail;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelInfoRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.service.v1.FuelAggregationService;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.labor.repository.LaborChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.labor.repository.LaborRepository;
import com.lineinc.erp.api.server.domain.labor.service.v1.LaborService;
import com.lineinc.erp.api.server.domain.laborpayroll.service.v1.LaborPayrollSyncService;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstructionGroup;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractDriverRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractWorkerRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1.OutsourcingCompanyContractConstructionService;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractOutsourcingContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractOutsourcingContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractOutsourcingCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractOutsourcingUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEvidenceFileUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportInputStatusCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportInputStatusUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportMainProcessCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportMainProcessUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportMaterialStatusCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportMaterialStatusUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingConstructionCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingConstructionUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingEquipmentSubEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportWorkCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportWorkUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractOutsourcingContractResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractOutsourcingResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEmployeeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEvidenceFileResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFileResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFuelResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportInputStatusResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportMainProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportMaterialStatusResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportOutsourcingConstructionGroupResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportOutsourcingResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportWorkDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportWorkResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelInfoCreateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final LaborService laborService;
    private final LaborRepository laborRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractWorkerRepository outsourcingCompanyContractWorkerRepository;
    private final OutsourcingCompanyContractDriverRepository outsourcingCompanyContractDriverRepository;
    private final OutsourcingCompanyContractEquipmentRepository outsourcingCompanyContractEquipmentRepository;
    private final UserService userService;
    private final LaborPayrollSyncService laborPayrollSyncService;
    private final FuelAggregationService fuelAggregationService;
    private final FuelInfoRepository fuelInfoRepository;
    private final LaborChangeHistoryRepository laborChangeHistoryRepository;
    private final OutsourcingCompanyContractConstructionService outsourcingCompanyContractConstructionService;
    private final OutsourcingCompanyContractService outsourcingCompanyContractService;

    @Transactional
    public void createDailyReport(
            final DailyReportCreateRequest request,
            final Long userId) {
        // 현장 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 같은 날짜, 현장, 공정에 대한 출역일보 조회 (소프트 삭제 포함)
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(request.reportDate());
        final Optional<DailyReport> existingDailyReportOpt = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateIncludingDeleted(site, siteProcess, reportDate);

        DailyReport dailyReport;
        if (existingDailyReportOpt.isPresent()) {
            final DailyReport existingDailyReport = existingDailyReportOpt.get();

            // 삭제되지 않은 출역일보가 이미 존재하면 에러 반환
            if (!existingDailyReport.isDeleted()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        ValidationMessages.DAILY_REPORT_ALREADY_EXISTS);
            }

            // 소프트 삭제된 출역일보는 복구하고 기본 정보만 업데이트
            existingDailyReport.restore();
            if (request.weather() != null) {
                existingDailyReport.setWeather(request.weather());
            }
            if (request.memo() != null) {
                existingDailyReport.setMemo(request.memo());
            }
            dailyReport = existingDailyReport;
        } else {
            // 출역일보 생성
            dailyReport = DailyReport.builder()
                    .site(site)
                    .siteProcess(siteProcess)
                    .reportDate(reportDate)
                    .weather(request.weather())
                    .memo(request.memo())
                    .build();
        }

        // 증빙 파일 추가
        if (request.evidenceFiles() != null) {
            for (final var evidenceFileRequest : request.evidenceFiles()) {
                for (final var evidenceFileInfo : evidenceFileRequest.files()) {
                    final DailyReportEvidenceFile evidenceFile = DailyReportEvidenceFile.builder()
                            .dailyReport(dailyReport)
                            .fileType(evidenceFileRequest.fileType())
                            .name(evidenceFileInfo.name())
                            .fileUrl(evidenceFileInfo.fileUrl())
                            .originalFileName(evidenceFileInfo.originalFileName())
                            .memo(evidenceFileInfo.memo())
                            .build();
                    dailyReport.getEvidenceFiles().add(evidenceFile);
                }
            }
        }

        // 직원 출역 정보 추가
        if (request.employees() != null) {

            for (final DailyReportEmployeeCreateRequest employeeRequest : request.employees()) {
                final Labor labor = laborService.getLaborByIdOrThrow(employeeRequest.laborId());

                final DailyReportEmployee employee = DailyReportEmployee.builder()
                        .dailyReport(dailyReport)
                        .labor(labor)
                        .workContent(employeeRequest.workContent())
                        .workQuantity(employeeRequest.workQuantity())
                        .unitPrice(labor.getDailyWage())
                        .memo(employeeRequest.memo())
                        .fileUrl(employeeRequest.fileUrl())
                        .originalFileName(employeeRequest.originalFileName())
                        .build();

                dailyReport.getEmployees().add(employee);
            }
        }

        // 직영/용역 직영 출역 정보 추가
        if (request.directContracts() != null) {

            for (final DailyReportDirectContractCreateRequest directContractRequest : request.directContracts()) {
                Labor labor;
                // 임시 인력인 경우 새로운 인력을 생성
                if (Boolean.TRUE.equals(directContractRequest.isTemporary())) {
                    labor = createTemporaryLabor(directContractRequest.temporaryLaborName(),
                            directContractRequest.unitPrice(), userId, LaborType.DIRECT_CONTRACT, null);
                } else {
                    // 기존 인력 검색
                    labor = laborService.getLaborByIdOrThrow(directContractRequest.laborId());

                }

                final DailyReportDirectContract directContract = DailyReportDirectContract.builder()
                        .dailyReport(dailyReport)
                        .labor(labor)
                        .position(directContractRequest.position())
                        .workContent(directContractRequest.workContent())
                        .unitPrice(directContractRequest.unitPrice())
                        .workQuantity(directContractRequest.workQuantity())
                        .memo(directContractRequest.memo())
                        .fileUrl(directContractRequest.fileUrl())
                        .originalFileName(directContractRequest.originalFileName())
                        .build();

                labor.updatePreviousDailyWage(directContractRequest.unitPrice());

                dailyReport.getDirectContracts().add(directContract);
            }
        }

        // 직영/용역 외주업체계약 출역 정보 추가
        if (request.directContractOutsourcingContracts() != null) {
            for (final DailyReportDirectContractOutsourcingContractCreateRequest directContractOutsourcingContractRequest : request
                    .directContractOutsourcingContracts()) {
                final OutsourcingCompany company = outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(
                        directContractOutsourcingContractRequest.outsourcingCompanyId());
                final OutsourcingCompanyContract contract = outsourcingCompanyContractService.getContractByIdOrThrow(
                        directContractOutsourcingContractRequest.outsourcingCompanyContractId());
                final Labor labor =
                        laborService.getLaborByIdOrThrow(directContractOutsourcingContractRequest.laborId());

                final DailyReportDirectContractOutsourcingContract directContractOutsourcingContract =
                        DailyReportDirectContractOutsourcingContract.builder()
                                .dailyReport(dailyReport)
                                .outsourcingCompany(company)
                                .outsourcingCompanyContract(contract)
                                .labor(labor)
                                .workQuantity(directContractOutsourcingContractRequest.workQuantity())
                                .fileUrl(directContractOutsourcingContractRequest.fileUrl())
                                .originalFileName(directContractOutsourcingContractRequest.originalFileName())
                                .memo(directContractOutsourcingContractRequest.memo())
                                .build();

                dailyReport.getDirectContractOutsourcingContracts().add(directContractOutsourcingContract);
            }
        }

        // 직영/용역 용역 출역 정보 추가
        if (request.directContractOutsourcings() != null) {
            for (final DailyReportDirectContractOutsourcingCreateRequest directContractOutsourcingRequest : request
                    .directContractOutsourcings()) {
                final OutsourcingCompany company =
                        directContractOutsourcingRequest.outsourcingCompanyId() != null
                                ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(
                                        directContractOutsourcingRequest.outsourcingCompanyId())
                                : null;

                Labor labor;
                // 임시 인력인 경우 새로운 인력을 생성
                if (Boolean.TRUE.equals(directContractOutsourcingRequest.isTemporary())) {
                    labor = createTemporaryLabor(directContractOutsourcingRequest.temporaryLaborName(),
                            directContractOutsourcingRequest.unitPrice(), userId, LaborType.OUTSOURCING,
                            directContractOutsourcingRequest.outsourcingCompanyId());
                } else {
                    // 기존 인력 검색
                    labor = laborService.getLaborByIdOrThrow(directContractOutsourcingRequest.laborId());
                }

                final DailyReportDirectContractOutsourcing directContractOutsourcing =
                        DailyReportDirectContractOutsourcing.builder()
                                .dailyReport(dailyReport)
                                .outsourcingCompany(company)
                                .labor(labor)
                                .position(directContractOutsourcingRequest.position())
                                .workContent(directContractOutsourcingRequest.workContent())
                                .unitPrice(directContractOutsourcingRequest.unitPrice())
                                .workQuantity(directContractOutsourcingRequest.workQuantity())
                                .memo(directContractOutsourcingRequest.memo())
                                .fileUrl(directContractOutsourcingRequest.fileUrl())
                                .originalFileName(directContractOutsourcingRequest.originalFileName())
                                .build();

                labor.updatePreviousDailyWage(directContractOutsourcingRequest.unitPrice());

                dailyReport.getDirectContractOutsourcings().add(directContractOutsourcing);
            }
        }

        // 외주업체계약 장비 출역 정보 추가
        if (request.outsourcingEquipments() != null) {
            for (final DailyReportOutsourcingEquipmentCreateRequest equipmentRequest : request
                    .outsourcingEquipments()) {
                final OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(equipmentRequest.outsourcingCompanyId());

                OutsourcingCompanyContractDriver driver = null;
                if (equipmentRequest.outsourcingCompanyContractDriverId() != null) {
                    driver = getOutsourcingCompanyContractDriverByIdOrThrow(
                            equipmentRequest.outsourcingCompanyContractDriverId());
                }

                OutsourcingCompanyContractEquipment equipment = null;
                if (equipmentRequest.outsourcingCompanyContractEquipmentId() != null) {
                    equipment = getOutsourcingCompanyContractEquipmentByIdOrThrow(
                            equipmentRequest.outsourcingCompanyContractEquipmentId());
                    // 장비의 이전단가 업데이트
                    if (equipment != null && equipmentRequest.unitPrice() != null) {
                        equipment.updatePreviousUnitPrice(equipmentRequest.unitPrice());
                    }
                }

                final DailyReportOutsourcingEquipment outsourcingEquipment = DailyReportOutsourcingEquipment.builder()
                        .dailyReport(dailyReport)
                        .outsourcingCompany(company)
                        .outsourcingCompanyContractDriver(driver)
                        .outsourcingCompanyContractEquipment(equipment)
                        .workContent(equipmentRequest.workContent())
                        .unitPrice(equipmentRequest.unitPrice())
                        .workHours(equipmentRequest.workHours())
                        .memo(equipmentRequest.memo())
                        .fileUrl(equipmentRequest.fileUrl())
                        .originalFileName(equipmentRequest.originalFileName())
                        .build();

                if (equipmentRequest.subEquipments() != null) {
                    for (final DailyReportOutsourcingEquipmentSubEquipmentCreateRequest subEquipmentRequest : equipmentRequest
                            .subEquipments()) {
                        // 서브장비 조회 및 이전단가 업데이트
                        OutsourcingCompanyContractSubEquipment subEquipmentEntity = null;
                        if (subEquipmentRequest.outsourcingCompanyContractSubEquipmentId() != null) {
                            subEquipmentEntity = outsourcingCompanyContractService.getSubEquipmentByIdOrThrow(
                                    subEquipmentRequest.outsourcingCompanyContractSubEquipmentId());
                            if (subEquipmentEntity != null && subEquipmentRequest.unitPrice() != null) {
                                subEquipmentEntity.updatePreviousUnitPrice(subEquipmentRequest.unitPrice());
                            }
                        }

                        final DailyReportOutsourcingEquipmentSubEquipment subEquipment =
                                DailyReportOutsourcingEquipmentSubEquipment.builder()
                                        .dailyReportOutsourcingEquipment(outsourcingEquipment)
                                        .workContent(subEquipmentRequest.workContent())
                                        .unitPrice(subEquipmentRequest.unitPrice())
                                        .workHours(subEquipmentRequest.workHours())
                                        .memo(subEquipmentRequest.memo())
                                        .outsourcingCompanyContractSubEquipment(
                                                subEquipmentEntity != null ? subEquipmentEntity
                                                        : OutsourcingCompanyContractSubEquipment.builder()
                                                                .id(subEquipmentRequest
                                                                        .outsourcingCompanyContractSubEquipmentId())
                                                                .build())
                                        .build();
                        outsourcingEquipment.getSubEquipments().add(subEquipment);
                    }
                }

                dailyReport.getOutsourcingEquipments().add(outsourcingEquipment);
            }
        }

        // 유류 출역 정보 추가
        if (request.fuelInfos() != null && !request.fuelInfos().isEmpty()) {
            // 새로 생성
            final FuelAggregationCreateRequest fuelAggregationRequest = new FuelAggregationCreateRequest(
                    request.siteId(),
                    request.siteProcessId(),
                    request.reportDate(),
                    request.weather(),
                    request.outsourcingCompanyId(),
                    request.gasolinePrice(),
                    request.dieselPrice(),
                    request.ureaPrice(),
                    request.fuelInfos()
                            .stream()
                            .map(fuelInfoRequest -> new FuelInfoCreateRequest(
                                    fuelInfoRequest.outsourcingCompanyId(),
                                    fuelInfoRequest.categoryType(),
                                    fuelInfoRequest.driverId(),
                                    fuelInfoRequest.equipmentId(),
                                    fuelInfoRequest.fuelType(),
                                    fuelInfoRequest.fuelAmount(),
                                    fuelInfoRequest.amount(),
                                    fuelInfoRequest.fileUrl(),
                                    fuelInfoRequest.originalFileName(),
                                    fuelInfoRequest.memo(),
                                    fuelInfoRequest.subEquipments()))
                            .toList());
            final FuelAggregation fuelAggregation =
                    fuelAggregationService.createFuelAggregation(fuelAggregationRequest, userId);

            // FuelAggregation만 연결하는 DailyReportFuel 생성
            final DailyReportFuel fuel =
                    DailyReportFuel.builder().dailyReport(dailyReport).fuelAggregation(fuelAggregation).build();

            dailyReport.getFuels().add(fuel);
        }

        // 외주 공사 출역 정보 추가 (2depth 구조: 외주업체+공사그룹 -> 공사항목)
        if (request.outsourcingConstructions() != null) {
            for (final DailyReportOutsourcingConstructionCreateRequest constructionRequest : request
                    .outsourcingConstructions()) {

                final OutsourcingCompany outsourcingCompany =
                        constructionRequest.outsourcingCompanyId() != null
                                ? outsourcingCompanyService
                                        .getOutsourcingCompanyByIdOrThrow(constructionRequest.outsourcingCompanyId())
                                : null;

                // 공사 그룹 조회 및 생성
                final OutsourcingCompanyContractConstructionGroup outsourcingCompanyContractConstructionGroup =
                        constructionRequest.outsourcingCompanyContractConstructionGroupId() != null
                                ? outsourcingCompanyContractConstructionService
                                        .getOutsourcingCompanyContractConstructionGroupByIdOrThrow(
                                                constructionRequest.outsourcingCompanyContractConstructionGroupId())
                                : null;

                final DailyReportOutsourcingConstructionGroup constructionGroup =
                        DailyReportOutsourcingConstructionGroup.builder()
                                .dailyReport(dailyReport)
                                .outsourcingCompany(outsourcingCompany)
                                .outsourcingCompanyContractConstructionGroup(
                                        outsourcingCompanyContractConstructionGroup)
                                .build();

                // 공사항목 목록 추가
                if (constructionRequest.items() != null) {
                    for (final DailyReportOutsourcingConstructionCreateRequest.ConstructionItemCreateRequest itemRequest : constructionRequest
                            .items()) {

                        final OutsourcingCompanyContractConstruction outsourcingCompanyContractConstruction =
                                itemRequest.outsourcingCompanyContractConstructionId() != null
                                        ? outsourcingCompanyContractConstructionService
                                                .getOutsourcingCompanyContractConstructionByIdOrThrow(
                                                        itemRequest.outsourcingCompanyContractConstructionId())
                                        : null;

                        final DailyReportOutsourcingConstruction construction =
                                DailyReportOutsourcingConstruction.builder()
                                        .outsourcingConstructionGroup(constructionGroup)
                                        .outsourcingCompanyContractConstruction(outsourcingCompanyContractConstruction)
                                        .quantity(itemRequest.quantity())
                                        .fileUrl(itemRequest.fileUrl())
                                        .originalFileName(itemRequest.originalFileName())
                                        .memo(itemRequest.memo())
                                        .build();

                        constructionGroup.getConstructions().add(construction);
                    }
                }

                dailyReport.getConstructionGroups().add(constructionGroup);
            }
        }

        // 현장 사진 정보 추가
        if (request.files() != null) {
            for (final DailyReportFileCreateRequest fileRequest : request.files()) {
                final DailyReportFile file = DailyReportFile.builder()
                        .dailyReport(dailyReport)
                        .fileUrl(fileRequest.fileUrl())
                        .originalFileName(fileRequest.originalFileName())
                        .description(fileRequest.description())
                        .memo(fileRequest.memo())
                        .build();

                dailyReport.getFiles().add(file);
            }
        }

        // 작업 정보 추가
        if (request.works() != null) {
            for (final DailyReportWorkCreateRequest workRequest : request.works()) {
                final DailyReportWork work = DailyReportWork.builder()
                        .dailyReport(dailyReport)
                        .workName(workRequest.workName())
                        .isToday(workRequest.isToday())
                        .build();

                // 작업 디테일 추가
                if (workRequest.workDetails() != null) {
                    for (final DailyReportWorkCreateRequest.DailyReportWorkDetailCreateRequest workDetailRequest : workRequest
                            .workDetails()) {
                        final DailyReportWorkDetail workDetail = DailyReportWorkDetail.builder()
                                .work(work)
                                .content(workDetailRequest.content())
                                .personnelAndEquipment(workDetailRequest.personnelAndEquipment())
                                .build();
                        work.getWorkDetails().add(workDetail);
                    }
                }

                dailyReport.getWorks().add(work);
            }
        }

        // 주요공정 정보 추가
        if (request.mainProcesses() != null) {
            for (final DailyReportMainProcessCreateRequest mainProcessRequest : request.mainProcesses()) {
                final DailyReportMainProcess mainProcess = DailyReportMainProcess.builder()
                        .dailyReport(dailyReport)
                        .process(mainProcessRequest.process())
                        .unit(mainProcessRequest.unit())
                        .contractAmount(mainProcessRequest.contractAmount())
                        .previousDayAmount(mainProcessRequest.previousDayAmount())
                        .todayAmount(mainProcessRequest.todayAmount())
                        .cumulativeAmount(mainProcessRequest.cumulativeAmount())
                        .processRate(mainProcessRequest.processRate())
                        .build();

                dailyReport.getMainProcesses().add(mainProcess);
            }
        }

        // 투입현황 정보 추가
        if (request.inputStatuses() != null) {
            for (final DailyReportInputStatusCreateRequest inputStatusRequest : request.inputStatuses()) {
                final DailyReportInputStatus inputStatus = DailyReportInputStatus.builder()
                        .dailyReport(dailyReport)
                        .category(inputStatusRequest.category())
                        .previousDayCount(inputStatusRequest.previousDayCount())
                        .todayCount(inputStatusRequest.todayCount())
                        .cumulativeCount(inputStatusRequest.cumulativeCount())
                        .type(inputStatusRequest.type())
                        .build();

                dailyReport.getInputStatuses().add(inputStatus);
            }
        }

        // 자재현황 정보 추가
        if (request.materialStatuses() != null) {
            for (final DailyReportMaterialStatusCreateRequest materialStatusRequest : request.materialStatuses()) {
                final DailyReportMaterialStatus materialStatus = DailyReportMaterialStatus.builder()
                        .dailyReport(dailyReport)
                        .materialName(materialStatusRequest.materialName())
                        .unit(materialStatusRequest.unit())
                        .plannedAmount(materialStatusRequest.plannedAmount())
                        .previousDayAmount(materialStatusRequest.previousDayAmount())
                        .todayAmount(materialStatusRequest.todayAmount())
                        .cumulativeAmount(materialStatusRequest.cumulativeAmount())
                        .remainingAmount(materialStatusRequest.remainingAmount())
                        .type(materialStatusRequest.type())
                        .build();

                dailyReport.getMaterialStatuses().add(materialStatus);
            }
        }

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        savedDailyReport.updateAllAggregatedData();
        dailyReportRepository.save(savedDailyReport);

        // 노무비 명세서 동기화 (마감 상태인 경우에만 실행)
        if (savedDailyReport.getStatus() == DailyReportStatus.COMPLETED
                || savedDailyReport.getStatus() == DailyReportStatus.AUTO_COMPLETED) {
            laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport, userId);
        }
    }

    public DailyReport getDailyReportById(
            final Long id) {
        return getDailyReportByIdOrThrow(id);
    }

    /**
     * 출역일보 상세 정보를 조회합니다.
     *
     * @param request 조회 요청 파라미터 (현장아이디, 공정아이디, 일자)
     * @return 출역일보 상세 정보
     */
    @Transactional(readOnly = true)
    public DailyReportDetailResponse getDailyReportDetail(
            final DailyReportSearchRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 출역일보 조회
        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        return DailyReportDetailResponse.from(dailyReport);
    }

    /**
     * 출역일보 기본 정보를 수정합니다. (현재: 날씨 데이터)
     *
     * @param searchRequest 조회 요청 파라미터 (현장아이디, 공정아이디, 일자)
     * @param request       수정 요청 정보
     */
    @Transactional
    public void updateDailyReport(
            final DailyReportSearchRequest searchRequest,
            final DailyReportUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 출역일보 조회
        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(searchRequest.reportDate()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 현재는 날씨 데이터만 수정
        if (request.weather() != null) {
            dailyReport.setWeather(request.weather());
        }
        if (request.memo() != null) {
            dailyReport.setMemo(request.memo());
        }

        // EntitySyncUtils.syncList를 사용하여 증빙 파일 동기화
        if (request.evidenceFiles() != null) {
            for (final DailyReportEvidenceFileUpdateRequest evidenceFileRequest : request.evidenceFiles()) {
                // 해당 fileType의 파일들만 추출하여 별도 리스트로 동기화 (다른 fileType 보호)
                final List<DailyReportEvidenceFile> typeSpecificFiles = new ArrayList<>(
                        dailyReport.getEvidenceFiles()
                                .stream()
                                .filter(file -> file.getFileType() == evidenceFileRequest.fileType())
                                .toList());

                EntitySyncUtils.syncList(typeSpecificFiles, evidenceFileRequest.files(), (
                        final DailyReportEvidenceFileUpdateRequest.EvidenceFileUpdateInfo dto) -> {
                    return DailyReportEvidenceFile.builder()
                            .dailyReport(dailyReport)
                            .fileType(evidenceFileRequest.fileType())
                            .name(dto.name())
                            .fileUrl(dto.fileUrl())
                            .originalFileName(dto.originalFileName())
                            .memo(dto.memo())
                            .build();
                });

                // 원본 리스트에서 해당 fileType 제거 후 동기화된 결과 추가
                dailyReport.getEvidenceFiles().removeIf(file -> file.getFileType() == evidenceFileRequest.fileType());
                dailyReport.getEvidenceFiles().addAll(typeSpecificFiles);
            }
        }

        dailyReportRepository.save(dailyReport);

        dailyReport.updateAllAggregatedData();
        dailyReportRepository.save(dailyReport);
    }

    /**
     * 출역일보 직원정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직원정보 슬라이스
     */
    public Slice<DailyReportEmployeeResponse> searchDailyReportEmployees(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportEmployeeResponse 슬라이스로 변환
        // 각 DailyReport의 직원들을 개별 항목으로 변환
        final List<DailyReportEmployeeResponse> allEmployees = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportEmployee employee : dailyReport.getEmployees()) {
                allEmployees.add(DailyReportEmployeeResponse.from(employee));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportEmployeeResponse> employeeSlice = new SliceImpl<>(
                allEmployees,
                pageable,
                dailyReportSlice.hasNext());

        return employeeSlice;
    }

    /**
     * 출역일보 직영/용역 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직영/용역 정보 슬라이스
     */
    public Slice<DailyReportDirectContractResponse> searchDailyReportDirectContracts(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportDirectContractResponse 슬라이스로 변환
        // 각 DailyReport의 직영/용역들을 개별 항목으로 변환
        final List<DailyReportDirectContractResponse> allDirectContracts = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                allDirectContracts.add(DailyReportDirectContractResponse.from(directContract));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportDirectContractResponse> directContractSlice = new SliceImpl<>(
                allDirectContracts,
                pageable,
                dailyReportSlice.hasNext());

        return directContractSlice;
    }

    /**
     * 출역일보 직영/용역 용역 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직영/용역 용역 정보 슬라이스
     */
    public Slice<DailyReportDirectContractOutsourcingResponse> searchDailyReportDirectContractOutsourcings(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportDirectContractOutsourcingResponse 슬라이스로 변환
        // 각 DailyReport의 직영/용역 용역들을 개별 항목으로 변환
        final List<DailyReportDirectContractOutsourcingResponse> allDirectContractOutsourcings = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportDirectContractOutsourcing directContractOutsourcing : dailyReport
                    .getDirectContractOutsourcings()) {
                allDirectContractOutsourcings
                        .add(DailyReportDirectContractOutsourcingResponse.from(directContractOutsourcing));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportDirectContractOutsourcingResponse> directContractOutsourcingSlice = new SliceImpl<>(
                allDirectContractOutsourcings,
                pageable,
                dailyReportSlice.hasNext());

        return directContractOutsourcingSlice;
    }

    /**
     * 출역일보 직영/용역 외주 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직영/용역 외주 정보 슬라이스
     */
    public Slice<DailyReportDirectContractOutsourcingContractResponse> searchDailyReportDirectContractOutsourcingContracts(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportDirectContractOutsourcingResponse 슬라이스로 변환
        // 각 DailyReport의 직영/용역 외주들을 개별 항목으로 변환
        final List<DailyReportDirectContractOutsourcingContractResponse> allDirectContractOutsourcings =
                new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportDirectContractOutsourcingContract directContractOutsourcingContract : dailyReport
                    .getDirectContractOutsourcingContracts()) {
                allDirectContractOutsourcings.add(
                        DailyReportDirectContractOutsourcingContractResponse.from(directContractOutsourcingContract));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportDirectContractOutsourcingContractResponse> directContractOutsourcingSlice =
                new SliceImpl<>(
                        allDirectContractOutsourcings,
                        pageable,
                        dailyReportSlice.hasNext());

        return directContractOutsourcingSlice;
    }

    /**
     * 출역일보 외주 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 외주 정보 슬라이스
     */
    public Slice<DailyReportOutsourcingResponse> searchDailyReportOutsourcings(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportOutsourcingResponse 슬라이스로 변환
        // 각 DailyReport의 외주들을 개별 항목으로 변환
        final List<DailyReportOutsourcingResponse> allOutsourcings = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportOutsourcing outsourcing : dailyReport.getOutsourcings()) {
                allOutsourcings.add(DailyReportOutsourcingResponse.from(outsourcing));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportOutsourcingResponse> outsourcingSlice = new SliceImpl<>(
                allOutsourcings,
                pageable,
                dailyReportSlice.hasNext());

        return outsourcingSlice;
    }

    /**
     * 출역일보 유류 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 유류 정보 슬라이스
     */
    public Slice<DailyReportFuelResponse> searchDailyReportFuels(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // FuelInfo를 직접 페이징으로 조회
        final Slice<FuelInfo> fuelInfoSlice = fuelInfoRepository.findByDailyReportSiteAndProcessAndDate(site,
                siteProcess, DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), pageable);

        // FuelInfo를 DailyReportFuelResponse로 변환
        final List<DailyReportFuelResponse> fuelResponses =
                fuelInfoSlice.getContent().stream().map(fuelInfo -> DailyReportFuelResponse.from(fuelInfo)).toList();

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportFuelResponse> fuelSlice = new SliceImpl<>(
                fuelResponses,
                pageable,
                fuelInfoSlice.hasNext());

        return fuelSlice;
    }

    /**
     * 출역일보 장비 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 장비 정보 슬라이스
     */
    public Slice<DailyReportEquipmentResponse> searchDailyReportEquipments(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportEquipmentResponse 슬라이스로 변환
        // 각 DailyReport의 장비들을 개별 항목으로 변환
        final List<DailyReportEquipmentResponse> allEquipments = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportOutsourcingEquipment equipment : dailyReport.getOutsourcingEquipments()) {
                allEquipments.add(DailyReportEquipmentResponse.from(equipment));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportEquipmentResponse> equipmentSlice = new SliceImpl<>(
                allEquipments,
                pageable,
                dailyReportSlice.hasNext());

        return equipmentSlice;
    }

    /**
     * 출역일보 파일 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 파일 정보 슬라이스
     */
    public Slice<DailyReportFileResponse> searchDailyReportFiles(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportFileResponse 슬라이스로 변환
        // 각 DailyReport의 파일들을 개별 항목으로 변환
        final List<DailyReportFileResponse> allFiles = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportFile file : dailyReport.getFiles()) {
                allFiles.add(DailyReportFileResponse.from(file));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportFileResponse> fileSlice = new SliceImpl<>(
                allFiles,
                pageable,
                dailyReportSlice.hasNext());

        return fileSlice;
    }

    /**
     * 출역일보 증빙 파일 정보를 슬라이스로 조회합니다.
     *
     * @param id       출역일보 아이디
     * @param fileType 증빙 파일 타입
     * @param pageable 페이징 정보
     * @return 출역일보 증빙 파일 정보 슬라이스
     */
    public Slice<DailyReportEvidenceFileResponse> searchDailyReportEvidenceFiles(
            final Long id,
            final DailyReportEvidenceFileType fileType,
            final Pageable pageable) {
        final DailyReport dailyReport = dailyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        final List<DailyReportEvidenceFileResponse> evidenceFiles = dailyReport.getEvidenceFiles()
                .stream()
                .filter(file -> fileType == null || file.getFileType() == fileType)
                .map(DailyReportEvidenceFileResponse::from)
                .toList();

        return new SliceImpl<>(
                evidenceFiles,
                pageable,
                false);
    }

    @Transactional
    public void updateDailyReportEmployees(
            final DailyReportSearchRequest searchRequest,
            final DailyReportEmployeeUpdateRequest request,
            final Long userId) {

        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 중복 laborId 체크
        final Set<Long> laborIds = new HashSet<>();
        for (final DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo employee : request.employees()) {
            if (employee.laborId() != null && !laborIds.add(employee.laborId())) {
                throw new IllegalArgumentException(
                        ValidationMessages.DAILY_REPORT_EMPLOYEE_DUPLICATE_LABOR_ID);
            }
        }

        // 정규직원만 직원 출역 정보에 추가 가능
        if (request.employees()
                .stream()
                .anyMatch(employee -> employee.laborId() != null && laborService.getLaborByIdOrThrow(employee.laborId())
                        .getType() != LaborType.REGULAR_EMPLOYEE)) {
            throw new IllegalArgumentException(
                    ValidationMessages.DAILY_REPORT_EMPLOYEE_MUST_BE_REGULAR);
        }

        // EntitySyncUtils.syncList를 사용하여 직원정보 동기화
        EntitySyncUtils.syncList(dailyReport.getEmployees(), request.employees(), (
                final DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo dto) -> {
            return DailyReportEmployee.builder()
                    .dailyReport(dailyReport)
                    .labor(laborService.getLaborByIdOrThrow(dto.laborId()))
                    .workContent(dto.workContent())
                    .workQuantity(dto.workQuantity())
                    .unitPrice(laborService.getLaborByIdOrThrow(dto.laborId()).getDailyWage())
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .memo(dto.memo())
                    .build();
        });

        // labor 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo employeeInfo : request.employees()) {
            if (employeeInfo.laborId() != null && employeeInfo.id() != null) { // ID가 있는 것만 처리
                final Labor labor = laborService.getLaborByIdOrThrow(employeeInfo.laborId());

                // 기존 엔티티만 찾아서 labor 설정 (ID가 null이 아닌 것만)
                dailyReport.getEmployees()
                        .stream()
                        .filter(emp -> emp.getId() != null && emp.getId().equals(employeeInfo.id()))
                        .findFirst()
                        .ifPresent(emp -> emp.updateFrom(employeeInfo, labor));
            }
        }

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        savedDailyReport.updateAllAggregatedData();
        dailyReportRepository.save(savedDailyReport);

        // 노무비 명세서 동기화 (마감 상태인 경우에만 실행)
        if (savedDailyReport.getStatus() == DailyReportStatus.COMPLETED
                || savedDailyReport.getStatus() == DailyReportStatus.AUTO_COMPLETED) {
            laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport, userId);
        }
    }

    @Transactional
    public void updateDailyReportDirectContracts(
            final DailyReportSearchRequest searchRequest,
            final DailyReportDirectContractUpdateRequest request,
            final Long userId) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 중복 laborId + unitPrice 체크 (임시 인력 제외)
        final Set<String> directContractKeys = new HashSet<>();
        for (final DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo directContract : request
                .directContracts()) {
            if (!Boolean.TRUE.equals(directContract.isTemporary()) && directContract.laborId() != null) {
                final String key = directContract.laborId() + "_" + directContract.unitPrice();
                if (!directContractKeys.add(key)) {
                    throw new IllegalArgumentException(
                            ValidationMessages.DAILY_REPORT_DIRECT_CONTRACT_DUPLICATE_LABOR_ID);
                }
            }
        }

        // EntitySyncUtils.syncList를 사용하여 직영/용역 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getDirectContracts(), request.directContracts(), (
                final DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo dto) -> {
            Labor labor;
            // 임시 인력인 경우 새로운 인력을 생성
            if (Boolean.TRUE.equals(dto.isTemporary())) {
                labor = createTemporaryLabor(dto.temporaryLaborName(), dto.unitPrice(), userId,
                        LaborType.DIRECT_CONTRACT, dto.outsourcingCompanyId());
            } else {
                // 기존 인력 검색
                labor = laborService.getLaborByIdOrThrow(dto.laborId());

                labor.updatePreviousDailyWage(dto.unitPrice());
            }

            final OutsourcingCompany outsourcingCompany = dto.outsourcingCompanyId() != null
                    ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId())
                    : null;

            return DailyReportDirectContract.builder()
                    .dailyReport(dailyReport)
                    .outsourcingCompany(outsourcingCompany)
                    .labor(labor)
                    .position(dto.position())
                    .workContent(dto.workContent())
                    .unitPrice(dto.unitPrice())
                    .workQuantity(dto.workQuantity())
                    .memo(dto.memo())
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .build();
        });

        // company와 labor 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo directContractInfo : request
                .directContracts()) {
            if (directContractInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany company =
                        directContractInfo.outsourcingCompanyId() != null
                                ? outsourcingCompanyService
                                        .getOutsourcingCompanyByIdOrThrow(directContractInfo.outsourcingCompanyId())
                                : null;
                final Labor labor = directContractInfo.laborId() != null
                        ? laborService.getLaborByIdOrThrow(directContractInfo.laborId())
                        : null;

                // 기존 엔티티만 찾아서 company와 labor 설정 (ID가 null이 아닌 것만)
                dailyReport.getDirectContracts()
                        .stream()
                        .filter(dc -> dc.getId() != null && dc.getId().equals(directContractInfo.id()))
                        .findFirst()
                        .ifPresent(dc -> {
                            dc.updateFrom(directContractInfo, labor, company);
                        });
            }
        }

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        savedDailyReport.updateAllAggregatedData();
        dailyReportRepository.save(savedDailyReport);

        // 노무비 명세서 동기화 (마감 상태인 경우에만 실행)
        if (savedDailyReport.getStatus() == DailyReportStatus.COMPLETED
                || savedDailyReport.getStatus() == DailyReportStatus.AUTO_COMPLETED) {
            laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport, userId);
        }
    }

    @Transactional
    public void updateDailyReportDirectContractOutsourcingContracts(
            final DailyReportSearchRequest searchRequest,
            final DailyReportDirectContractOutsourcingContractUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 직영/용역 외주 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getDirectContractOutsourcingContracts(),
                request.directContractOutsourcingContracts(), (
                        final DailyReportDirectContractOutsourcingContractUpdateRequest.DirectContractOutsourcingContractUpdateInfo dto) -> {
                    final OutsourcingCompany company =
                            outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId());
                    final OutsourcingCompanyContract contract = outsourcingCompanyContractService
                            .getContractByIdOrThrow(dto.outsourcingCompanyContractId());
                    final Labor labor = laborService.getLaborByIdOrThrow(dto.laborId());

                    return DailyReportDirectContractOutsourcingContract.builder()
                            .dailyReport(dailyReport)
                            .outsourcingCompany(company)
                            .outsourcingCompanyContract(contract)
                            .labor(labor)
                            .workQuantity(dto.workQuantity())
                            .fileUrl(dto.fileUrl())
                            .originalFileName(dto.originalFileName())
                            .memo(dto.memo())
                            .build();
                });

        // company, contract, labor 업데이트를 위해 추가 처리
        for (final DailyReportDirectContractOutsourcingContractUpdateRequest.DirectContractOutsourcingContractUpdateInfo directContractOutsourcingContractInfo : request
                .directContractOutsourcingContracts()) {
            if (directContractOutsourcingContractInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(directContractOutsourcingContractInfo.outsourcingCompanyId());
                final OutsourcingCompanyContract contract = outsourcingCompanyContractService
                        .getContractByIdOrThrow(directContractOutsourcingContractInfo.outsourcingCompanyContractId());
                final Labor labor = laborService.getLaborByIdOrThrow(directContractOutsourcingContractInfo.laborId());

                // 기존 엔티티만 찾아서 업데이트
                dailyReport.getDirectContractOutsourcingContracts()
                        .stream()
                        .filter(dco -> dco.getId() != null
                                && dco.getId().equals(directContractOutsourcingContractInfo.id()))
                        .findFirst()
                        .ifPresent(dco -> {
                            dco.updateFrom(directContractOutsourcingContractInfo, contract, company, labor);
                        });
            }
        }
    }

    @Transactional
    public void updateDailyReportDirectContractOutsourcings(
            final DailyReportSearchRequest searchRequest,
            final DailyReportDirectContractOutsourcingUpdateRequest request,
            final Long userId) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 중복 laborId + unitPrice 체크 (임시 인력 제외)
        final Set<String> directContractOutsourcingKeys = new HashSet<>();
        for (final DailyReportDirectContractOutsourcingUpdateRequest.DirectContractOutsourcingUpdateInfo directContractOutsourcing : request
                .directContractOutsourcings()) {
            if (!Boolean.TRUE.equals(directContractOutsourcing.isTemporary())
                    && directContractOutsourcing.laborId() != null) {
                final String key = directContractOutsourcing.laborId() + "_" + directContractOutsourcing.unitPrice();
                if (!directContractOutsourcingKeys.add(key)) {
                    throw new IllegalArgumentException(
                            ValidationMessages.DAILY_REPORT_DIRECT_CONTRACT_DUPLICATE_LABOR_ID);
                }
            }
        }

        // EntitySyncUtils.syncList를 사용하여 직영/용역 용역 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getDirectContractOutsourcings(), request.directContractOutsourcings(), (
                final DailyReportDirectContractOutsourcingUpdateRequest.DirectContractOutsourcingUpdateInfo dto) -> {
            Labor labor;
            // 임시 인력인 경우 새로운 인력을 생성
            if (Boolean.TRUE.equals(dto.isTemporary())) {
                labor = createTemporaryLabor(dto.temporaryLaborName(), dto.unitPrice(), userId, LaborType.OUTSOURCING,
                        dto.outsourcingCompanyId());
            } else {
                // 기존 인력 검색
                labor = laborService.getLaborByIdOrThrow(dto.laborId());

                labor.updatePreviousDailyWage(dto.unitPrice());
            }

            final OutsourcingCompany outsourcingCompany = dto.outsourcingCompanyId() != null
                    ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId())
                    : null;

            return DailyReportDirectContractOutsourcing.builder()
                    .dailyReport(dailyReport)
                    .outsourcingCompany(outsourcingCompany)
                    .labor(labor)
                    .position(dto.position())
                    .workContent(dto.workContent())
                    .unitPrice(dto.unitPrice())
                    .workQuantity(dto.workQuantity())
                    .memo(dto.memo())
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .build();
        });

        // company와 labor 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportDirectContractOutsourcingUpdateRequest.DirectContractOutsourcingUpdateInfo directContractOutsourcingInfo : request
                .directContractOutsourcings()) {
            if (directContractOutsourcingInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany company =
                        directContractOutsourcingInfo.outsourcingCompanyId() != null
                                ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(
                                        directContractOutsourcingInfo.outsourcingCompanyId())
                                : null;
                final Labor labor = (!Boolean.TRUE.equals(directContractOutsourcingInfo.isTemporary())
                        && directContractOutsourcingInfo.laborId() != null)
                                ? laborService.getLaborByIdOrThrow(directContractOutsourcingInfo.laborId())
                                : null;

                // 기존 엔티티만 찾아서 company와 labor 설정 (ID가 null이 아닌 것만)
                dailyReport.getDirectContractOutsourcings()
                        .stream()
                        .filter(dco -> dco.getId() != null && dco.getId().equals(directContractOutsourcingInfo.id()))
                        .findFirst()
                        .ifPresent(dco -> {
                            dco.updateFrom(directContractOutsourcingInfo, labor, company);
                        });
            }
        }

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        savedDailyReport.updateAllAggregatedData();
        dailyReportRepository.save(savedDailyReport);

        // 노무비 명세서 동기화 (마감 상태인 경우에만 실행)
        if (savedDailyReport.getStatus() == DailyReportStatus.COMPLETED
                || savedDailyReport.getStatus() == DailyReportStatus.AUTO_COMPLETED) {
            laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport, userId);
        }
    }

    @Transactional
    public void updateDailyReportOutsourcings(
            final DailyReportSearchRequest searchRequest,
            final DailyReportOutsourcingUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 외주 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getOutsourcings(), request.outsourcings(), (
                final DailyReportOutsourcingUpdateRequest.OutsourcingUpdateInfo dto) -> {
            return DailyReportOutsourcing.builder()
                    .dailyReport(dailyReport)
                    .outsourcingCompany(
                            outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId()))
                    .outsourcingCompanyContractWorker(
                            getOutsourcingCompanyContractWorkerByIdOrThrow(dto.outsourcingCompanyContractWorkerId()))
                    .category(dto.category())
                    .workContent(dto.workContent())
                    .workQuantity(dto.workQuantity())
                    .memo(dto.memo())
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .build();
        });

        // company와 outsourcingCompanyContractWorker 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportOutsourcingUpdateRequest.OutsourcingUpdateInfo outsourcingInfo : request.outsourcings()) {
            if (outsourcingInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany company =
                        outsourcingInfo.outsourcingCompanyId() != null
                                ? outsourcingCompanyService
                                        .getOutsourcingCompanyByIdOrThrow(outsourcingInfo.outsourcingCompanyId())
                                : null;
                final OutsourcingCompanyContractWorker outsourcingCompanyContractWorker =
                        outsourcingInfo.outsourcingCompanyContractWorkerId() != null
                                ? getOutsourcingCompanyContractWorkerByIdOrThrow(
                                        outsourcingInfo.outsourcingCompanyContractWorkerId())
                                : null;

                // 기존 엔티티만 찾아서 company와 outsourcingCompanyContractWorker 설정 (ID가 null이 아닌 것만)
                dailyReport.getOutsourcings()
                        .stream()
                        .filter(os -> os.getId() != null && os.getId().equals(outsourcingInfo.id()))
                        .findFirst()
                        .ifPresent(os -> {
                            os.updateFrom(outsourcingInfo, company, outsourcingCompanyContractWorker);
                        });
            }
        }

        dailyReportRepository.save(dailyReport);
        dailyReport.updateAllAggregatedData();
        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void updateDailyReportEquipments(
            final DailyReportSearchRequest searchRequest,
            final DailyReportEquipmentUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 장비 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getOutsourcingEquipments(), request.outsourcingEquipments(), (
                final DailyReportEquipmentUpdateRequest.EquipmentUpdateInfo dto) -> {
            OutsourcingCompanyContractEquipment equipmentEntity = null;
            if (dto.outsourcingCompanyContractEquipmentId() != null) {
                equipmentEntity =
                        getOutsourcingCompanyContractEquipmentByIdOrThrow(dto.outsourcingCompanyContractEquipmentId());
                // 장비의 이전단가 업데이트
                if (equipmentEntity != null && dto.unitPrice() != null) {
                    equipmentEntity.updatePreviousUnitPrice(dto.unitPrice());
                }
            }

            final DailyReportOutsourcingEquipment equipment = DailyReportOutsourcingEquipment.builder()
                    .dailyReport(dailyReport)
                    .outsourcingCompany(
                            outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId()))
                    .outsourcingCompanyContractDriver(dto.outsourcingCompanyContractDriverId() != null
                            ? getOutsourcingCompanyContractDriverByIdOrThrow(dto.outsourcingCompanyContractDriverId())
                            : null)
                    .outsourcingCompanyContractEquipment(equipmentEntity)
                    .workContent(dto.workContent())
                    .unitPrice(dto.unitPrice())
                    .workHours(dto.workHours())
                    .memo(dto.memo())
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .build();

            // 서브 장비 추가
            if (dto.subEquipments() != null) {
                for (final DailyReportEquipmentUpdateRequest.OutsourcingCompanyContractSubEquipmentUpdateInfo subEquipmentDto : dto
                        .subEquipments()) {
                    // 서브장비 조회 및 이전단가 업데이트
                    OutsourcingCompanyContractSubEquipment subEquipmentEntity = null;
                    if (subEquipmentDto.outsourcingCompanyContractSubEquipmentId() != null) {
                        subEquipmentEntity = outsourcingCompanyContractService
                                .getSubEquipmentByIdOrThrow(subEquipmentDto.outsourcingCompanyContractSubEquipmentId());
                        if (subEquipmentEntity != null && subEquipmentDto.unitPrice() != null) {
                            subEquipmentEntity.updatePreviousUnitPrice(subEquipmentDto.unitPrice());
                        }
                    }

                    final DailyReportOutsourcingEquipmentSubEquipment subEquipment =
                            DailyReportOutsourcingEquipmentSubEquipment.builder()
                                    .dailyReportOutsourcingEquipment(equipment)
                                    .outsourcingCompanyContractSubEquipment(subEquipmentEntity != null
                                            ? subEquipmentEntity
                                            : OutsourcingCompanyContractSubEquipment.builder()
                                                    .id(subEquipmentDto.outsourcingCompanyContractSubEquipmentId())
                                                    .build())
                                    .workContent(subEquipmentDto.workContent())
                                    .unitPrice(subEquipmentDto.unitPrice())
                                    .workHours(subEquipmentDto.workHours())
                                    .memo(subEquipmentDto.memo())
                                    .build();
                    equipment.getSubEquipments().add(subEquipment);
                }
            }

            return equipment;
        });

        // outsourcingCompany, outsourcingCompanyContractDriver,
        // outsourcingCompanyContractEquipment 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportEquipmentUpdateRequest.EquipmentUpdateInfo equipmentInfo : request
                .outsourcingEquipments()) {
            if (equipmentInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany outsourcingCompany =
                        equipmentInfo.outsourcingCompanyId() != null ? outsourcingCompanyService
                                .getOutsourcingCompanyByIdOrThrow(equipmentInfo.outsourcingCompanyId()) : null;
                final OutsourcingCompanyContractDriver outsourcingCompanyContractDriver =
                        equipmentInfo.outsourcingCompanyContractDriverId() != null
                                ? getOutsourcingCompanyContractDriverByIdOrThrow(
                                        equipmentInfo.outsourcingCompanyContractDriverId())
                                : null;
                final OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment =
                        equipmentInfo.outsourcingCompanyContractEquipmentId() != null
                                ? getOutsourcingCompanyContractEquipmentByIdOrThrow(
                                        equipmentInfo.outsourcingCompanyContractEquipmentId())
                                : null;

                // 장비의 이전단가 업데이트
                if (outsourcingCompanyContractEquipment != null && equipmentInfo.unitPrice() != null) {
                    outsourcingCompanyContractEquipment.updatePreviousUnitPrice(equipmentInfo.unitPrice());
                }

                // 기존 엔티티만 찾아서 outsourcingCompany, outsourcingCompanyContractDriver,
                // outsourcingCompanyContractEquipment 설정 (ID가 null이 아닌 것만)
                dailyReport.getOutsourcingEquipments()
                        .stream()
                        .filter(eq -> eq.getId() != null && eq.getId().equals(equipmentInfo.id()))
                        .findFirst()
                        .ifPresent(eq -> {
                            eq.updateFrom(equipmentInfo, outsourcingCompany, outsourcingCompanyContractDriver,
                                    outsourcingCompanyContractEquipment);

                            // 서브장비의 이전단가 업데이트
                            if (equipmentInfo.subEquipments() != null) {
                                for (final DailyReportEquipmentUpdateRequest.OutsourcingCompanyContractSubEquipmentUpdateInfo subEquipmentInfo : equipmentInfo
                                        .subEquipments()) {
                                    if (subEquipmentInfo.outsourcingCompanyContractSubEquipmentId() != null
                                            && subEquipmentInfo.unitPrice() != null) {
                                        final OutsourcingCompanyContractSubEquipment subEquipmentEntity =
                                                outsourcingCompanyContractService.getSubEquipmentByIdOrThrow(
                                                        subEquipmentInfo.outsourcingCompanyContractSubEquipmentId());
                                        if (subEquipmentEntity != null) {
                                            subEquipmentEntity.updatePreviousUnitPrice(subEquipmentInfo.unitPrice());
                                        }
                                    }
                                }
                            }
                        });
            }
        }

        dailyReportRepository.save(dailyReport);
        dailyReport.updateAllAggregatedData();
        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void updateDailyReportOutsourcingConstructions(
            final DailyReportSearchRequest searchRequest,
            final DailyReportOutsourcingConstructionUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 공사 그룹 정보 동기화 (2depth 구조)
        EntitySyncUtils.syncList(dailyReport.getConstructionGroups(), request.constructionGroups(), (
                final DailyReportOutsourcingConstructionUpdateRequest.ConstructionGroupUpdateInfo dto) -> {
            final OutsourcingCompany outsourcingCompany =
                    outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId());

            final OutsourcingCompanyContractConstructionGroup contractConstructionGroup =
                    outsourcingCompanyContractConstructionService
                            .getOutsourcingCompanyContractConstructionGroupByIdOrThrow(
                                    dto.outsourcingCompanyContractConstructionGroupId());

            final DailyReportOutsourcingConstructionGroup group = DailyReportOutsourcingConstructionGroup.builder()
                    .dailyReport(dailyReport)
                    .outsourcingCompany(outsourcingCompany)
                    .outsourcingCompanyContractConstructionGroup(contractConstructionGroup)
                    .build();

            // 공사항목 추가
            if (dto.items() != null) {
                for (final DailyReportOutsourcingConstructionUpdateRequest.ConstructionItemUpdateInfo itemDto : dto
                        .items()) {
                    final OutsourcingCompanyContractConstruction contractConstruction =
                            outsourcingCompanyContractConstructionService
                                    .getOutsourcingCompanyContractConstructionByIdOrThrow(
                                            itemDto.outsourcingCompanyContractConstructionId());

                    final DailyReportOutsourcingConstruction construction = DailyReportOutsourcingConstruction.builder()
                            .outsourcingConstructionGroup(group)
                            .outsourcingCompanyContractConstruction(contractConstruction)
                            .quantity(itemDto.quantity())
                            .fileUrl(itemDto.fileUrl())
                            .originalFileName(itemDto.originalFileName())
                            .memo(itemDto.memo())
                            .build();

                    group.getConstructions().add(construction);
                }
            }

            return group;
        });

        // 기존 공사 그룹 업데이트
        for (final DailyReportOutsourcingConstructionUpdateRequest.ConstructionGroupUpdateInfo groupInfo : request
                .constructionGroups()) {
            if (groupInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany outsourcingCompany =
                        outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(groupInfo.outsourcingCompanyId());

                final OutsourcingCompanyContractConstructionGroup contractConstructionGroup =
                        outsourcingCompanyContractConstructionService
                                .getOutsourcingCompanyContractConstructionGroupByIdOrThrow(
                                        groupInfo.outsourcingCompanyContractConstructionGroupId());

                // 기존 공사 그룹 엔티티 찾아서 업데이트
                dailyReport.getConstructionGroups()
                        .stream()
                        .filter(group -> group.getId() != null && group.getId().equals(groupInfo.id()))
                        .findFirst()
                        .ifPresent(group -> group.updateFrom(groupInfo, outsourcingCompany, contractConstructionGroup,
                                outsourcingCompanyContractConstructionService));
            }
        }

        dailyReportRepository.save(dailyReport);
        dailyReport.updateAllAggregatedData();
        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void updateDailyReportFiles(
            final DailyReportSearchRequest searchRequest,
            final DailyReportFileUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 파일 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getFiles(), request.files(), (
                final DailyReportFileUpdateRequest.FileUpdateInfo dto) -> {
            return DailyReportFile.builder()
                    .dailyReport(dailyReport)
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .description(dto.description())
                    .memo(dto.memo())
                    .build();
        });

        // 기존 엔티티 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportFileUpdateRequest.FileUpdateInfo fileInfo : request.files()) {
            if (fileInfo.id() != null) { // ID가 있는 것만 처리
                // 기존 엔티티만 찾아서 업데이트 (ID가 null이 아닌 것만)
                dailyReport.getFiles()
                        .stream()
                        .filter(file -> file.getId() != null && file.getId().equals(fileInfo.id()))
                        .findFirst()
                        .ifPresent(file -> file.updateFrom(fileInfo));
            }
        }

        dailyReportRepository.save(dailyReport);
        dailyReport.updateAllAggregatedData();
        dailyReportRepository.save(dailyReport);
    }

    /**
     * 출역일보 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<DailyReportListResponse> searchDailyReports(
            final Long userId,
            final DailyReportListSearchRequest request,
            final Pageable pageable) {
        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        return dailyReportRepository.findAllBySearchConditions(request, pageable, accessibleSiteIds);
    }

    @Transactional
    public void completeDailyReport(
            final DailyReportSearchRequest searchRequest,
            final Long userId) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());
        final DailyReport dailyReport =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 수동 마감 처리
        dailyReport.complete();
        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        // 노무비 명세서 동기화 (마감 시에만 실행)
        laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport, userId);
    }

    private OutsourcingCompanyContractWorker getOutsourcingCompanyContractWorkerByIdOrThrow(
            final Long workerId) {
        return outsourcingCompanyContractWorkerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_WORKER_NOT_FOUND));
    }

    private OutsourcingCompanyContractDriver getOutsourcingCompanyContractDriverByIdOrThrow(
            final Long driverId) {
        return outsourcingCompanyContractDriverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_DRIVER_NOT_FOUND));
    }

    private OutsourcingCompanyContractEquipment getOutsourcingCompanyContractEquipmentByIdOrThrow(
            final Long equipmentId) {
        return outsourcingCompanyContractEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_NOT_FOUND));
    }

    private DailyReport getDailyReportByIdOrThrow(
            final Long id) {
        return dailyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));
    }

    /**
     * 출역일보 검색 요청으로 출역일보를 조회합니다.
     */
    private DailyReport getDailyReportBySearchRequest(
            final DailyReportSearchRequest searchRequest) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        return dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));
    }

    /**
     * 출역일보 수정 권한을 검증합니다.
     * - 본사 직원(isHeadOffice=true): 언제든 수정 가능
     * - 현장 직원(isHeadOffice=false): PENDING 상태인 경우에만 수정 가능
     *
     * @param dailyReport 출역일보
     * @throws ResponseStatusException 수정 권한이 없을 때
     */
    private void validateDailyReportEditPermission(
            final DailyReport dailyReport) {
        // 현재 사용자 정보 조회
        final CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User currentUser = userService.getUserEntity(userDetails.getUserId());

        // 본사 직원인 경우 언제든 수정 가능
        if (currentUser.isHeadOffice()) {
            return;
        }

        // 현장 직원인 경우 PENDING 상태가 아니면 수정 불가
        if (dailyReport.getStatus() != DailyReportStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ValidationMessages.DAILY_REPORT_EDIT_NOT_ALLOWED);
        }
    }

    /**
     * 출역일보 삭제
     * 마감된 출역일보는 삭제할 수 없습니다.
     */
    @Transactional
    public void deleteDailyReports(
            final List<Long> dailyReportIds) {
        final List<DailyReport> dailyReports = new ArrayList<>();

        for (final Long id : dailyReportIds) {
            final DailyReport dailyReport = getDailyReportByIdOrThrow(id);

            // 마감된 출역일보는 삭제 불가
            if (dailyReport.getStatus() == DailyReportStatus.COMPLETED
                    || dailyReport.getStatus() == DailyReportStatus.AUTO_COMPLETED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        ValidationMessages.DAILY_REPORT_DELETE_NOT_ALLOWED);
            }

            // 출역일보 삭제 (soft delete)
            dailyReport.markAsDeleted();
            dailyReports.add(dailyReport);
        }

        dailyReportRepository.saveAll(dailyReports);
    }

    /**
     * 임시 인력 생성 메서드
     */
    private Labor createTemporaryLabor(
            final String temporaryLaborName,
            final Long unitPrice,
            final Long userId,
            final LaborType laborType,
            final Long outsourcingCompanyId) {
        // 임시 인력 이름이 필수
        if (temporaryLaborName == null || temporaryLaborName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    ValidationMessages.TEMPORARY_LABOR_NAME_REQUIRED);
        }

        // 외주업체 조회
        final OutsourcingCompany outsourcingCompany = outsourcingCompanyId != null
                ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(outsourcingCompanyId)
                : null;

        // 임시 인력 생성
        final Labor temporaryLabor = Labor.builder()
                .name(temporaryLaborName)
                .type(laborType)
                .isTemporary(true)
                .dailyWage(unitPrice)
                .outsourcingCompany(outsourcingCompany)
                .build();

        final Labor labor = laborRepository.save(temporaryLabor);

        final LaborChangeHistory changeHistory = LaborChangeHistory.builder()
                .labor(labor)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(userService.getUserByIdOrThrow(userId))
                .build();
        laborChangeHistoryRepository.save(changeHistory);

        return labor;
    }

    /**
     * 출역일보 외주(공사) 그룹 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 외주(공사) 그룹 정보 슬라이스
     */
    public Slice<DailyReportOutsourcingConstructionGroupResponse> searchDailyReportOutsourcingConstructions(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportOutsourcingConstructionGroupResponse 슬라이스로 변환
        // (2depth 구조)
        // 각 DailyReport의 공사 그룹들을 개별 항목으로 변환
        final List<DailyReportOutsourcingConstructionGroupResponse> allConstructionGroups = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportOutsourcingConstructionGroup group : dailyReport.getConstructionGroups()) {
                allConstructionGroups.add(DailyReportOutsourcingConstructionGroupResponse.from(group));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportOutsourcingConstructionGroupResponse> groupSlice = new SliceImpl<>(
                allConstructionGroups,
                pageable,
                dailyReportSlice.hasNext());

        return groupSlice;
    }

    /**
     * 출역일보 작업 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 작업 정보 슬라이스
     */
    public Slice<DailyReportWorkResponse> searchDailyReportWorks(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportWorkResponse 슬라이스로 변환
        // 각 DailyReport의 작업들을 개별 항목으로 변환
        final List<DailyReportWorkResponse> allWorks = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportWork work : dailyReport.getWorks()) {
                allWorks.add(DailyReportWorkResponse.from(work));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportWorkResponse> workSlice = new SliceImpl<>(
                allWorks,
                pageable,
                dailyReportSlice.hasNext());

        return workSlice;
    }

    /**
     * 출역일보 작업 디테일 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 작업 디테일 정보 슬라이스
     */
    public Slice<DailyReportWorkDetailResponse> searchDailyReportWorkDetails(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportWorkDetailResponse 슬라이스로 변환
        // 각 DailyReport의 작업 디테일들을 개별 항목으로 변환
        final List<DailyReportWorkDetailResponse> allWorkDetails = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportWork work : dailyReport.getWorks()) {
                for (final DailyReportWorkDetail workDetail : work.getWorkDetails()) {
                    allWorkDetails.add(DailyReportWorkDetailResponse.from(workDetail));
                }
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportWorkDetailResponse> workDetailSlice = new SliceImpl<>(
                allWorkDetails,
                pageable,
                dailyReportSlice.hasNext());

        return workDetailSlice;
    }

    /**
     * 출역일보 주요공정 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 주요공정 정보 슬라이스
     */
    public Slice<DailyReportMainProcessResponse> searchDailyReportMainProcesses(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportMainProcessResponse 슬라이스로 변환
        // 각 DailyReport의 주요공정들을 개별 항목으로 변환
        final List<DailyReportMainProcessResponse> allMainProcesses = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportMainProcess mainProcess : dailyReport.getMainProcesses()) {
                allMainProcesses.add(DailyReportMainProcessResponse.from(mainProcess));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportMainProcessResponse> mainProcessSlice = new SliceImpl<>(
                allMainProcesses,
                pageable,
                dailyReportSlice.hasNext());

        return mainProcessSlice;
    }

    /**
     * 출역일보 투입현황 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 투입현황 정보 슬라이스
     */
    public Slice<DailyReportInputStatusResponse> searchDailyReportInputStatuses(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportInputStatusResponse 슬라이스로 변환
        // 각 DailyReport의 투입현황들을 개별 항목으로 변환
        final List<DailyReportInputStatusResponse> allInputStatuses = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportInputStatus inputStatus : dailyReport.getInputStatuses()) {
                allInputStatuses.add(DailyReportInputStatusResponse.from(inputStatus));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportInputStatusResponse> inputStatusSlice = new SliceImpl<>(
                allInputStatuses,
                pageable,
                dailyReportSlice.hasNext());

        return inputStatusSlice;
    }

    /**
     * 출역일보 자재현황 정보를 슬라이스로 조회합니다.
     *
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 자재현황 정보 슬라이스
     */
    public Slice<DailyReportMaterialStatusResponse> searchDailyReportMaterialStatuses(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice =
                dailyReportRepository.findBySiteAndSiteProcessAndReportDateAndWeatherOptional(site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), null, pageable);

        // DailyReport 슬라이스를 DailyReportMaterialStatusResponse 슬라이스로 변환
        // 각 DailyReport의 자재현황들을 개별 항목으로 변환
        final List<DailyReportMaterialStatusResponse> allMaterialStatuses = new ArrayList<>();

        for (final DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (final DailyReportMaterialStatus materialStatus : dailyReport.getMaterialStatuses()) {
                allMaterialStatuses.add(DailyReportMaterialStatusResponse.from(materialStatus));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        final Slice<DailyReportMaterialStatusResponse> materialStatusSlice = new SliceImpl<>(
                allMaterialStatuses,
                pageable,
                dailyReportSlice.hasNext());

        return materialStatusSlice;
    }

    /**
     * 출역일보 작업 정보를 수정합니다.
     *
     * @param searchRequest 출역일보 검색 요청
     * @param request       작업 수정 요청
     */
    @Transactional
    public void updateDailyReportWork(
            final DailyReportSearchRequest searchRequest,
            final DailyReportWorkUpdateRequest request) {
        // 출역일보 조회
        final DailyReport dailyReport = getDailyReportBySearchRequest(searchRequest);

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 작업 정보 동기화 (디테일 포함)
        EntitySyncUtils.syncList(dailyReport.getWorks(), request.works(), (
                final DailyReportWorkUpdateRequest.WorkUpdateInfo dto) -> {
            final DailyReportWork work = DailyReportWork.builder()
                    .dailyReport(dailyReport)
                    .workName(dto.workName())
                    .isToday(dto.isToday())
                    .build();

            // 디테일도 함께 생성
            for (final DailyReportWorkUpdateRequest.WorkUpdateInfo.WorkDetailUpdateInfo detailDto : dto.workDetails()) {
                final DailyReportWorkDetail workDetail = DailyReportWorkDetail.builder()
                        .work(work)
                        .content(detailDto.content())
                        .personnelAndEquipment(detailDto.personnelAndEquipment())
                        .build();
                work.getWorkDetails().add(workDetail);
            }

            return work;
        });

        dailyReportRepository.save(dailyReport);
    }

    /**
     * 출역일보 주요공정 정보를 수정합니다.
     *
     * @param searchRequest 출역일보 검색 요청
     * @param request       주요공정 수정 요청
     */
    @Transactional
    public void updateDailyReportMainProcess(
            final DailyReportSearchRequest searchRequest,
            final DailyReportMainProcessUpdateRequest request) {
        // 출역일보 조회
        final DailyReport dailyReport = getDailyReportBySearchRequest(searchRequest);

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 주요공정 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getMainProcesses(), request.mainProcesses(), (
                final DailyReportMainProcessUpdateRequest.MainProcessUpdateInfo dto) -> {
            return DailyReportMainProcess.builder()
                    .dailyReport(dailyReport)
                    .process(dto.process())
                    .unit(dto.unit())
                    .contractAmount(dto.contractAmount())
                    .previousDayAmount(dto.previousDayAmount())
                    .todayAmount(dto.todayAmount())
                    .cumulativeAmount(dto.cumulativeAmount())
                    .processRate(dto.processRate())
                    .build();
        });

        dailyReportRepository.save(dailyReport);
    }

    /**
     * 출역일보 투입현황 정보를 수정합니다.
     *
     * @param searchRequest 출역일보 검색 요청
     * @param request       투입현황 수정 요청
     */
    @Transactional
    public void updateDailyReportInputStatus(
            final DailyReportSearchRequest searchRequest,
            final DailyReportInputStatusUpdateRequest request) {
        // 출역일보 조회
        final DailyReport dailyReport = getDailyReportBySearchRequest(searchRequest);

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 투입현황 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getInputStatuses(), request.inputStatuses(), (
                final DailyReportInputStatusUpdateRequest.InputStatusUpdateInfo dto) -> {
            return DailyReportInputStatus.builder()
                    .dailyReport(dailyReport)
                    .category(dto.category())
                    .previousDayCount(dto.previousDayCount())
                    .todayCount(dto.todayCount())
                    .cumulativeCount(dto.cumulativeCount())
                    .type(dto.type())
                    .build();
        });

        dailyReportRepository.save(dailyReport);
    }

    /**
     * 출역일보 자재현황 정보를 수정합니다.
     *
     * @param searchRequest 출역일보 검색 요청
     * @param request       자재현황 수정 요청
     */
    @Transactional
    public void updateDailyReportMaterialStatus(
            final DailyReportSearchRequest searchRequest,
            final DailyReportMaterialStatusUpdateRequest request) {
        // 출역일보 조회
        final DailyReport dailyReport = getDailyReportBySearchRequest(searchRequest);

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 자재현황 정보 동기화
        EntitySyncUtils.syncList(dailyReport.getMaterialStatuses(), request.materialStatuses(), (
                final DailyReportMaterialStatusUpdateRequest.MaterialStatusUpdateInfo dto) -> {
            return DailyReportMaterialStatus.builder()
                    .dailyReport(dailyReport)
                    .materialName(dto.materialName())
                    .unit(dto.unit())
                    .plannedAmount(dto.plannedAmount())
                    .previousDayAmount(dto.previousDayAmount())
                    .todayAmount(dto.todayAmount())
                    .cumulativeAmount(dto.cumulativeAmount())
                    .remainingAmount(dto.remainingAmount())
                    .type(dto.type())
                    .build();
        });

        dailyReportRepository.save(dailyReport);
    }

}
