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
import com.lineinc.erp.api.server.domain.labormanagement.service.LaborService;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractDriverRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractSubEquipmentRepository;
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
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportEmployeeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportDirectContractResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final LaborService laborService;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractRepository outsourcingCompanyContractRepository;
    private final OutsourcingCompanyContractWorkerRepository outsourcingCompanyContractWorkerRepository;
    private final OutsourcingCompanyContractDriverRepository outsourcingCompanyContractDriverRepository;
    private final OutsourcingCompanyContractEquipmentRepository outsourcingCompanyContractEquipmentRepository;
    private final OutsourcingCompanyContractSubEquipmentRepository outsourcingCompanyContractSubEquipmentRepository;

    @Transactional
    public void createDailyReport(DailyReportCreateRequest request) {
        // 현장 조회
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 출역일보 생성
        DailyReport dailyReport = DailyReport.builder()
                .site(site)
                .siteProcess(siteProcess)
                .reportDate(
                        DateTimeFormatUtils.toOffsetDateTime(request.reportDate()))
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
                        .getOutsourcingCompanyByIdOrThrow(directContractRequest.companyId());
                Labor labor = laborService.getLaborByIdOrThrow(directContractRequest.laborId());

                DailyReportDirectContract directContract = DailyReportDirectContract.builder()
                        .dailyReport(dailyReport)
                        .company(company)
                        .labor(labor)
                        .position(directContractRequest.position())
                        .workContent(directContractRequest.workContent())
                        .unitPrice(directContractRequest.unitPrice())
                        .workQuantity(directContractRequest.workQuantity())
                        .memo(directContractRequest.memo())
                        .build();

                dailyReport.getDirectContracts().add(directContract);
            }
        }

        // 외주 출역 정보 추가
        if (request.outsourcings() != null) {
            for (DailyReportOutsourcingCreateRequest outsourcingRequest : request.outsourcings()) {
                OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(outsourcingRequest.companyId());
                OutsourcingCompanyContractWorker worker = getOutsourcingCompanyContractWorkerByIdOrThrow(
                        outsourcingRequest.outsourcingCompanyContractWorkerId());

                DailyReportOutsourcing outsourcing = DailyReportOutsourcing.builder()
                        .dailyReport(dailyReport)
                        .company(company)
                        .outsourcingCompanyContractWorker(worker)
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
                        .getOutsourcingCompanyByIdOrThrow(equipmentRequest.companyId());

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

                OutsourcingCompanyContractSubEquipment subEquipment = null;
                if (equipmentRequest.outsourcingCompanyContractSubEquipmentId() != null) {
                    subEquipment = getOutsourcingCompanyContractSubEquipmentByIdOrThrow(
                            equipmentRequest.outsourcingCompanyContractSubEquipmentId());
                }

                DailyReportOutsourcingEquipment outsourcingEquipment = DailyReportOutsourcingEquipment.builder()
                        .dailyReport(dailyReport)
                        .company(company)
                        .outsourcingCompanyContractDriver(driver)
                        .outsourcingCompanyContractEquipment(equipment)
                        .outsourcingCompanyContractSubEquipment(subEquipment)
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

    private OutsourcingCompanyContractSubEquipment getOutsourcingCompanyContractSubEquipmentByIdOrThrow(
            Long subEquipmentId) {
        return outsourcingCompanyContractSubEquipmentRepository.findById(subEquipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_SUB_EQUIPMENT_NOT_FOUND));
    }

    private DailyReport getDailyReportByIdOrThrow(Long id) {
        return dailyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.DAILY_REPORT_NOT_FOUND));
    }
}
