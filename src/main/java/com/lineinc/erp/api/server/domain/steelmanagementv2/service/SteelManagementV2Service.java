package com.lineinc.erp.api.server.domain.steelmanagementv2.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementChangeHistoryV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementDetailV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementChangeHistoryV2Type;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementChangeHistoryV2Repository;
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementV2Repository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementDetailV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementDetailV2UpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2UpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementChangeHistoryV2Response;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementDetailV2Response;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2DetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import lombok.RequiredArgsConstructor;

/**
 * 강재수불부 V2 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SteelManagementV2Service {

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final SteelManagementV2Repository steelManagementV2Repository;
    private final SteelManagementChangeHistoryV2Repository changeHistoryRepository;
    private final UserService userService;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;
    private final S3FileService s3FileService;
    private final Javers javers;

    /**
     * 강재수불부 V2 등록
     */
    @Transactional
    public void createSteelManagementV2(
            final SteelManagementV2CreateRequest request,
            final CustomUserDetails user) {

        // 현장 및 공정 검증
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }
        // 동일한 현장 및 공정에 대한 데이터가 이미 있는지 확인
        if (steelManagementV2Repository.existsBySiteAndSiteProcess(site, siteProcess)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ValidationMessages.STEEL_MANAGEMENT_ALREADY_EXISTS);
        }

        // 강재수불부 V2 생성
        SteelManagementV2 steelManagementV2 = SteelManagementV2.builder().site(site).siteProcess(siteProcess).build();
        steelManagementV2 = steelManagementV2Repository.save(steelManagementV2);

        // 상세 항목 생성
        if (request.details() != null) {
            for (final SteelManagementDetailV2CreateRequest detailRequest : request.details()) {
                // 외주업체 설정 (선택적)
                OutsourcingCompany outsourcingCompany = null;
                if (detailRequest.outsourcingCompanyId() != null) {
                    outsourcingCompany = outsourcingCompanyService
                            .getOutsourcingCompanyByIdOrThrow(detailRequest.outsourcingCompanyId());
                }

                final SteelManagementDetailV2 detail = SteelManagementDetailV2.builder()
                        .steelManagementV2(steelManagementV2)
                        .outsourcingCompany(outsourcingCompany)
                        .type(detailRequest.type())
                        .name(detailRequest.name())
                        .specification(detailRequest.specification())
                        .weight(detailRequest.weight())
                        .count(detailRequest.count())
                        .totalWeight(detailRequest.totalWeight())
                        .length(detailRequest.length())
                        .unitPrice(detailRequest.unitPrice())
                        .amount(detailRequest.amount())
                        .vat(detailRequest.vat())
                        .total(detailRequest.total())
                        .category(detailRequest.category())
                        .fileUrl(detailRequest.fileUrl())
                        .originalFileName(detailRequest.originalFileName())
                        .incomingDate(DateTimeFormatUtils.toOffsetDateTime(detailRequest.incomingDate()))
                        .outgoingDate(DateTimeFormatUtils.toOffsetDateTime(detailRequest.outgoingDate()))
                        .salesDate(DateTimeFormatUtils.toOffsetDateTime(detailRequest.salesDate()))
                        .memo(detailRequest.memo())
                        .build();

                steelManagementV2.getDetails().add(detail);
            }
        }

        steelManagementV2 = steelManagementV2Repository.save(steelManagementV2);

        // 집계 계산
        steelManagementV2.calculateAggregations();
        steelManagementV2 = steelManagementV2Repository.save(steelManagementV2);

        // 변경 이력 생성
        final SteelManagementChangeHistoryV2 changeHistory = SteelManagementChangeHistoryV2.builder()
                .steelManagementV2(steelManagementV2)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(userService.getUserByIdOrThrow(user.getUserId()))
                .build();
        changeHistoryRepository.save(changeHistory);
    }

    /**
     * 강재수불부 목록 조회 (페이징)
     */
    public Page<SteelManagementV2Response> getSteelManagementV2List(
            final SteelManagementV2ListRequest request,
            final Long userId,
            final Pageable pageable) {
        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        return steelManagementV2Repository.findAll(request, pageable, accessibleSiteIds);
    }

    /**
     * 강재수불부 상세 조회
     */
    public SteelManagementV2DetailResponse getSteelManagementV2ById(
            final Long id,
            final SteelManagementDetailV2Type type) {
        final SteelManagementV2 steelManagementV2 = steelManagementV2Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND));
        return SteelManagementV2DetailResponse.from(steelManagementV2, type);
    }

    /**
     * 강재수불부 수정
     */
    @Transactional
    public void updateSteelManagementV2(
            final Long id,
            final SteelManagementV2UpdateRequest request,
            final CustomUserDetails user) {

        final SteelManagementV2 steelManagementV2 = steelManagementV2Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND));

        final List<SteelManagementDetailV2> beforeDetails = steelManagementV2.getDetails()
                .stream()
                .filter(detail -> detail.getType() == request.type())
                .map(detail -> JaversUtils.createSnapshot(javers, detail, SteelManagementDetailV2.class))
                .toList();

        // 2. 요청 ID 목록
        final Set<Long> requestIds = request.details()
                .stream()
                .map(SteelManagementDetailV2UpdateRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 해당 타입의 기존 항목 중 요청에 없는 것은 논리삭제 (다른 타입은 건드리지 않음!)
        steelManagementV2.getDetails()
                .stream()
                .filter(detail -> detail.getType() == request.type())
                .filter(detail -> detail.getId() != null && !requestIds.contains(detail.getId()))
                .forEach(detail -> detail.markAsDeleted());

        // 4. 요청 항목 처리
        for (final SteelManagementDetailV2UpdateRequest dto : request.details()) {
            final OutsourcingCompany outsourcingCompany = dto.outsourcingCompanyId() != null
                    ? outsourcingCompanyService.getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId())
                    : null;

            if (dto.id() != null) {
                // 기존 항목 수정
                final SteelManagementDetailV2 existingDetail = steelManagementV2.getDetails()
                        .stream()
                        .filter(d -> d.getId() != null && d.getId().equals(dto.id()))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ValidationMessages.STEEL_MANAGEMENT_DETAIL_NOT_FOUND));
                existingDetail.updateFrom(dto);
            } else {
                // 신규 항목 추가
                final SteelManagementDetailV2 newDetail = SteelManagementDetailV2.builder()
                        .steelManagementV2(steelManagementV2)
                        .outsourcingCompany(outsourcingCompany)
                        .type(request.type())
                        .name(dto.name())
                        .specification(dto.specification())
                        .weight(dto.weight())
                        .count(dto.count())
                        .totalWeight(dto.totalWeight())
                        .length(dto.length())
                        .unitPrice(dto.unitPrice())
                        .amount(dto.amount())
                        .vat(dto.vat())
                        .total(dto.total())
                        .category(dto.category())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .incomingDate(DateTimeFormatUtils.toOffsetDateTime(dto.incomingDate()))
                        .outgoingDate(DateTimeFormatUtils.toOffsetDateTime(dto.outgoingDate()))
                        .salesDate(DateTimeFormatUtils.toOffsetDateTime(dto.salesDate()))
                        .memo(dto.memo())
                        .build();
                steelManagementV2.getDetails().add(newDetail);
            }
        }

        final List<SteelManagementDetailV2> afterDetails = new ArrayList<>(
                steelManagementV2.getDetails()).stream().filter(detail -> detail.getType() == request.type()).toList();
        final List<Map<String, String>> allChanges = new ArrayList<>();

        final Set<Long> beforeIds = beforeDetails.stream()
                .map(SteelManagementDetailV2::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final SteelManagementDetailV2 after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, SteelManagementDetailV2> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(SteelManagementDetailV2::getId, d -> d));

        for (final SteelManagementDetailV2 before : beforeDetails) {
            if (before.getId() == null || !afterMap.containsKey(before.getId())) {
                continue;
            }

            final SteelManagementDetailV2 after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(allChanges);
            final SteelManagementChangeHistoryV2Type historyType =
                    SteelManagementChangeHistoryV2Type.valueOf(request.type().name());
            final SteelManagementChangeHistoryV2 changeHistory = SteelManagementChangeHistoryV2.builder()
                    .steelManagementV2(steelManagementV2)
                    .type(historyType)
                    .changes(changesJson)
                    .user(userService.getUserByIdOrThrow(user.getUserId()))
                    .build();
            changeHistoryRepository.save(changeHistory);
        }

        steelManagementV2.calculateAggregations();
        steelManagementV2Repository.save(steelManagementV2);
    }

    /**
     * 강재수불부 변경 이력 조회 (페이징)
     */
    public Page<SteelManagementChangeHistoryV2Response> getSteelManagementChangeHistoriesWithPaging(
            final Long id,
            final CustomUserDetails loginUser,
            final Pageable pageable) {
        final Page<SteelManagementChangeHistoryV2> historyPage =
                changeHistoryRepository.findBySteelManagementV2IdWithPaging(id, pageable);
        return historyPage.map(history -> SteelManagementChangeHistoryV2Response.from(history, loginUser.getUserId()));
    }

    /**
     * 강재수불부 목록 엑셀 다운로드
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(
            final CustomUserDetails user,
            final SteelManagementV2ListRequest request,
            final Sort sort,
            final List<String> fields) {
        final User userEntity = userService.getUserByIdOrThrow(user.getUserId());
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(userEntity);
        final List<SteelManagementV2Response> responses =
                steelManagementV2Repository.findAllWithoutPaging(request, sort, accessibleSiteIds);

        final Workbook workbook =
                ExcelExportUtils.generateWorkbook(responses, fields, this::getExcelHeaderName, this::getExcelCellValue);

        // S3에 엑셀 파일 업로드
        final String fileUrl =
                s3FileService.uploadExcelToS3(workbook, ExcelDownloadHistoryType.STEEL_MANAGEMENT.name());

        // 다운로드 이력 저장
        excelDownloadHistoryService.recordDownload(ExcelDownloadHistoryType.STEEL_MANAGEMENT,
                userService.getUserByIdOrThrow(user.getUserId()), fileUrl);

        return workbook;
    }

    /**
     * 강재수불부 상세 엑셀 다운로드 (모든 타입별로 시트 구분)
     */
    @Transactional(readOnly = true)
    public Workbook downloadDetailExcel(
            final Long steelManagementId,
            final CustomUserDetails user,
            final List<String> fields) {
        // 강재수불부 상세 조회 (타입 필터 없이 모든 데이터)
        final SteelManagementV2DetailResponse detailResponse = getSteelManagementV2ById(steelManagementId, null);

        // 모든 상세 항목들을 타입별로 그룹화 (순서: 입고, 출고, 사장, 고철)
        final LinkedHashMap<SteelManagementDetailV2Type, List<SteelManagementDetailV2Response>> detailsByType =
                detailResponse.details()
                        .stream()
                        .collect(Collectors.groupingBy(SteelManagementDetailV2Response::typeCode, () -> {
                            final LinkedHashMap<SteelManagementDetailV2Type, List<SteelManagementDetailV2Response>> orderedMap =
                                    new LinkedHashMap<>();
                            orderedMap.put(SteelManagementDetailV2Type.INCOMING, new ArrayList<>());
                            orderedMap.put(SteelManagementDetailV2Type.OUTGOING, new ArrayList<>());
                            orderedMap.put(SteelManagementDetailV2Type.ON_SITE_STOCK, new ArrayList<>());
                            orderedMap.put(SteelManagementDetailV2Type.SCRAP, new ArrayList<>());
                            return orderedMap;
                        }, Collectors.toList()));

        // 소계에 포함될 필드들 정의
        final List<String> subtotalFields =
                List.of("weight", "count", "totalWeight", "unitPrice", "amount", "vat", "total", "length");

        // 워크북 생성 (여러 시트 지원, 소계 포함)
        final Workbook workbook = ExcelExportUtils.generateMultiSheetWorkbookWithSubtotal(detailsByType, fields, (
                field,
                type) -> getDetailExcelHeaderName(field, type), this::getDetailExcelCellValue,
                SteelManagementDetailV2Type::getLabel, 3, subtotalFields);

        // S3에 엑셀 파일 업로드
        final String fileUrl =
                s3FileService.uploadExcelToS3(workbook, ExcelDownloadHistoryType.STEEL_MANAGEMENT_DETAIL.name());

        // 다운로드 이력 저장
        excelDownloadHistoryService.recordDownload(ExcelDownloadHistoryType.STEEL_MANAGEMENT_DETAIL,
                userService.getUserByIdOrThrow(user.getUserId()), fileUrl);

        return workbook;
    }

    private String getExcelHeaderName(
            final String field) {
        return switch (field) {
            case "siteName" -> "현장명";
            case "siteProcessName" -> "공정명";
            case "incomingOwnMaterial" -> "입고 자사(톤/합계)";
            case "incomingPurchase" -> "입고 구매(톤/합계)";
            case "incomingRental" -> "입고 임대(톤/합계)";
            case "outgoingOwnMaterial" -> "출고 자사(톤/합계)";
            case "outgoingPurchase" -> "출고 구매(톤/합계)";
            case "outgoingRental" -> "출고 임대(톤/합계)";
            case "onSiteStock" -> "사장(톤)";
            case "scrap" -> "고철(톤/합계)";
            case "totalInvestmentAmount" -> "총 금액(투입비)";
            case "onSiteRemainingWeight" -> "현장보유수량(톤)";
            case "createdAt" -> "등록일";
            default -> "";
        };
    }

    private String getExcelCellValue(
            final SteelManagementV2Response response,
            final String field) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        return switch (field) {
            case "siteName" -> response.site() != null ? response.site().name() : "";
            case "siteProcessName" -> response.siteProcess() != null ? response.siteProcess().name() : "";
            case "incomingOwnMaterial" -> formatWeightAndAmount(response.incomingOwnMaterialTotalWeight(),
                    response.incomingOwnMaterialAmount(), numberFormat);
            case "incomingPurchase" -> formatWeightAndAmount(response.incomingPurchaseTotalWeight(),
                    response.incomingPurchaseAmount(), numberFormat);
            case "incomingRental" -> formatWeightAndAmount(response.incomingRentalTotalWeight(),
                    response.incomingRentalAmount(), numberFormat);
            case "outgoingOwnMaterial" -> formatWeightAndAmount(response.outgoingOwnMaterialTotalWeight(),
                    response.outgoingOwnMaterialAmount(), numberFormat);
            case "outgoingPurchase" -> formatWeightAndAmount(response.outgoingPurchaseTotalWeight(),
                    response.outgoingPurchaseAmount(), numberFormat);
            case "outgoingRental" -> formatWeightAndAmount(response.outgoingRentalTotalWeight(),
                    response.outgoingRentalAmount(), numberFormat);
            case "onSiteStock" -> response.onSiteStockTotalWeight() != null && response.onSiteStockTotalWeight() != 0
                    ? numberFormat.format(response.onSiteStockTotalWeight())
                    : "";
            case "scrap" -> formatWeightAndAmount(response.scrapTotalWeight(), response.scrapAmount(), numberFormat);
            case "totalInvestmentAmount" -> response.totalInvestmentAmount() != null
                    ? numberFormat.format(response.totalInvestmentAmount())
                    : "";
            case "onSiteRemainingWeight" -> response.onSiteRemainingWeight() != null
                    ? numberFormat.format(response.onSiteRemainingWeight())
                    : "";
            case "createdAt" -> response.createdAt() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(response.createdAt())
                    : "";
            default -> "";
        };
    }

    private String formatWeightAndAmount(
            final Double weight,
            final Long amount,
            final NumberFormat numberFormat) {
        // 둘 다 null이거나 둘 다 0인 경우 "-"만 표시
        final boolean weightIsEmpty = weight == null || weight == 0.0;
        final boolean amountIsEmpty = amount == null || amount == 0L;

        if (weightIsEmpty && amountIsEmpty) {
            return null;
        }

        final String weightStr = weight != null && weight != 0.0 ? numberFormat.format(weight) : "0";
        final String amountStr = amount != null && amount != 0L ? numberFormat.format(amount) : "0";
        return weightStr + " / " + amountStr;
    }

    private String getDetailExcelHeaderName(
            final String field,
            final SteelManagementDetailV2Type type) {
        return switch (field) {
            case "name" -> "품명";
            case "specification" -> "규격";
            case "weight" -> "단위중량(톤)";
            case "count" -> "본";
            case "totalWeight" -> "총무게(톤)";
            case "unitPrice" -> "단가";
            case "amount" -> "공급가";
            case "vat" -> "부가세";
            case "total" -> "합계";
            case "category" -> "구분";
            case "outsourcingCompanyName" -> "거래선";
            case "incomingDate" -> type == SteelManagementDetailV2Type.INCOMING ? "입고일" : null;
            case "outgoingDate" -> type == SteelManagementDetailV2Type.OUTGOING ? "출고일" : null;
            case "salesDate" -> type == SteelManagementDetailV2Type.SCRAP ? "판매일" : null;
            case "createdAt" -> "등록";
            case "originalFileName" -> "증빙";
            case "memo" -> "비고";
            case "length" -> "길이(m)";
            default -> "";
        };
    }

    private String getDetailExcelCellValue(
            final SteelManagementDetailV2Response response,
            final String field) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        return switch (field) {
            case "name" -> response.name() != null ? response.name() : "";
            case "specification" -> response.specification() != null ? response.specification() : "";
            case "weight" -> response.weight() != null ? numberFormat.format(response.weight()) : "";
            case "count" -> response.count() != null ? numberFormat.format(response.count()) : "";
            case "totalWeight" -> response.totalWeight() != null ? numberFormat.format(response.totalWeight()) : "";
            case "unitPrice" -> response.unitPrice() != null ? numberFormat.format(response.unitPrice()) : "";
            case "amount" -> response.amount() != null ? numberFormat.format(response.amount()) : "";
            case "vat" -> response.vat() != null ? numberFormat.format(response.vat()) : "";
            case "total" -> response.total() != null ? numberFormat.format(response.total()) : "";
            case "category" -> response.categoryName() != null ? response.categoryName() : "";
            case "outsourcingCompanyName" -> response.outsourcingCompany() != null
                    ? response.outsourcingCompany().name()
                    : "";
            case "incomingDate" -> response.incomingDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(response.incomingDate())
                    : "";
            case "outgoingDate" -> response.outgoingDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(response.outgoingDate())
                    : "";
            case "salesDate" -> response.salesDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(response.salesDate())
                    : "";
            case "createdAt" -> response.createdAt() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(response.createdAt())
                    : "";
            case "originalFileName" -> response.originalFileName() != null ? response.originalFileName() : "";
            case "memo" -> response.memo() != null ? response.memo() : "";
            case "length" -> response.length() != null ? numberFormat.format(response.length()) : "";
            default -> "";
        };
    }
}
