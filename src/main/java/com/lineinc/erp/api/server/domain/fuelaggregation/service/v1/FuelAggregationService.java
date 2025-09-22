package com.lineinc.erp.api.server.domain.fuelaggregation.service.v1;

import java.text.NumberFormat;
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
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
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
    public void createFuelAggregation(final FuelAggregationCreateRequest request) {
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        final FuelAggregation fuelAggregation = FuelAggregation.builder()
                .site(site)
                .siteProcess(siteProcess)
                .date(DateTimeFormatUtils.toOffsetDateTime(request.date()))
                .weather(request.weather())
                .build();

        fuelAggregationRepository.save(fuelAggregation);

        for (final FuelInfoCreateRequest fuelInfo : request.fuelInfos()) {
            // 업체, 기사, 장비 ID 검증
            final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(fuelInfo.outsourcingCompanyId());
            final OutsourcingCompanyContractDriver driver = outsourcingCompanyContractService
                    .getDriverByIdOrThrow(fuelInfo.driverId());
            final OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                    .getEquipmentByIdOrThrow(fuelInfo.equipmentId());

            final FuelInfo fuelInfoEntity = FuelInfo.builder()
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
    public Page<FuelAggregationListResponse> getAllFuelAggregations(final FuelAggregationListRequest request,
            final Pageable pageable) {
        return fuelAggregationRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(final FuelAggregationListRequest request, final Sort sort,
            final List<String> fields) {
        final List<FuelAggregationListResponse> responses = fuelAggregationRepository.findAllWithoutPaging(request,
                sort);

        return ExcelExportUtils.generateWorkbook(
                responses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(final String field) {
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

    private String getExcelCellValue(final FuelAggregationListResponse response, final String field) {
        return switch (field) {
            case "id" -> String.valueOf(response.id());
            case "siteName" -> response.site() != null ? response.site().name() : "";
            case "processName" -> response.process() != null ? response.process().name() : "";
            case "date" -> response.date() != null ? response.date().toString() : "";
            case "outsourcingCompanyName" ->
                response.fuelInfo() != null && response.fuelInfo().outsourcingCompany() != null
                        ? response.fuelInfo().outsourcingCompany().name()
                        : "";
            case "driverName" -> response.fuelInfo() != null && response.fuelInfo().driver() != null
                    ? response.fuelInfo().driver().name()
                    : "";
            case "vehicleNumber" -> response.fuelInfo() != null && response.fuelInfo().equipment() != null
                    ? response.fuelInfo().equipment().vehicleNumber()
                    : "";
            case "specification" -> response.fuelInfo() != null && response.fuelInfo().equipment() != null
                    ? response.fuelInfo().equipment().specification()
                    : "";
            case "fuelType" -> response.fuelInfo() != null ? response.fuelInfo().fuelType() : "";
            case "fuelAmount" -> response.fuelInfo() != null && response.fuelInfo().fuelAmount() != null
                    ? NumberFormat.getNumberInstance().format(response.fuelInfo().fuelAmount())
                    : "";
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
    public FuelAggregationDetailResponse getFuelAggregationById(final Long id) {
        final FuelAggregation fuelAggregation = fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        return FuelAggregationDetailResponse.from(fuelAggregation);
    }

    @Transactional(readOnly = true)
    public FuelAggregation getFuelAggregationByIdOrThrow(final Long id) {
        return fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));
    }

    /**
     * 유류집계 변경 이력 조회 (Slice 방식)
     */
    @Transactional(readOnly = true)
    public Slice<FuelAggregationChangeHistoryResponse> getFuelAggregationChangeHistories(final Long fuelAggregationId,
            final Pageable pageable) {
        final FuelAggregation fuelAggregation = getFuelAggregationByIdOrThrow(fuelAggregationId);

        final Slice<FuelAggregationChangeHistory> histories = fuelAggregationChangeHistoryRepository
                .findByFuelAggregation(fuelAggregation, pageable);
        return histories.map(FuelAggregationChangeHistoryResponse::from);
    }

    /**
     * 유류집계 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<FuelAggregationChangeHistoryResponse> getFuelAggregationChangeHistoriesWithPaging(
            final Long fuelAggregationId,
            final Pageable pageable) {
        final FuelAggregation fuelAggregation = getFuelAggregationByIdOrThrow(fuelAggregationId);

        final Page<FuelAggregationChangeHistory> historyPage = fuelAggregationChangeHistoryRepository
                .findByFuelAggregationWithPaging(fuelAggregation, pageable);
        return historyPage.map(FuelAggregationChangeHistoryResponse::from);
    }

    @Transactional
    public void deleteFuelAggregations(final DeleteFuelAggregationsRequest request) {
        final List<FuelAggregation> fuelAggregations = fuelAggregationRepository
                .findAllById(request.fuelAggregationIds());
        if (fuelAggregations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.FUEL_AGGREGATION_NOT_FOUND);
        }

        for (final FuelAggregation fuelAggregation : fuelAggregations) {
            fuelAggregation.markAsDeleted();
        }
        fuelAggregationRepository.saveAll(fuelAggregations);
    }

    @Transactional
    public void addFuelInfoToAggregation(final Long fuelAggregationId, final AddFuelInfoRequest request) {
        final FuelAggregation fuelAggregation = fuelAggregationRepository.findById(fuelAggregationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        // 업체, 기사, 장비 ID 검증
        final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
        final OutsourcingCompanyContractDriver driver = outsourcingCompanyContractService
                .getDriverByIdOrThrow(request.driverId());
        final OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                .getEquipmentByIdOrThrow(request.equipmentId());

        final FuelInfo fuelInfo = FuelInfo.builder()
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
    public void updateFuelAggregation(final Long id, final FuelAggregationUpdateRequest request) {
        final FuelAggregation fuelAggregation = fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        fuelAggregation.syncTransientFields();
        final FuelAggregation oldSnapshot = JaversUtils.createSnapshot(javers, fuelAggregation, FuelAggregation.class);

        fuelAggregation.updateFrom(request, site, siteProcess);
        fuelAggregationRepository.save(fuelAggregation);

        final Diff diff = javers.compare(oldSnapshot, fuelAggregation);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final FuelAggregationChangeHistory changeHistory = FuelAggregationChangeHistory.builder()
                    .fuelAggregation(fuelAggregation)
                    .type(FuelAggregationChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            fuelAggregationChangeHistoryRepository.save(changeHistory);
        }

        // 변경이력 memo 업데이트 처리
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final FuelAggregationUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
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
