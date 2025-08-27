package com.lineinc.erp.api.server.domain.dailyreport.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFile;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFuel;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcing;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.service.LaborService;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractDriverRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractWorkerRepository;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFuelCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEmployeeUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEmployeeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFuelResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportOutsourcingResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportFileResponse;

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
    private final OutsourcingCompanyContractRepository outsourcingCompanyContractRepository;
    private final OutsourcingCompanyContractWorkerRepository outsourcingCompanyContractWorkerRepository;
    private final OutsourcingCompanyContractDriverRepository outsourcingCompanyContractDriverRepository;
    private final OutsourcingCompanyContractEquipmentRepository outsourcingCompanyContractEquipmentRepository;

    @Transactional
    public void createDailyReport(DailyReportCreateRequest request) {
        // 현장 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 같은 날짜, 현장, 공정에 대한 출역일보 중복 체크
        OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(request.reportDate());
        if (dailyReportRepository.existsBySiteAndSiteProcessAndReportDate(
                site.getId(), siteProcess.getId(), reportDate)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    ValidationMessages.DAILY_REPORT_ALREADY_EXISTS);
        }

        // 출역일보 생성
        DailyReport dailyReport = DailyReport.builder()
                .site(site)
                .siteProcess(siteProcess)
                .reportDate(reportDate)
                .weather(request.weather())
                .build();

        // 직원 출역 정보 추가
        if (request.employees() != null) {
            for (DailyReportEmployeeCreateRequest employeeRequest : request.employees()) {
                Labor labor = laborService.getLaborByIdOrThrow(employeeRequest.laborId());

                DailyReportEmployee employee = DailyReportEmployee.builder()
                        .dailyReport(dailyReport)
                        .labor(labor)
                        .workContent(employeeRequest.workContent())
                        .workQuantity(employeeRequest.workQuantity())
                        .memo(employeeRequest.memo())
                        .build();

                dailyReport.getEmployees().add(employee);
            }
        }

        // 직영/계약직 출역 정보 추가
        if (request.directContracts() != null) {
            for (DailyReportDirectContractCreateRequest directContractRequest : request.directContracts()) {
                OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(directContractRequest.outsourcingCompanyId());

                Labor labor;
                // 임시 인력인 경우 새로운 인력을 생성
                if (Boolean.TRUE.equals(directContractRequest.isTemporary())) {
                    labor = createTemporaryLabor(directContractRequest);
                } else {
                    // 기존 인력 검색
                    labor = laborService.getLaborByIdOrThrow(directContractRequest.laborId());
                }

                DailyReportDirectContract directContract = DailyReportDirectContract.builder()
                        .dailyReport(dailyReport)
                        .outsourcingCompany(company)
                        .labor(labor)
                        .position(directContractRequest.position())
                        .workContent(directContractRequest.workContent())
                        .unitPrice(directContractRequest.unitPrice())
                        .workQuantity(directContractRequest.workQuantity())
                        .memo(directContractRequest.memo())
                        .build();

                labor.updatePreviousDailyWage(directContractRequest.unitPrice());
                dailyReport.getDirectContracts().add(directContract);
            }
        }

        // 외주 출역 정보 추가
        if (request.outsourcings() != null)

        {
            for (DailyReportOutsourcingCreateRequest outsourcingRequest : request.outsourcings()) {
                OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(outsourcingRequest.outsourcingCompanyId());
                OutsourcingCompanyContractWorker worker = getOutsourcingCompanyContractWorkerByIdOrThrow(
                        outsourcingRequest.outsourcingCompanyContractWorkerId());

                DailyReportOutsourcing outsourcing = DailyReportOutsourcing.builder()
                        .dailyReport(dailyReport)
                        .company(company)
                        .outsourcingCompanyContractWorker(worker)
                        .category(outsourcingRequest.category())
                        .workContent(outsourcingRequest.workContent())
                        .workQuantity(outsourcingRequest.workQuantity())
                        .memo(outsourcingRequest.memo())
                        .build();

                dailyReport.getOutsourcings().add(outsourcing);
            }
        }

        // 외주업체계약 장비 출역 정보 추가
        if (request.outsourcingEquipments() != null) {
            for (DailyReportOutsourcingEquipmentCreateRequest equipmentRequest : request.outsourcingEquipments()) {
                OutsourcingCompany company = outsourcingCompanyService
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

                DailyReportOutsourcingEquipment outsourcingEquipment = DailyReportOutsourcingEquipment.builder()
                        .dailyReport(dailyReport)
                        .company(company)
                        .outsourcingCompanyContractDriver(driver)
                        .outsourcingCompanyContractEquipment(equipment)
                        .workContent(equipmentRequest.workContent())
                        .unitPrice(equipmentRequest.unitPrice())
                        .workHours(equipmentRequest.workHours())
                        .memo(equipmentRequest.memo())
                        .build();

                dailyReport.getOutsourcingEquipments().add(outsourcingEquipment);
            }
        }

        // 유류 출역 정보 추가
        if (request.fuels() != null) {
            for (DailyReportFuelCreateRequest fuelRequest : request.fuels()) {
                OutsourcingCompanyContract contract = getOutsourcingCompanyContractByIdOrThrow(
                        fuelRequest.outsourcingCompanyContractId());

                OutsourcingCompanyContractDriver driver = null;
                if (fuelRequest.outsourcingCompanyContractDriverId() != null) {
                    driver = getOutsourcingCompanyContractDriverByIdOrThrow(
                            fuelRequest.outsourcingCompanyContractDriverId());
                }

                OutsourcingCompanyContractEquipment equipment = null;
                if (fuelRequest.outsourcingCompanyContractEquipmentId() != null) {
                    equipment = getOutsourcingCompanyContractEquipmentByIdOrThrow(
                            fuelRequest.outsourcingCompanyContractEquipmentId());
                }

                DailyReportFuel fuel = DailyReportFuel.builder()
                        .dailyReport(dailyReport)
                        .outsourcingCompanyContract(contract)
                        .outsourcingCompanyContractDriver(driver)
                        .outsourcingCompanyContractEquipment(equipment)
                        .fuelType(fuelRequest.fuelType())
                        .fuelAmount(fuelRequest.fuelAmount())
                        .memo(fuelRequest.memo())
                        .build();

                dailyReport.getFuels().add(fuel);
            }
        }

        // 현장 사진 정보 추가
        if (request.files() != null) {
            for (DailyReportFileCreateRequest fileRequest : request.files()) {
                DailyReportFile file = DailyReportFile.builder()
                        .dailyReport(dailyReport)
                        .fileUrl(fileRequest.fileUrl())
                        .originalFileName(fileRequest.originalFileName())
                        .description(fileRequest.description())
                        .memo(fileRequest.memo())
                        .build();

                dailyReport.getFiles().add(file);
            }
        }

        dailyReportRepository.save(dailyReport);
    }

    public DailyReport getDailyReportById(Long id) {
        return getDailyReportByIdOrThrow(id);
    }

    /**
     * 출역일보 직원정보를 슬라이스로 조회합니다.
     * 
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 직원정보 슬라이스
     */
    public Slice<DailyReportEmployeeResponse> searchDailyReportEmployees(DailyReportSearchRequest request,
            Pageable pageable) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        request.weather(), pageable);

        // DailyReport 슬라이스를 DailyReportEmployeeResponse 슬라이스로 변환
        // 각 DailyReport의 직원들을 개별 항목으로 변환
        List<DailyReportEmployeeResponse> allEmployees = new ArrayList<>();

        for (DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (DailyReportEmployee employee : dailyReport.getEmployees()) {
                allEmployees.add(DailyReportEmployeeResponse.from(employee));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        Slice<DailyReportEmployeeResponse> employeeSlice = new SliceImpl<>(
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
    public Slice<DailyReportDirectContractResponse> searchDailyReportDirectContracts(DailyReportSearchRequest request,
            Pageable pageable) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        request.weather(), pageable);

        // DailyReport 슬라이스를 DailyReportDirectContractResponse 슬라이스로 변환
        // 각 DailyReport의 직영/계약직들을 개별 항목으로 변환
        List<DailyReportDirectContractResponse> allDirectContracts = new ArrayList<>();

        for (DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                allDirectContracts.add(DailyReportDirectContractResponse.from(directContract));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        Slice<DailyReportDirectContractResponse> directContractSlice = new SliceImpl<>(
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
    public Slice<DailyReportOutsourcingResponse> searchDailyReportOutsourcings(DailyReportSearchRequest request,
            Pageable pageable) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        request.weather(), pageable);

        // DailyReport 슬라이스를 DailyReportOutsourcingResponse 슬라이스로 변환
        // 각 DailyReport의 외주들을 개별 항목으로 변환
        List<DailyReportOutsourcingResponse> allOutsourcings = new ArrayList<>();

        for (DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (DailyReportOutsourcing outsourcing : dailyReport.getOutsourcings()) {
                allOutsourcings.add(DailyReportOutsourcingResponse.from(outsourcing));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        Slice<DailyReportOutsourcingResponse> outsourcingSlice = new SliceImpl<>(
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
    public Slice<DailyReportFuelResponse> searchDailyReportFuels(DailyReportSearchRequest request,
            Pageable pageable) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        request.weather(), pageable);

        // DailyReport 슬라이스를 DailyReportFuelResponse 슬라이스로 변환
        // 각 DailyReport의 유류들을 개별 항목으로 변환
        List<DailyReportFuelResponse> allFuels = new ArrayList<>();

        for (DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (DailyReportFuel fuel : dailyReport.getFuels()) {
                allFuels.add(DailyReportFuelResponse.from(fuel));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        Slice<DailyReportFuelResponse> fuelSlice = new SliceImpl<>(
                allFuels,
                pageable,
                dailyReportSlice.hasNext());

        return fuelSlice;
    }

    /**
     * 출역일보 장비 정보를 슬라이스로 조회합니다.
     * 
     * @param request  조회 요청 파라미터 (현장아이디, 공정아이디, 일자, 날씨)
     * @param pageable 페이징 정보
     * @return 출역일보 장비 정보 슬라이스
     */
    public Slice<DailyReportEquipmentResponse> searchDailyReportEquipments(DailyReportSearchRequest request,
            Pageable pageable) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        request.weather(), pageable);

        // DailyReport 슬라이스를 DailyReportEquipmentResponse 슬라이스로 변환
        // 각 DailyReport의 장비들을 개별 항목으로 변환
        List<DailyReportEquipmentResponse> allEquipments = new ArrayList<>();

        for (DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (DailyReportOutsourcingEquipment equipment : dailyReport.getOutsourcingEquipments()) {
                allEquipments.add(DailyReportEquipmentResponse.from(equipment));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        Slice<DailyReportEquipmentResponse> equipmentSlice = new SliceImpl<>(
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
    public Slice<DailyReportFileResponse> searchDailyReportFiles(DailyReportSearchRequest request,
            Pageable pageable) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 해당 일자와 날씨(선택사항)의 출역일보를 슬라이스로 조회
        Slice<DailyReport> dailyReportSlice = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
                        site, siteProcess,
                        DateTimeFormatUtils.toUtcStartOfDay(request.reportDate()),
                        request.weather(), pageable);

        // DailyReport 슬라이스를 DailyReportFileResponse 슬라이스로 변환
        // 각 DailyReport의 파일들을 개별 항목으로 변환
        List<DailyReportFileResponse> allFiles = new ArrayList<>();

        for (DailyReport dailyReport : dailyReportSlice.getContent()) {
            for (DailyReportFile file : dailyReport.getFiles()) {
                allFiles.add(DailyReportFileResponse.from(file));
            }
        }

        // 슬라이스 정보를 유지하면서 새로운 슬라이스 생성
        Slice<DailyReportFileResponse> fileSlice = new SliceImpl<>(
                allFiles,
                pageable,
                dailyReportSlice.hasNext());

        return fileSlice;
    }

    @Transactional
    public void updateDailyReportEmployees(DailyReportSearchRequest searchRequest,
            DailyReportEmployeeUpdateRequest request) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());
        DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // EntitySyncUtils.syncList를 사용하여 직원정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getEmployees(),
                request.employees(),
                (DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo dto) -> {
                    return DailyReportEmployee.builder()
                            .dailyReport(dailyReport)
                            .labor(laborService.getLaborByIdOrThrow(dto.laborId()))
                            .workContent(dto.workContent())
                            .workQuantity(dto.workQuantity())
                            .memo(dto.memo())
                            .build();
                });

        // labor 업데이트를 위해 추가 처리 (한 번만 반복)
        for (DailyReportEmployeeUpdateRequest.EmployeeUpdateInfo employeeInfo : request.employees()) {
            if (employeeInfo.laborId() != null && employeeInfo.id() != null) { // ID가 있는 것만 처리
                Labor labor = laborService.getLaborByIdOrThrow(employeeInfo.laborId());

                // 기존 엔티티만 찾아서 labor 설정 (ID가 null이 아닌 것만)
                dailyReport.getEmployees().stream()
                        .filter(emp -> emp.getId() != null && emp.getId().equals(employeeInfo.id()))
                        .findFirst()
                        .ifPresent(emp -> emp.setEntities(labor));
            }
        }

        dailyReportRepository.save(dailyReport);
    }

    @Transactional
    public void updateDailyReportDirectContracts(DailyReportSearchRequest searchRequest,
            DailyReportDirectContractUpdateRequest request) {
        // 현장과 공정 조회
        Site site = siteService.getSiteByIdOrThrow(searchRequest.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(searchRequest.siteProcessId());

        // 해당 날짜의 출역일보 조회
        OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(searchRequest.reportDate());
        DailyReport dailyReport = dailyReportRepository
                .findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));

        // EntitySyncUtils.syncList를 사용하여 직영/계약직 정보 동기화
        EntitySyncUtils.syncList(
                dailyReport.getDirectContracts(),
                request.directContracts(),
                (DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo dto) -> {
                    return DailyReportDirectContract.builder()
                            .dailyReport(dailyReport)
                            .outsourcingCompany(outsourcingCompanyService
                                    .getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId()))
                            .labor(laborService.getLaborByIdOrThrow(dto.laborId()))
                            .position(dto.position())
                            .workContent(dto.workContent())
                            .unitPrice(dto.unitPrice())
                            .workQuantity(dto.workQuantity())
                            .memo(dto.memo())
                            .build();
                });

        // company와 labor 업데이트를 위해 추가 처리 (한 번만 반복)
        for (DailyReportDirectContractUpdateRequest.DirectContractUpdateInfo directContractInfo : request
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

        dailyReportRepository.save(dailyReport);
    }

    private OutsourcingCompanyContract getOutsourcingCompanyContractByIdOrThrow(Long contractId) {
        return outsourcingCompanyContractRepository.findById(contractId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));
    }

    private OutsourcingCompanyContractWorker getOutsourcingCompanyContractWorkerByIdOrThrow(Long workerId) {
        return outsourcingCompanyContractWorkerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_WORKER_NOT_FOUND));
    }

    private OutsourcingCompanyContractDriver getOutsourcingCompanyContractDriverByIdOrThrow(Long driverId) {
        return outsourcingCompanyContractDriverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_DRIVER_NOT_FOUND));
    }

    private OutsourcingCompanyContractEquipment getOutsourcingCompanyContractEquipmentByIdOrThrow(Long equipmentId) {
        return outsourcingCompanyContractEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_NOT_FOUND));
    }

    private DailyReport getDailyReportByIdOrThrow(Long id) {
        return dailyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));
    }

    /**
     * 임시 인력을 생성합니다.
     */
    private Labor createTemporaryLabor(DailyReportDirectContractCreateRequest request) {
        // 임시 인력 이름이 필수
        if (request.temporaryLaborName() == null || request.temporaryLaborName().trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.TEMPORARY_LABOR_NAME_REQUIRED);
        }

        // 임시 인력 생성
        Labor temporaryLabor = Labor.builder()
                .name(request.temporaryLaborName())
                .type(LaborType.DIRECT_CONTRACT)
                .build();

        return laborRepository.save(temporaryLabor);
    }
}
