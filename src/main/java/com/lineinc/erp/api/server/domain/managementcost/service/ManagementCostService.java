package com.lineinc.erp.api.server.domain.managementcost.service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.service.LaborService;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyChangeRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.DeleteManagementCostsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostKeyMoneyDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ItemDescriptionResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyBasicInfoRequest;
import com.lineinc.erp.api.server.shared.dto.request.ChangeHistoryRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagementCostService {

    private final ManagementCostRepository managementCostRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final LaborService laborService;

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final ManagementCostDetailService managementCostDetailService;
    private final ManagementCostFileService managementCostFileService;
    private final ManagementCostKeyMoneyDetailService managementCostKeyMoneyDetailService;
    private final ManagementCostMealFeeDetailService managementCostMealFeeDetailService;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final OutsourcingCompanyChangeRepository outsourcingCompanyChangeRepository;

    private final Javers javers;

    @Transactional
    public void createManagementCost(final ManagementCostCreateRequest request) {
        // 1. 현장 및 공정 존재 확인
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        // 2. 외주업체 처리
        final OutsourcingCompany outsourcingCompany = processOutsourcingCompany(
                request.outsourcingCompanyId(),
                request.outsourcingCompanyInfo());

        // 3. 관리비 엔티티 생성 및 저장
        ManagementCost managementCost = ManagementCost.builder()
                .site(site)
                .siteProcess(siteProcess)
                .outsourcingCompany(outsourcingCompany)
                .itemType(request.itemType())
                .itemTypeDescription(request.itemTypeDescription())
                .paymentDate(DateTimeFormatUtils.toOffsetDateTime(request.paymentDate()))
                .memo(request.memo())
                .build();

        managementCost = managementCostRepository.save(managementCost);

        // 4. 상세 목록 저장
        if (request.details() != null && !request.details().isEmpty()) {
            managementCostDetailService.createManagementCostDetails(managementCost, request.details());
        }

        // 5. 전도금 상세 목록 저장
        if (request.keyMoneyDetails() != null && !request.keyMoneyDetails().isEmpty()) {
            createKeyMoneyDetails(managementCost, request.keyMoneyDetails());
        }

        // 6. 식대 상세 목록 저장
        if (request.mealFeeDetails() != null && !request.mealFeeDetails().isEmpty()) {
            createMealFeeDetails(managementCost, request.mealFeeDetails());
        }

        // 7. 파일 목록 저장
        if (request.files() != null && !request.files().isEmpty()) {
            managementCostFileService.createManagementCostFiles(request.files(), managementCost);
        }
    }

    /**
     * 외주업체 처리 (생성 또는 수정)
     */
    private OutsourcingCompany processOutsourcingCompany(final Long outsourcingCompanyId,
            final OutsourcingCompanyBasicInfoRequest outsourcingCompanyInfo) {
        OutsourcingCompany outsourcingCompany = null;

        if (outsourcingCompanyId != null) {
            // 기존 외주업체 조회
            outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(outsourcingCompanyId);

            if (outsourcingCompanyInfo != null) {
                // 외주업체 기본 정보 직접 업데이트
                outsourcingCompany.syncTransientFields();
                final OutsourcingCompany oldSnapshot = JaversUtils.createSnapshot(javers, outsourcingCompany,
                        OutsourcingCompany.class);

                outsourcingCompany.updateOutsourcingCompanyInfo(
                        outsourcingCompanyInfo.name(),
                        outsourcingCompanyInfo.businessNumber(),
                        outsourcingCompanyInfo.ceoName(),
                        outsourcingCompanyInfo.bankName(),
                        outsourcingCompanyInfo.accountNumber(),
                        outsourcingCompanyInfo.accountHolder(),
                        outsourcingCompanyInfo.memo());
                outsourcingCompany = outsourcingCompanyRepository.save(outsourcingCompany);
                final Diff diff = javers.compare(oldSnapshot, outsourcingCompany);
                final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
                final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

                if (!simpleChanges.isEmpty()) {
                    final OutsourcingChangeHistory changeHistory = OutsourcingChangeHistory.builder()
                            .outsourcingCompany(outsourcingCompany)
                            .type(OutsourcingChangeType.BASIC)
                            .changes(changesJson)
                            .build();
                    outsourcingCompanyChangeRepository.save(changeHistory);
                }
            }
        } else if (outsourcingCompanyInfo != null) {
            // 신규 외주업체 생성
            outsourcingCompany = OutsourcingCompany.builder()
                    .name(outsourcingCompanyInfo.name())
                    .businessNumber(outsourcingCompanyInfo.businessNumber())
                    .type(OutsourcingCompanyType.ETC)
                    .ceoName(outsourcingCompanyInfo.ceoName())
                    .bankName(outsourcingCompanyInfo.bankName())
                    .accountNumber(outsourcingCompanyInfo.accountNumber())
                    .accountHolder(outsourcingCompanyInfo.accountHolder())
                    .memo(outsourcingCompanyInfo.memo())
                    .build();
            outsourcingCompany = outsourcingCompanyRepository.save(outsourcingCompany);
        }

        return outsourcingCompany;
    }

    /**
     * 전도금 상세 목록 생성
     */
    private void createKeyMoneyDetails(final ManagementCost managementCost,
            final List<ManagementCostKeyMoneyDetailCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        final List<ManagementCostKeyMoneyDetail> details = requests.stream()
                .map(request -> ManagementCostKeyMoneyDetail.builder()
                        .managementCost(managementCost)
                        .account(request.account())
                        .purpose(request.purpose())
                        .personnelCount(request.personnelCount())
                        .amount(request.amount())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        managementCost.getKeyMoneyDetails().addAll(details);
    }

    /**
     * 식대 상세 목록 생성
     */
    private void createMealFeeDetails(final ManagementCost managementCost,
            final List<ManagementCostMealFeeDetailCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        final List<ManagementCostMealFeeDetail> details = requests.stream()
                .<ManagementCostMealFeeDetail>map(request -> ManagementCostMealFeeDetail.builder()
                        .managementCost(managementCost)
                        .workType(request.workType())
                        .labor(request.laborId() != null ? laborService.getLaborByIdOrThrow(request.laborId()) : null)
                        .name(request.name())
                        .breakfastCount(request.breakfastCount())
                        .lunchCount(request.lunchCount())
                        .unitPrice(request.unitPrice())
                        .amount(request.amount())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        managementCost.getMealFeeDetails().addAll(details);
    }

    @Transactional(readOnly = true)
    public Page<ManagementCostResponse> getAllManagementCosts(final ManagementCostListRequest request,
            final Pageable pageable) {
        return managementCostRepository.findAll(request, pageable);
    }

    @Transactional
    public void deleteManagementCosts(final DeleteManagementCostsRequest request) {
        final List<ManagementCost> managementCosts = managementCostRepository.findAllById(request.managementCostIds());
        if (managementCosts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.MANAGEMENT_COST_NOT_FOUND);
        }

        for (final ManagementCost managementCost : managementCosts) {
            managementCost.markAsDeleted();
        }

        managementCostRepository.saveAll(managementCosts);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(final ManagementCostListRequest request, final Sort sort, final List<String> fields) {
        final List<ManagementCostResponse> managementCostResponses = managementCostRepository
                .findAllWithoutPaging(request, sort)
                .stream()
                .map(ManagementCostResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                managementCostResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(final String field) {
        return switch (field) {
            case "id" -> "No.";
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "itemType" -> "항목";
            case "paymentDate" -> "일자";
            case "outsourcingCompanyName" -> "업체명";
            case "outsourcingCompanyBusinessNumber" -> "사업자번호";
            case "outsourcingCompanyCeoName" -> "대표자";
            case "outsourcingCompanyAccountNumber" -> "청구계좌";
            case "outsourcingCompanyAccountHolder" -> "예금주명";
            case "supplyPrice" -> "공급가";
            case "vat" -> "부가세";
            case "total" -> "합계";
            case "hasFile" -> "첨부파일";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(final ManagementCostResponse managementCost, final String field) {
        return switch (field) {
            case "id" -> String.valueOf(managementCost.id());
            case "siteName" -> managementCost.site().name();
            case "processName" -> managementCost.process().name();
            case "itemType" -> managementCost.itemType();
            case "paymentDate" -> DateTimeFormatUtils.formatKoreaLocalDate(managementCost.paymentDate());
            case "outsourcingCompanyName" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().name()
                    : "";
            case "outsourcingCompanyBusinessNumber" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().businessNumber()
                    : "";
            case "outsourcingCompanyCeoName" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().ceoName()
                    : "";
            case "outsourcingCompanyAccountNumber" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().bankName() + " "
                            + managementCost.outsourcingCompany().accountNumber()
                    : "";
            case "outsourcingCompanyAccountHolder" -> managementCost.outsourcingCompany() != null
                    ? managementCost.outsourcingCompany().accountHolder()
                    : "";
            case "supplyPrice" -> {
                if (managementCost.supplyPrice() != null) {
                    yield NumberFormat.getNumberInstance().format(managementCost.supplyPrice());
                }
                yield "";
            }
            case "vat" -> {
                if (managementCost.vat() != null) {
                    yield NumberFormat.getNumberInstance().format(managementCost.vat());
                }
                yield "";
            }
            case "total" -> {
                if (managementCost.total() != null) {
                    yield NumberFormat.getNumberInstance().format(managementCost.total());
                }
                yield "";
            }
            case "hasFile" -> managementCost.hasFile() ? "Y" : "N";
            case "memo" -> managementCost.memo();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public ManagementCostDetailViewResponse getManagementCostById(final Long siteId) {
        final ManagementCost managementCost = managementCostRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MANAGEMENT_COST_NOT_FOUND));
        return ManagementCostDetailViewResponse.from(managementCost);
    }

    @Transactional(readOnly = true)
    public ManagementCost getManagementCostByIdOrThrow(final Long id) {
        return managementCostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MANAGEMENT_COST_NOT_FOUND));
    }

    @Transactional
    public void updateManagementCost(final Long managementCostId, final ManagementCostUpdateRequest request) {
        final ManagementCost managementCost = getManagementCostByIdOrThrow(managementCostId);
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        // 2. 외주업체 처리
        final OutsourcingCompany outsourcingCompany = processOutsourcingCompany(
                request.outsourcingCompanyId(),
                request.outsourcingCompanyInfo());

        // 수정 전 스냅샷 생성
        managementCost.syncTransientFields();
        final ManagementCost oldSnapshot = JaversUtils.createSnapshot(javers, managementCost, ManagementCost.class);

        // 엔티티 업데이트
        managementCost.updateFrom(request, site, siteProcess, outsourcingCompany);
        managementCostRepository.save(managementCost);

        // 변경 이력 추적
        final Diff diff = javers.compare(oldSnapshot, managementCost);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final ManagementCostChangeHistory changeHistory = ManagementCostChangeHistory.builder()
                    .managementCost(managementCost)
                    .type(ManagementCostChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }

        // 변경이력 메모 수정 처리
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final ChangeHistoryRequest historyRequest : request.changeHistories()) {
                managementCostChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getManagementCost().getId().equals(managementCost.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        // 상세 정보 업데이트
        if (request.details() != null) {
            managementCostDetailService.updateManagementCostDetails(managementCost,
                    request.details());
        }

        // 전도금 상세 정보 업데이트
        if (request.keyMoneyDetails() != null) {
            managementCostKeyMoneyDetailService.updateManagementCostKeyMoneyDetails(managementCost,
                    request.keyMoneyDetails());
        }

        // 식대 상세 정보 업데이트
        if (request.mealFeeDetails() != null) {
            managementCostMealFeeDetailService.updateManagementCostMealFeeDetails(managementCost,
                    request.mealFeeDetails());
        }

        // 파일 업데이트
        if (request.files() != null) {
            managementCostFileService.updateManagementCostFiles(managementCost,
                    request.files());
        }
    }

    /**
     * ETC 항목의 description 목록 조회
     */
    public Slice<ItemDescriptionResponse> getEtcItemDescriptions(final String keyword, final Pageable pageable) {
        Slice<Object[]> resultSlice;

        if (keyword == null || keyword.isBlank()) {
            resultSlice = managementCostRepository.findAllDistinctItemDescriptions(ItemType.ETC, pageable);
        } else {
            resultSlice = managementCostRepository.findDistinctItemDescriptionsByKeyword(ItemType.ETC, keyword,
                    pageable);
        }

        return resultSlice.map(result -> new ItemDescriptionResponse((Long) result[1], (String) result[0]));
    }

    /**
     * 인력명 키워드 검색
     */
    public Slice<LaborNameResponse> getLaborNames(final String keyword, final List<LaborType> types,
            final Pageable pageable) {
        return laborService.getLaborNames(keyword, types, pageable);
    }

    /**
     * 관리비 수정이력 조회 (Slice 방식)
     */
    @Transactional(readOnly = true)
    public Slice<ManagementCostChangeHistoryResponse> getManagementCostChangeHistories(final Long managementCostId,
            final Pageable pageable) {
        final ManagementCost managementCost = getManagementCostByIdOrThrow(managementCostId);

        return managementCostChangeHistoryRepository.findAllByManagementCost(managementCost, pageable)
                .map(ManagementCostChangeHistoryResponse::from);
    }

    /**
     * 관리비 수정이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<ManagementCostChangeHistoryResponse> getManagementCostChangeHistoriesWithPaging(
            final Long managementCostId,
            final Pageable pageable) {
        final ManagementCost managementCost = getManagementCostByIdOrThrow(managementCostId);

        final Page<ManagementCostChangeHistory> historyPage = managementCostChangeHistoryRepository
                .findAllByManagementCostWithPaging(managementCost, pageable);
        return historyPage.map(ManagementCostChangeHistoryResponse::from);
    }
}