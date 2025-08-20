package com.lineinc.erp.api.server.domain.fuelaggregation.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationChangeType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelInfoRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.AddFuelInfoRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.DeleteFuelAggregationsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelInfoCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuelAggregationService {

    private final FuelAggregationRepository fuelAggregationRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final FuelInfoRepository fuelInfoRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractService outsourcingCompanyContractService;
    private final FuelAggregationChangeHistoryRepository fuelAggregationChangeHistoryRepository;
    private final FuelInfoService fuelInfoService;
    private final Javers javers;

    @Transactional
    public void createFuelAggregation(FuelAggregationCreateRequest request) {
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        FuelAggregation fuelAggregation = FuelAggregation.builder()
                .site(site)
                .siteProcess(siteProcess)
                .date(DateTimeFormatUtils.toOffsetDateTime(request.date()))
                .weather(request.weather())
                .build();

        fuelAggregationRepository.save(fuelAggregation);

        for (FuelInfoCreateRequest fuelInfo : request.fuelInfos()) {
            // 업체, 기사, 장비 ID 검증
            OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(fuelInfo.outsourcingCompanyId());
            OutsourcingCompanyContractDriver driver = outsourcingCompanyContractService
                    .getDriverByIdOrThrow(fuelInfo.driverId());
            OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                    .getEquipmentByIdOrThrow(fuelInfo.equipmentId());

            FuelInfo fuelInfoEntity = FuelInfo.builder()
                    .fuelAggregation(fuelAggregation)
                    .outsourcingCompany(outsourcingCompany)
                    .driver(driver)
                    .equipment(equipment)
                    .fuelType(fuelInfo.fuelType())
                    .fuelAmount(fuelInfo.fuelAmount())
                    .memo(fuelInfo.memo())
                    .build();
            fuelInfoRepository.save(fuelInfoEntity);
        }

    }

    @Transactional(readOnly = true)
    public Page<FuelAggregationListResponse> getAllFuelAggregations(FuelAggregationListRequest request,
            Pageable pageable) {
        return fuelAggregationRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(FuelAggregationListRequest request, Sort sort, List<String> fields) {
        List<FuelAggregationListResponse> responses = fuelAggregationRepository.findAllWithoutPaging(request, sort);

        return ExcelExportUtils.generateWorkbook(
                responses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "date" -> "일자";
            case "outsourcingCompanyName" -> "업체명";
            case "driverName" -> "기사명";
            case "vehicleNumber" -> "차량번호";
            case "specification" -> "규격";
            case "fuelType" -> "유종";
            case "fuelAmount" -> "주유량";
            case "createdAtAndUpdatedAt" -> "등록/수정일";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(FuelAggregationListResponse response, String field) {
        return switch (field) {
            case "id" -> String.valueOf(response.id());
            case "siteName" -> response.site() != null ? response.site().name() : "";
            case "processName" -> response.process() != null ? response.process().name() : "";
            case "date" -> response.date() != null ? response.date().toString() : "";
            case "outsourcingCompanyName" ->
                response.fuelInfo() != null && response.fuelInfo().outsourcingCompany() != null
                        ? response.fuelInfo().outsourcingCompany().name()
                        : "";
            case "driverName" -> response.fuelInfo() != null ? response.fuelInfo().driverName() : "";
            case "vehicleNumber" -> response.fuelInfo() != null ? response.fuelInfo().vehicleNumber() : "";
            case "specification" -> response.fuelInfo() != null ? response.fuelInfo().specification() : "";
            case "fuelType" -> response.fuelInfo() != null ? response.fuelInfo().fuelType() : "";
            case "fuelAmount" -> response.fuelInfo() != null ? String.valueOf(response.fuelInfo().fuelAmount()) : "";
            case "createdAtAndUpdatedAt" ->
                response.createdAt() != null ? DateTimeFormatUtils.formatKoreaLocalDate(response.createdAt())
                        + " / "
                        + DateTimeFormatUtils.formatKoreaLocalDate(response.updatedAt())
                        : "";
            case "memo" -> response.fuelInfo() != null ? response.fuelInfo().memo() : "";
            default -> "";
        };
    }

    @Transactional(readOnly = true)
    public FuelAggregationDetailResponse getFuelAggregationById(Long id) {
        FuelAggregation fuelAggregation = fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        return FuelAggregationDetailResponse.from(fuelAggregation);
    }

    @Transactional(readOnly = true)
    public Slice<FuelAggregationChangeHistoryResponse> getFuelAggregationChangeHistories(Long fuelAggregationId,
            Pageable pageable) {

        Slice<FuelAggregationChangeHistory> histories = fuelAggregationChangeHistoryRepository
                .findByFuelAggregationId(fuelAggregationId, pageable);
        return histories.map(FuelAggregationChangeHistoryResponse::from);
    }

    @Transactional
    public void deleteFuelAggregations(DeleteFuelAggregationsRequest request) {
        List<FuelAggregation> fuelAggregations = fuelAggregationRepository.findAllById(request.fuelAggregationIds());
        if (fuelAggregations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.FUEL_AGGREGATION_NOT_FOUND);
        }

        for (FuelAggregation fuelAggregation : fuelAggregations) {
            fuelAggregation.markAsDeleted();
        }
        fuelAggregationRepository.saveAll(fuelAggregations);
    }

    @Transactional
    public void addFuelInfoToAggregation(Long fuelAggregationId, AddFuelInfoRequest request) {
        FuelAggregation fuelAggregation = fuelAggregationRepository.findById(fuelAggregationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        // 업체, 기사, 장비 ID 검증
        OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
        OutsourcingCompanyContractDriver driver = outsourcingCompanyContractService
                .getDriverByIdOrThrow(request.driverId());
        OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                .getEquipmentByIdOrThrow(request.equipmentId());

        FuelInfo fuelInfo = FuelInfo.builder()
                .fuelAggregation(fuelAggregation)
                .outsourcingCompany(outsourcingCompany)
                .driver(driver)
                .equipment(equipment)
                .fuelType(request.fuelType())
                .fuelAmount(request.fuelAmount())
                .memo(request.memo())
                .build();

        fuelInfoRepository.save(fuelInfo);
    }

    @Transactional
    public void updateFuelAggregation(Long id, FuelAggregationUpdateRequest request) {
        FuelAggregation fuelAggregation = fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        fuelAggregation.syncTransientFields();
        FuelAggregation oldSnapshot = JaversUtils.createSnapshot(javers, fuelAggregation, FuelAggregation.class);

        fuelAggregation.updateFrom(request, site, siteProcess);
        fuelAggregationRepository.save(fuelAggregation);

        Diff diff = javers.compare(oldSnapshot, fuelAggregation);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            FuelAggregationChangeHistory changeHistory = FuelAggregationChangeHistory.builder()
                    .fuelAggregation(fuelAggregation)
                    .type(FuelAggregationChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            fuelAggregationChangeHistoryRepository.save(changeHistory);
        }

        // 변경이력 memo 업데이트 처리
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (FuelAggregationUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                fuelAggregationChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getFuelAggregation().getId().equals(fuelAggregation.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        // 유류정보 수정 처리
        if (request.fuelInfos() != null) {
            fuelInfoService.updateFuelInfos(fuelAggregation, request.fuelInfos());
        }
    }

}
