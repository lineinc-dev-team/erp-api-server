package com.lineinc.erp.api.server.domain.fuelaggregation.service.v1;

import java.text.NumberFormat;
import java.time.OffsetDateTime;
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

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportFuel;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfoSubEquipment;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationChangeType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelInfoRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.AddFuelInfoRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.DeleteFuelAggregationsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelCompanyRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelInfoCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelPriceRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelCompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelPriceResponse;
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
    private final DailyReportRepository dailyReportRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractService outsourcingCompanyContractService;
    private final FuelAggregationChangeHistoryRepository fuelAggregationChangeHistoryRepository;
    private final FuelInfoService fuelInfoService;
    private final UserService userService;
    private final S3FileService s3FileService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;
    private final Javers javers;

    @Transactional
    public FuelAggregation createFuelAggregation(final FuelAggregationCreateRequest request, final Long userId) {
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        final FuelAggregation fuelAggregation = fuelAggregationRepository.save(FuelAggregation.builder()
                .site(site)
                .siteProcess(siteProcess)
                .date(DateTimeFormatUtils.toOffsetDateTime(request.date()))
                .weather(request.weather())
                .outsourcingCompany(request.outsourcingCompanyId() != null
                        ? outsourcingCompanyService
                                .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId())
                        : null)
                .gasolinePrice(request.gasolinePrice())
                .dieselPrice(request.dieselPrice())
                .ureaPrice(request.ureaPrice())
                .build());

        for (final FuelInfoCreateRequest fuelInfo : request.fuelInfos()) {
            // 업체, 기사, 장비 ID 검증
            final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(fuelInfo.outsourcingCompanyId());
            final FuelInfo fuelInfoEntity = FuelInfo.builder()
                    .fuelAggregation(fuelAggregation)
                    .outsourcingCompany(outsourcingCompany)
                    .driver(fuelInfo.driverId() != null
                            ? outsourcingCompanyContractService.getDriverByIdOrThrow(fuelInfo.driverId())
                            : null)
                    .equipment(fuelInfo.equipmentId() != null
                            ? outsourcingCompanyContractService.getEquipmentByIdOrThrow(fuelInfo.equipmentId())
                            : null)
                    .categoryType(fuelInfo.categoryType())
                    .fuelType(fuelInfo.fuelType())
                    .fuelAmount(fuelInfo.fuelAmount())
                    .amount(fuelInfo.amount())
                    .fileUrl(fuelInfo.fileUrl())
                    .originalFileName(fuelInfo.originalFileName())
                    .memo(fuelInfo.memo())
                    .build();

            // FuelInfo를 저장하고 FuelAggregation의 컬렉션에도 추가
            fuelInfoRepository.save(fuelInfoEntity);
            fuelAggregation.getFuelInfos().add(fuelInfoEntity);

            // 서브장비 정보 추가
            if (fuelInfo.subEquipments() != null) {
                for (final FuelInfoCreateRequest.FuelInfoSubEquipmentCreateRequest subEquipmentRequest : fuelInfo
                        .subEquipments()) {
                    final OutsourcingCompanyContractSubEquipment subEquipment = subEquipmentRequest
                            .outsourcingCompanyContractSubEquipmentId() != null
                                    ? outsourcingCompanyContractService
                                            .getSubEquipmentByIdOrThrow(
                                                    subEquipmentRequest.outsourcingCompanyContractSubEquipmentId())
                                    : null;

                    final FuelInfoSubEquipment fuelInfoSubEquipment = FuelInfoSubEquipment.builder()
                            .fuelInfo(fuelInfoEntity)
                            .outsourcingCompanyContractSubEquipment(subEquipment)
                            .fuelType(subEquipmentRequest.fuelType())
                            .fuelAmount(subEquipmentRequest.fuelAmount())
                            .amount(subEquipmentRequest.amount())
                            .memo(subEquipmentRequest.memo())
                            .build();

                    fuelInfoEntity.getSubEquipments().add(fuelInfoSubEquipment);
                }
            }
        }

        // 해당 현장, 공정, 일자에 맞는 출역일보 찾기
        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(request.date());
        dailyReportRepository.findBySiteAndSiteProcessAndReportDate(site, siteProcess, reportDate)
                .ifPresent(dailyReport -> {
                    // DailyReportFuel 엔티티 생성 및 연결
                    final DailyReportFuel dailyReportFuel = DailyReportFuel.builder()
                            .dailyReport(dailyReport)
                            .fuelAggregation(fuelAggregation)
                            .build();
                    dailyReport.getFuels().add(dailyReportFuel);

                    // 출역일보의 유류 집계 데이터 업데이트
                    dailyReport.updateGasolineTotalAmount();
                    dailyReport.updateDieselTotalAmount();
                    dailyReport.updateUreaTotalAmount();
                    dailyReport.updateEtcTotalAmount();
                    dailyReport.updateFuelEvidenceSubmitted();
                });

        final FuelAggregationChangeHistory changeHistory = FuelAggregationChangeHistory.builder()
                .fuelAggregation(fuelAggregation)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(userService.getUserByIdOrThrow(userId))
                .build();
        fuelAggregationChangeHistoryRepository.save(changeHistory);

        // FuelInfo들이 저장된 후 다시 조회하여 반환
        return fuelAggregationRepository.findById(fuelAggregation.getId()).orElse(fuelAggregation);
    }

    @Transactional(readOnly = true)
    public Page<FuelAggregationListResponse> getAllFuelAggregations(
            final Long userId,
            final FuelAggregationListRequest request,
            final Pageable pageable) {
        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        return fuelAggregationRepository.findAll(request, pageable, accessibleSiteIds);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(final CustomUserDetails user, final FuelAggregationListRequest request,
            final Sort sort,
            final List<String> fields) {
        final User userEntity = userService.getUserByIdOrThrow(user.getUserId());
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(userEntity);
        final List<FuelAggregationListResponse> responses = fuelAggregationRepository.findAllWithoutPaging(request,
                sort, accessibleSiteIds);

        final Workbook workbook = ExcelExportUtils.generateWorkbook(
                responses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);

        final String fileUrl = s3FileService.uploadExcelToS3(workbook,
                ExcelDownloadHistoryType.FUEL_AGGREGATION.name());

        excelDownloadHistoryService.recordDownload(
                ExcelDownloadHistoryType.FUEL_AGGREGATION,
                userService.getUserByIdOrThrow(user.getUserId()),
                fileUrl);

        return workbook;
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
            case "amount" -> "금액";
            case "createdAtAndUpdatedAt" -> "등록/수정일";
            case "memo" -> "비고";
            case "fuelOutsourcingCompanyName" -> "유류 업체명";
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
            case "amount" -> response.fuelInfo() != null && response.fuelInfo().amount() != null
                    ? NumberFormat.getNumberInstance().format(response.fuelInfo().amount())
                    : "";
            case "createdAtAndUpdatedAt" ->
                response.createdAt() != null ? DateTimeFormatUtils.formatKoreaLocalDate(response.createdAt())
                        + " / "
                        + DateTimeFormatUtils.formatKoreaLocalDate(response.updatedAt())
                        : "";
            case "memo" -> response.fuelInfo() != null ? response.fuelInfo().memo() : "";
            case "fuelOutsourcingCompanyName" ->
                response.outsourcingCompany() != null ? response.outsourcingCompany().name() : "";
            default -> "";
        };
    }

    @Transactional(readOnly = true)
    public FuelAggregationDetailResponse getFuelAggregationById(final Long id) {
        final FuelAggregation fuelAggregation = fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        return FuelAggregationDetailResponse.from(fuelAggregation, dailyReportRepository);
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
            final Pageable pageable, final Long userId) {
        final FuelAggregation fuelAggregation = getFuelAggregationByIdOrThrow(fuelAggregationId);

        final Slice<FuelAggregationChangeHistory> histories = fuelAggregationChangeHistoryRepository
                .findByFuelAggregation(fuelAggregation, pageable);
        return histories.map(history -> FuelAggregationChangeHistoryResponse.from(history, userId));
    }

    /**
     * 유류집계 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<FuelAggregationChangeHistoryResponse> getFuelAggregationChangeHistoriesWithPaging(
            final Long fuelAggregationId,
            final Pageable pageable, final Long userId) {
        final FuelAggregation fuelAggregation = getFuelAggregationByIdOrThrow(fuelAggregationId);

        final Page<FuelAggregationChangeHistory> historyPage = fuelAggregationChangeHistoryRepository
                .findByFuelAggregationWithPaging(fuelAggregation, pageable);
        return historyPage.map(history -> FuelAggregationChangeHistoryResponse.from(history, userId));
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
    public void updateFuelAggregation(final Long id, final FuelAggregationUpdateRequest request, final Long userId) {
        final FuelAggregation fuelAggregation = fuelAggregationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.FUEL_AGGREGATION_NOT_FOUND));

        // 수정 전 스냅샷 생성
        fuelAggregation.syncTransientFields();
        final FuelAggregation oldSnapshot = JaversUtils.createSnapshot(javers, fuelAggregation, FuelAggregation.class);

        final OutsourcingCompany outsourcingCompany = request.outsourcingCompanyId() != null
                ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId())
                : null;

        // 엔티티 업데이트
        fuelAggregation.updateFrom(request, outsourcingCompany);
        fuelAggregationRepository.save(fuelAggregation);

        // 변경 이력 추적
        final Diff diff = javers.compare(oldSnapshot, fuelAggregation);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final FuelAggregationChangeHistory changeHistory = FuelAggregationChangeHistory.builder()
                    .fuelAggregation(fuelAggregation)
                    .user(userService.getUserByIdOrThrow(userId))
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
            fuelInfoService.updateFuelInfos(fuelAggregation, request.fuelInfos(), userId);
        }

        // 출역일보의 유류 집계 데이터 업데이트
        dailyReportRepository.findBySiteAndSiteProcessAndReportDate(
                fuelAggregation.getSite(),
                fuelAggregation.getSiteProcess(),
                fuelAggregation.getDate())
                .ifPresent(dailyReport -> {
                    dailyReport.updateGasolineTotalAmount();
                    dailyReport.updateDieselTotalAmount();
                    dailyReport.updateUreaTotalAmount();
                    dailyReport.updateEtcTotalAmount();
                    dailyReport.updateFuelEvidenceSubmitted();
                });
    }

    @Transactional(readOnly = true)
    public FuelPriceResponse getFuelPrice(final FuelPriceRequest request) {
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(request.reportDate());
        final FuelAggregation fuelAggregation = fuelAggregationRepository
                .findBySiteAndSiteProcessAndDate(site, siteProcess, reportDate)
                .orElse(null);

        if (fuelAggregation == null) {
            return new FuelPriceResponse(null, null, null, null);
        }

        return new FuelPriceResponse(
                fuelAggregation.getId(),
                fuelAggregation.getGasolinePrice(),
                fuelAggregation.getDieselPrice(),
                fuelAggregation.getUreaPrice());
    }

    @Transactional(readOnly = true)
    public FuelCompanyResponse getFuelCompany(final FuelCompanyRequest request) {
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        final OffsetDateTime reportDate = DateTimeFormatUtils.toOffsetDateTime(request.reportDate());
        final FuelAggregation fuelAggregation = fuelAggregationRepository
                .findBySiteAndSiteProcessAndDate(site, siteProcess, reportDate)
                .orElse(null);

        return FuelCompanyResponse.from(fuelAggregation);
    }

}
