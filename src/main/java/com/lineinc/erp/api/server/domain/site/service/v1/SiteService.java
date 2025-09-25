package com.lineinc.erp.api.server.domain.site.service.v1;

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

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.clientcompany.service.v1.ClientCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import com.lineinc.erp.api.server.domain.site.enums.SiteChangeHistoryType;
import com.lineinc.erp.api.server.domain.site.repository.SiteChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.CreateSiteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.DeleteSitesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private final ClientCompanyService clientCompanyService;
    private final SiteProcessService siteProcessService;
    private final SiteContractService siteContractService;
    private final UserService userService;
    private final Javers javers;
    private final SiteChangeHistoryRepository siteChangeHistoryRepository;

    @Transactional
    public void createSite(final CreateSiteRequest request) {
        final ClientCompany clientCompany = clientCompanyService.getClientCompanyByIdOrThrow(request.clientCompanyId());
        final User user = userService.getUserByIdOrThrow(request.userId());

        final OffsetDateTime startedAt = DateTimeFormatUtils.toOffsetDateTime(request.startedAt());
        final OffsetDateTime endedAt = DateTimeFormatUtils.toOffsetDateTime(request.endedAt());

        final Site site = siteRepository.save(Site.builder()
                .name(request.name())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .city(request.city())
                .district(request.district())
                .type(request.type())
                .clientCompany(clientCompany)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .user(user)
                .contractAmount(request.contractAmount())
                .memo(request.memo())
                .build());

        if (request.process() != null) {
            siteProcessService.createProcess(site, request.process());
        }
        if (request.contracts() != null && !request.contracts().isEmpty()) {
            siteContractService.createContracts(site, request.contracts());
        }

        final SiteChangeHistory changeHistory = SiteChangeHistory.builder()
                .site(site)
                .description(ValidationMessages.INITIAL_CREATION)
                .build();
        siteChangeHistoryRepository.save(changeHistory);
    }

    @Transactional(readOnly = true)
    public void validateDuplicateName(final String name) {
        if (siteRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_NAME_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<SiteResponse> getAllSites(final Long userId, final SiteListRequest request, final Pageable pageable) {
        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        return siteRepository.findAll(request, pageable, accessibleSiteIds);
    }

    @Transactional
    public void deleteSites(final DeleteSitesRequest request) {
        final List<Site> sites = siteRepository.findAllById(request.siteIds());
        if (sites.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND);
        }

        for (final Site site : sites) {
            site.markAsDeleted();
            site.getProcesses().forEach(process -> process.markAsDeleted());
        }

        siteRepository.saveAll(sites);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(final Long userId, final SiteListRequest request, final Sort sort,
            final List<String> fields) {
        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        final List<SiteResponse> siteResponses = siteRepository.findAllWithoutPaging(request, sort, accessibleSiteIds)
                .stream()
                .map(SiteResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                siteResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(final String field) {
        return switch (field) {
            case "id" -> "No.";
            case "name" -> "현장명";
            case "processName" -> "공정명";
            case "address" -> "위치";
            case "type" -> "현장유형";
            case "clientCompanyName" -> "발주처명";
            case "period" -> "사업기간";
            case "processStatuses" -> "진행상태";
            case "createdBy" -> "등록자";
            case "createdAt" -> "등록일자";
            case "hasFile" -> "첨부파일";
            case "memo" -> "비고";
            case "contractAmount" -> "계약금액";
            case "managerName" -> "공정소장";
            default -> null;
        };
    }

    private String getExcelCellValue(final SiteResponse siteResponse, final String field) {
        return switch (field) {
            case "id" -> String.valueOf(siteResponse.id());
            case "name" -> siteResponse.name();
            case "processName" -> siteResponse.process().name();
            case "address" -> siteResponse.address() + " " + siteResponse.detailAddress();
            case "type" -> siteResponse.type();
            case "clientCompanyName" -> siteResponse.clientCompany() != null ? siteResponse.clientCompany().name() : "";
            case "period" ->
                DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.startedAt()) + "~"
                        + DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.endedAt());
            case "processStatuses" -> siteResponse.process().status();
            case "createdBy" -> siteResponse.createdBy();
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.createdAt());
            case "hasFile" -> siteResponse.hasFile() ? "Y" : "N";
            case "memo" -> siteResponse.memo();
            case "contractAmount" -> String.format("%,d", siteResponse.contractAmount());
            case "managerName" -> siteResponse.manager() != null ? siteResponse.manager().username() : null;
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public Site getSiteByIdOrThrow(final Long siteId) {
        return siteRepository.findById(siteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public SiteDetailResponse getSiteById(final Long siteId, final Long userId) {
        final Site site = siteRepository.findById(siteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
        final User user = userService.getUserByIdOrThrow(userId);
        final boolean hasAccess = hasSiteProcessAccess(user, siteId);
        return SiteDetailResponse.from(site, hasAccess);
    }

    private boolean hasSiteProcessAccess(final User user, final Long siteId) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return false;
        }

        final boolean hasGlobalAccess = user.getUserRoles().stream()
                .anyMatch(r -> r.getRole().isHasGlobalSiteProcessAccess());
        if (hasGlobalAccess) {
            return true;
        }

        return user.getUserRoles().stream()
                .flatMap(userRole -> userRole.getRole().getSiteProcesses().stream())
                .anyMatch(siteProcess -> siteProcess.getSite().getId().equals(siteId));
    }

    @Transactional
    public void updateSite(final Long siteId, final UpdateSiteRequest request) {
        final Site site = siteRepository.findById(siteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));

        if (!site.getName().equals(request.name())) {
            validateDuplicateName(request.name());
        }

        site.syncTransientFields();
        final Site oldSnapshot = JaversUtils.createSnapshot(javers, site, Site.class);

        site.updateFrom(request, userService.getUserByIdOrThrow(request.userId()),
                clientCompanyService.getClientCompanyByIdOrThrow(request.clientCompanyId()));
        siteRepository.save(site);

        final Diff diff = javers.compare(oldSnapshot, site);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final SiteChangeHistory changeHistory = SiteChangeHistory.builder()
                    .site(site)
                    .type(SiteChangeHistoryType.BASIC)
                    .changes(changesJson)
                    .build();
            siteChangeHistoryRepository.save(changeHistory);
        }

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final UpdateSiteRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                siteChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getSite().getId().equals(site.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        if (request.process() != null) {
            siteProcessService.updateProcess(site, request.process());
        }

        if (request.contracts() != null) {
            siteContractService.updateContracts(site, request.contracts());
        }

    }

    @Transactional(readOnly = true)
    public Slice<SiteResponse.SiteSimpleResponse> searchSiteByName(final String keyword, final Pageable pageable) {
        Slice<Site> siteSlice;

        if (keyword == null || keyword.isBlank()) {
            siteSlice = siteRepository.findAllBy(pageable);
        } else {
            siteSlice = siteRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return siteSlice.map(SiteResponse.SiteSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<SiteChangeHistoryResponse> getSiteChangeHistories(final Long siteId, final Pageable pageable) {
        // 해당 현장 존재 여부 확인 (예외 처리)
        final Site site = getSiteByIdOrThrow(siteId);

        // 현장 변경 이력 조회 (페이지 단위)
        final Slice<SiteChangeHistory> historySlice = siteChangeHistoryRepository.findBySite(site, pageable);

        // 엔티티 -> DTO 변환 후 반환
        return historySlice.map(SiteChangeHistoryResponse::from);
    }

    /**
     * 현장 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<SiteChangeHistoryResponse> getSiteChangeHistoriesWithPaging(final Long siteId,
            final Pageable pageable) {
        // 해당 현장 존재 여부 확인 (예외 처리)
        final Site site = getSiteByIdOrThrow(siteId);

        // 현장 변경 이력 조회 (전체 개수 포함)
        final Page<SiteChangeHistory> historyPage = siteChangeHistoryRepository.findBySiteWithPaging(site, pageable);

        // 엔티티 -> DTO 변환 후 반환
        return historyPage.map(SiteChangeHistoryResponse::from);
    }
}
