package com.lineinc.erp.api.server.domain.dailyreport.service.v1;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEvidenceFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFuel;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcing;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;
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
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractDriverRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractWorkerRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEvidenceFileUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEmployeeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEvidenceFileResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFileResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFuelResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportOutsourcingResponse;
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

    @Transactional
    public void createDailyReport(final DailyReportCreateRequest request, final Long userId) {
        // 현장 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 같은 날짜, 현장, 공정에 대한 출역일보 중복 체크
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(request.reportDate());
        if (dailyReportRepository.existsBySiteAndSiteProcessAndReportDate(
                site.getId(), siteProcess.getId(), reportDate)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    ValidationMessages.DAILY_REPORT_ALREADY_EXISTS);
        }

        // 출역일보 생성
        final DailyReport dailyReport = DailyReport.builder()
                .site(site)
                .siteProcess(siteProcess)
                .reportDate(reportDate)
                .weather(request.weather())
                .memo(request.memo())
                .build();

        // 직원 출역 정보 추가
        if (request.employees() != null) {
            // 중복 laborId 체크
            validateEmployeeDuplicates(request.employees());

            for (final DailyReportEmployeeCreateRequest employeeRequest : request.employees()) {
                final Labor labor = laborService.getLaborByIdOrThrow(employeeRequest.laborId());

                // 정규직원만 직원 출역 정보에 추가 가능
                if (labor.getType() != LaborType.REGULAR_EMPLOYEE) {
                    throw new IllegalArgumentException(
                            ValidationMessages.DAILY_REPORT_EMPLOYEE_MUST_BE_REGULAR);
                }

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

        // 직영/계약직 출역 정보 추가
        if (request.directContracts() != null) {
            // 중복 laborId + unitPrice 체크 (임시 인력 제외)
            validateDirectContractDuplicates(request.directContracts());

            for (final DailyReportDirectContractCreateRequest directContractRequest : request.directContracts()) {
                final OutsourcingCompany company = directContractRequest.outsourcingCompanyId() != null
                        ? outsourcingCompanyService
                                .getOutsourcingCompanyByIdOrThrow(directContractRequest.outsourcingCompanyId())
                        : null;

                Labor labor;
                // 임시 인력인 경우 새로운 인력을 생성
                if (Boolean.TRUE.equals(directContractRequest.isTemporary())) {
                    labor = createTemporaryLabor(directContractRequest.temporaryLaborName(),
                            directContractRequest.unitPrice(), userId);
                } else {
                    // 기존 인력 검색
                    labor = laborService.getLaborByIdOrThrow(directContractRequest.laborId());

                    // 직영/계약직 출역 정보에는 DIRECT_CONTRACT 또는 ETC 타입만 허용
                    if (labor.getType() != LaborType.DIRECT_CONTRACT && labor.getType() != LaborType.ETC) {
                        throw new IllegalArgumentException(
                                ValidationMessages.DAILY_REPORT_DIRECT_CONTRACT_INVALID_TYPE);
                    }

                }

                final DailyReportDirectContract directContract = DailyReportDirectContract.builder()
                        .dailyReport(dailyReport)
                        .outsourcingCompany(company)
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

        // 외주 출역 정보 추가
        if (request.outsourcings() != null)

        {
            for (final DailyReportOutsourcingCreateRequest outsourcingRequest : request.outsourcings()) {
                final OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(outsourcingRequest.outsourcingCompanyId());
                final OutsourcingCompanyContractWorker worker = getOutsourcingCompanyContractWorkerByIdOrThrow(
                        outsourcingRequest.outsourcingCompanyContractWorkerId());

                final DailyReportOutsourcing outsourcing = DailyReportOutsourcing.builder()
                        .dailyReport(dailyReport)
                        .outsourcingCompany(company)
                        .outsourcingCompanyContractWorker(worker)
                        .category(outsourcingRequest.category())
                        .workContent(outsourcingRequest.workContent())
                        .workQuantity(outsourcingRequest.workQuantity())
                        .memo(outsourcingRequest.memo())
                        .fileUrl(outsourcingRequest.fileUrl())
                        .originalFileName(outsourcingRequest.originalFileName())
                        .build();

                dailyReport.getOutsourcings().add(outsourcing);
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
                    request.fuelInfos().stream()
                            .map(fuelInfoRequest -> new FuelInfoCreateRequest(
                                    fuelInfoRequest.outsourcingCompanyId(),
                                    fuelInfoRequest.driverId(),
                                    fuelInfoRequest.equipmentId(),
                                    fuelInfoRequest.fuelType(),
                                    fuelInfoRequest.fuelAmount(),
                                    fuelInfoRequest.fileUrl(),
                                    fuelInfoRequest.originalFileName(),
                                    fuelInfoRequest.memo()))
                            .toList());
            final FuelAggregation fuelAggregation = fuelAggregationService
                    .createFuelAggregation(fuelAggregationRequest, userId);

            // FuelAggregation만 연결하는 DailyReportFuel 생성
            final DailyReportFuel fuel = DailyReportFuel.builder()
                    .dailyReport(dailyReport)
                    .fuelAggregation(fuelAggregation)
                    .build();

            dailyReport.getFuels().add(fuel);
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

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        // 노무비 명세서 동기화
        laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport);
    }

    public DailyReport getDailyReportById(final Long id) {
        return getDailyReportByIdOrThrow(id);
    }

    /**
     * 출역일보 상세 정보를 조회합니다.
     * 
     * @param request 조회 요청 파라미터 (현장아이디, 공정아이디, 일자)
     * @return 출역일보 상세 정보
     */
    @Transactional(readOnly = true)
    public DailyReportDetailResponse getDailyReportDetail(final DailyReportSearchRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 출역일보 조회
        final DailyReport dailyReport = dailyReportRepository.findBySiteAndSiteProcessAndReportDate(
                site, siteProcess, DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
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
    public void updateDailyReport(final DailyReportSearchRequest searchRequest,
            final DailyReportUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 출역일보 조회
        final DailyReport dailyReport = dailyReportRepository.findBySiteAndSiteProcessAndReportDate(
                site, siteProcess, DateTimeFormatUtils.toUtcStartOfDay(searchRequest.reportDate()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
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
                EntitySyncUtils.syncList(
                        dailyReport.getEvidenceFiles(),
                        evidenceFileRequest.files(),
                        (final DailyReportEvidenceFileUpdateRequest.FileUpdateInfo dto) -> {
                            return DailyReportEvidenceFile.builder()
                                    .dailyReport(dailyReport)
                                    .fileType(evidenceFileRequest.fileType())
                                    .name(dto.name())
                                    .fileUrl(dto.fileUrl())
                                    .originalFileName(dto.originalFileName())
                                    .memo(dto.memo())
                                    .build();
                        });
            }
        }

        dailyReportRepository.save(dailyReport);
    }

    /**
     * 출역일보 직원정보를 슬라이스로 조회합니다.
     * 
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직원정보 슬라이스
     */
    public Slice<DailyReportEmployeeResponse> searchDailyReportEmployees(final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        null, pageable);

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
     * 출역일보 직영/계약직 정보를 슬라이스로 조회합니다.
     * 
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직영/계약직 정보 슬라이스
     */
    public Slice<DailyReportDirectContractResponse> searchDailyReportDirectContracts(
            final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        null, pageable);

        // DailyReport 슬라이스를 DailyReportDirectContractResponse 슬라이스로 변환
        // 각 DailyReport의 직영/계약직들을 개별 항목으로 변환
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
     * 출역일보 외주 정보를 슬라이스로 조회합니다.
     * 
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 외주 정보 슬라이스
     */
    public Slice<DailyReportOutsourcingResponse> searchDailyReportOutsourcings(final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        null, pageable);

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
    public Slice<DailyReportFuelResponse> searchDailyReportFuels(final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // FuelInfo를 직접 페이징으로 조회
        final Slice<FuelInfo> fuelInfoSlice = fuelInfoRepository.findByDailyReportSiteAndProcessAndDate(
                site, siteProcess, DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()), pageable);

        // FuelInfo를 DailyReportFuelResponse로 변환
        final List<DailyReportFuelResponse> fuelResponses = fuelInfoSlice.getContent().stream()
                .map(fuelInfo -> DailyReportFuelResponse.from(fuelInfo))
                .toList();

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
    public Slice<DailyReportEquipmentResponse> searchDailyReportEquipments(final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        null, pageable);

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
    public Slice<DailyReportFileResponse> searchDailyReportFiles(final DailyReportSearchRequest request,
            final Pageable pageable) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        final Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        null, pageable);

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
    public Slice<DailyReportEvidenceFileResponse> searchDailyReportEvidenceFiles(final Long id,
            final DailyReportEvidenceFileType fileType,
            final Pageable pageable) {
        final DailyReport dailyReport = dailyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        final List<DailyReportEvidenceFileResponse> evidenceFiles = dailyReport.getEvidenceFiles().stream()
                .filter(file -> fileType == null || file.getFileType() == fileType)
                .map(DailyReportEvidenceFileResponse::from)
                .toList();

        return new SliceImpl<>(evidenceFiles, pageable, false);
    }

    @Transactional
    public void updateDailyReportEmployees(final DailyReportSearchRequest searchRequest,
            final DailyReportEmployeeUpdateRequest request) {

        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 중복 laborId 체크
        final Set<Long> laborIds = new HashSet<>();
        for (final DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo employee : request.employees()) {
            if (employee.laborId() != null && !laborIds.add(employee.laborId())) {
                throw new IllegalArgumentException(ValidationMessages.DAILY_REPORT_EMPLOYEE_DUPLICATE_LABOR_ID);
            }
        }

        // 정규직원만 직원 출역 정보에 추가 가능
        if (request.employees().stream().anyMatch(employee -> employee.laborId() != null
                && laborService.getLaborByIdOrThrow(employee.laborId()).getType() != LaborType.REGULAR_EMPLOYEE)) {
            throw new IllegalArgumentException(
                    ValidationMessages.DAILY_REPORT_EMPLOYEE_MUST_BE_REGULAR);
        }

        // EntitySyncUtils.syncList를 사용하여 직원정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getEmployees(),
                request.employees(),
                (final DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo dto) -> {
                    return DailyReportEmployee.builder()
                            .dailyReport(dailyReport)
                            .labor(laborService.getLaborByIdOrThrow(dto.laborId()))
                            .workContent(dto.workContent())
                            .workQuantity(dto.workQuantity())
                            .unitPrice(laborService.getLaborByIdOrThrow(dto.laborId())
                                    .getDailyWage())
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
                dailyReport.getEmployees().stream()
                        .filter(emp -> emp.getId() != null && emp.getId().equals(employeeInfo.id()))
                        .findFirst()
                        .ifPresent(emp -> emp.setEntities(labor));
            }
        }

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        // 노무비 명세서 동기화
        laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport);
    }

    @Transactional
    public void updateDailyReportDirectContracts(final DailyReportSearchRequest searchRequest,
            final DailyReportDirectContractUpdateRequest request, final Long userId) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 중복 laborId + unitPrice 체크 (임시 인력 제외)
        final Set<String> directContractKeys = new HashSet<>();
        for (final DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo directContract : request
                .directContracts()) {
            if (!Boolean.TRUE.equals(directContract.isTemporary()) &&
                    directContract.laborId() != null) {
                final String key = directContract.laborId() + "_" + directContract.unitPrice();
                if (!directContractKeys.add(key)) {
                    throw new IllegalArgumentException(
                            ValidationMessages.DAILY_REPORT_DIRECT_CONTRACT_DUPLICATE_LABOR_ID);
                }
            }
        }

        // EntitySyncUtils.syncList를 사용하여 직영/계약직 정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getDirectContracts(),
                request.directContracts(),
                (final DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo dto) -> {
                    Labor labor;
                    // 임시 인력인 경우 새로운 인력을 생성
                    if (Boolean.TRUE.equals(dto.isTemporary())) {
                        labor = createTemporaryLabor(dto.temporaryLaborName(), dto.unitPrice(), userId);
                    } else {
                        // 기존 인력 검색
                        labor = laborService.getLaborByIdOrThrow(dto.laborId());

                        // 직영/계약직 출역 정보에는 DIRECT_CONTRACT 또는 ETC 타입만 허용
                        if (labor.getType() != LaborType.DIRECT_CONTRACT && labor.getType() != LaborType.ETC) {
                            throw new IllegalArgumentException(
                                    ValidationMessages.DAILY_REPORT_DIRECT_CONTRACT_INVALID_TYPE);
                        }

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
                final OutsourcingCompany company = directContractInfo.outsourcingCompanyId() != null
                        ? outsourcingCompanyService
                                .getOutsourcingCompanyByIdOrThrow(directContractInfo.outsourcingCompanyId())
                        : null;
                final Labor labor = directContractInfo.laborId() != null
                        ? laborService.getLaborByIdOrThrow(directContractInfo.laborId())
                        : null;

                // 기존 엔티티만 찾아서 company와 labor 설정 (ID가 null이 아닌 것만)
                dailyReport.getDirectContracts().stream()
                        .filter(dc -> dc.getId() != null && dc.getId().equals(directContractInfo.id()))
                        .findFirst()
                        .ifPresent(dc -> {
                            dc.updateFrom(directContractInfo);
                            dc.setEntities(company, labor);
                        });
            }
        }

        final DailyReport savedDailyReport = dailyReportRepository.save(dailyReport);

        // 노무비 명세서 동기화
        laborPayrollSyncService.syncLaborPayrollFromDailyReport(savedDailyReport);
    }

    @Transactional
    public void updateDailyReportOutsourcings(final DailyReportSearchRequest searchRequest,
            final DailyReportOutsourcingUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 외주 정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getOutsourcings(),
                request.outsourcings(),
                (final DailyReportOutsourcingUpdateRequest.OutsourcingUpdateInfo dto) -> {
                    return DailyReportOutsourcing.builder()
                            .dailyReport(dailyReport)
                            .outsourcingCompany(outsourcingCompanyService
                                    .getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId()))
                            .outsourcingCompanyContractWorker(getOutsourcingCompanyContractWorkerByIdOrThrow(
                                    dto.outsourcingCompanyContractWorkerId()))
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
                final OutsourcingCompany company = outsourcingInfo.outsourcingCompanyId() != null
                        ? outsourcingCompanyService
                                .getOutsourcingCompanyByIdOrThrow(outsourcingInfo.outsourcingCompanyId())
                        : null;
                final OutsourcingCompanyContractWorker outsourcingCompanyContractWorker = outsourcingInfo
                        .outsourcingCompanyContractWorkerId() != null
                                ? getOutsourcingCompanyContractWorkerByIdOrThrow(
                                        outsourcingInfo.outsourcingCompanyContractWorkerId())
                                : null;

                // 기존 엔티티만 찾아서 company와 outsourcingCompanyContractWorker 설정 (ID가 null이 아닌 것만)
                dailyReport.getOutsourcings().stream()
                        .filter(os -> os.getId() != null && os.getId().equals(outsourcingInfo.id()))
                        .findFirst()
                        .ifPresent(os -> {
                            os.updateFrom(outsourcingInfo);
                            os.setEntities(company, outsourcingCompanyContractWorker);
                        });
            }
        }

        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void updateDailyReportEquipments(final DailyReportSearchRequest searchRequest,
            final DailyReportEquipmentUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 장비 정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getOutsourcingEquipments(),
                request.outsourcingEquipments(),
                (final DailyReportEquipmentUpdateRequest.EquipmentUpdateInfo dto) -> {
                    return DailyReportOutsourcingEquipment.builder()
                            .dailyReport(dailyReport)
                            .outsourcingCompany(outsourcingCompanyService
                                    .getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId()))
                            .outsourcingCompanyContractDriver(dto.outsourcingCompanyContractDriverId() != null
                                    ? getOutsourcingCompanyContractDriverByIdOrThrow(
                                            dto.outsourcingCompanyContractDriverId())
                                    : null)
                            .outsourcingCompanyContractEquipment(dto.outsourcingCompanyContractEquipmentId() != null
                                    ? getOutsourcingCompanyContractEquipmentByIdOrThrow(
                                            dto.outsourcingCompanyContractEquipmentId())
                                    : null)
                            .workContent(dto.workContent())
                            .unitPrice(dto.unitPrice())
                            .workHours(dto.workHours())
                            .memo(dto.memo())
                            .fileUrl(dto.fileUrl())
                            .originalFileName(dto.originalFileName())
                            .build();
                });

        // outsourcingCompany, outsourcingCompanyContractDriver,
        // outsourcingCompanyContractEquipment 업데이트를 위해 추가 처리 (한 번만 반복)
        for (final DailyReportEquipmentUpdateRequest.EquipmentUpdateInfo equipmentInfo : request
                .outsourcingEquipments()) {
            if (equipmentInfo.id() != null) { // ID가 있는 것만 처리
                final OutsourcingCompany outsourcingCompany = equipmentInfo.outsourcingCompanyId() != null
                        ? outsourcingCompanyService
                                .getOutsourcingCompanyByIdOrThrow(equipmentInfo.outsourcingCompanyId())
                        : null;
                final OutsourcingCompanyContractDriver outsourcingCompanyContractDriver = equipmentInfo
                        .outsourcingCompanyContractDriverId() != null
                                ? getOutsourcingCompanyContractDriverByIdOrThrow(
                                        equipmentInfo.outsourcingCompanyContractDriverId())
                                : null;
                final OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment = equipmentInfo
                        .outsourcingCompanyContractEquipmentId() != null
                                ? getOutsourcingCompanyContractEquipmentByIdOrThrow(
                                        equipmentInfo.outsourcingCompanyContractEquipmentId())
                                : null;

                // 기존 엔티티만 찾아서 outsourcingCompany, outsourcingCompanyContractDriver,
                // outsourcingCompanyContractEquipment 설정 (ID가 null이 아닌 것만)
                dailyReport.getOutsourcingEquipments().stream()
                        .filter(eq -> eq.getId() != null && eq.getId().equals(equipmentInfo.id()))
                        .findFirst()
                        .ifPresent(eq -> {
                            eq.updateFrom(equipmentInfo);
                            eq.setEntities(outsourcingCompany, outsourcingCompanyContractDriver,
                                    outsourcingCompanyContractEquipment);
                        });
            }
        }

        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void updateDailyReportFiles(final DailyReportSearchRequest searchRequest,
            final DailyReportFileUpdateRequest request) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());

        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // EntitySyncUtils.syncList를 사용하여 파일 정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getFiles(),
                request.files(),
                (final DailyReportFileUpdateRequest.FileUpdateInfo dto) -> {
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
                dailyReport.getFiles().stream()
                        .filter(file -> file.getId() != null && file.getId().equals(fileInfo.id()))
                        .findFirst()
                        .ifPresent(file -> file.updateFrom(fileInfo));
            }
        }

        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void completeDailyReport(final DailyReportSearchRequest searchRequest) {
        // 현장과 공정 조회
        final Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());
        final DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // 출역일보 수정 권한 검증
        validateDailyReportEditPermission(dailyReport);

        // 수동 마감 처리
        dailyReport.complete();
        dailyReportRepository.save(dailyReport);
    }

    private OutsourcingCompanyContractWorker getOutsourcingCompanyContractWorkerByIdOrThrow(final Long workerId) {
        return outsourcingCompanyContractWorkerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_WORKER_NOT_FOUND));
    }

    private OutsourcingCompanyContractDriver getOutsourcingCompanyContractDriverByIdOrThrow(final Long driverId) {
        return outsourcingCompanyContractDriverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_DRIVER_NOT_FOUND));
    }

    private OutsourcingCompanyContractEquipment getOutsourcingCompanyContractEquipmentByIdOrThrow(
            final Long equipmentId) {
        return outsourcingCompanyContractEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_NOT_FOUND));
    }

    private DailyReport getDailyReportByIdOrThrow(final Long id) {
        return dailyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
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
    private void validateDailyReportEditPermission(final DailyReport dailyReport) {
        // 현재 사용자 정보 조회
        final CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        final User currentUser = userService.getUserEntity(userDetails.getUserId());

        // 본사 직원인 경우 언제든 수정 가능
        if (currentUser.isHeadOffice()) {
            return;
        }

        // 현장 직원인 경우 PENDING 상태가 아니면 수정 불가
        if (dailyReport.getStatus() != DailyReportStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ValidationMessages.DAILY_REPORT_EDIT_NOT_ALLOWED);
        }
    }

    /**
     * 임시 인력 생성 메서드
     */
    private Labor createTemporaryLabor(final String temporaryLaborName, final Long unitPrice, final Long userId) {
        // 임시 인력 이름이 필수
        if (temporaryLaborName == null || temporaryLaborName.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.TEMPORARY_LABOR_NAME_REQUIRED);
        }

        // 임시 인력 생성
        final Labor temporaryLabor = Labor.builder()
                .name(temporaryLaborName)
                .type(LaborType.DIRECT_CONTRACT)
                .isTemporary(true)
                .isHeadOffice(true)
                .dailyWage(unitPrice)
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
     * 직원 출역 정보 중복 체크
     */
    private void validateEmployeeDuplicates(final List<? extends DailyReportEmployeeCreateRequest> employees) {
        if (employees == null || employees.isEmpty()) {
            return;
        }

        final Set<Long> laborIds = new HashSet<>();
        for (final DailyReportEmployeeCreateRequest employeeRequest : employees) {
            if (!laborIds.add(employeeRequest.laborId())) {
                throw new IllegalArgumentException(ValidationMessages.DAILY_REPORT_EMPLOYEE_DUPLICATE_LABOR_ID);
            }
        }
    }

    /**
     * 직영/계약직 출역 정보 중복 체크
     */
    private void validateDirectContractDuplicates(
            final List<? extends DailyReportDirectContractCreateRequest> directContracts) {
        if (directContracts == null || directContracts.isEmpty()) {
            return;
        }

        final Set<String> directContractKeys = new HashSet<>();
        for (final DailyReportDirectContractCreateRequest directContractRequest : directContracts) {
            if (!Boolean.TRUE.equals(directContractRequest.isTemporary()) &&
                    directContractRequest.laborId() != null) {
                final String key = directContractRequest.laborId() + "_" + directContractRequest.unitPrice();
                if (!directContractKeys.add(key)) {
                    throw new IllegalArgumentException(
                            ValidationMessages.DAILY_REPORT_DIRECT_CONTRACT_DUPLICATE_LABOR_ID);
                }
            }
        }
    }

}
