package com.lineinc.erp.api.server.domain.site.service;

import com.lineinc.erp.api.server.domain.client.service.CompanyService;
import com.lineinc.erp.api.server.domain.user.service.UserService;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import com.lineinc.erp.api.server.domain.site.enums.SiteChangeType;
import com.lineinc.erp.api.server.domain.site.repository.SiteChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.site.DeleteSitesRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.site.CreateSiteRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.UpdateSiteRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.changehistory.SiteChangeHistoryResponse;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.site.SiteDetailResponse;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.site.SiteResponse;
import lombok.RequiredArgsConstructor;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private final CompanyService companyService;
    private final SiteProcessService siteProcessService;
    private final SiteContractService siteContractService;
    private final UserService userService;
    private final Javers javers;
    private final SiteChangeHistoryRepository siteChangeHistoryRepository;

    @Transactional
    public void createSite(CreateSiteRequest request) {
        validateDuplicateName(request.name());
        ClientCompany clientCompany = companyService.getClientCompanyByIdOrThrow(request.clientCompanyId());
        User user = userService.getUserByIdOrThrow(request.userId());

        OffsetDateTime startedAt = DateTimeFormatUtils.toOffsetDateTime(request.startedAt());
        OffsetDateTime endedAt = DateTimeFormatUtils.toOffsetDateTime(request.endedAt());

        Site site = siteRepository.save(Site.builder()
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
                .build()
        );

        if (request.process() != null) {
            siteProcessService.createProcess(site, request.process());
        }
        if (request.contracts() != null && !request.contracts().isEmpty()) {
            siteContractService.createContracts(site, request.contracts());
        }
    }

    @Transactional(readOnly = true)
    public void validateDuplicateName(String name) {
        if (siteRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_NAME_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<SiteResponse> getAllSites(Long userId, SiteListRequest request, Pageable pageable) {
        User user = userService.getUserByIdOrThrow(userId);
        List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        return siteRepository.findAll(request, pageable, accessibleSiteIds);
    }

    @Transactional
    public void deleteSites(DeleteSitesRequest request) {
        List<Site> sites = siteRepository.findAllById(request.siteIds());
        if (sites.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND);
        }

        for (Site site : sites) {
            site.markAsDeleted();
        }

        siteRepository.saveAll(sites);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(Long userId, SiteListRequest request, Sort sort, List<String> fields) {
        User user = userService.getUserByIdOrThrow(userId);
        List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        List<SiteResponse> siteResponses = siteRepository.findAllWithoutPaging(request, sort, accessibleSiteIds)
                .stream()
                .map(SiteResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                siteResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue
        );
    }

    private String getExcelHeaderName(String field) {
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
            case "contractAmount" -> "도급금액";
            case "managerName" -> "공정소장";
            default -> null;
        };
    }

    private String getExcelCellValue(SiteResponse siteResponse, String field) {
        return switch (field) {
            case "id" -> String.valueOf(siteResponse.id());
            case "name" -> siteResponse.name();
            case "processName" -> siteResponse.process().name();
            case "address" -> siteResponse.address() + " " + siteResponse.detailAddress();
            case "type" -> siteResponse.type();
            case "clientCompanyName" -> siteResponse.clientCompany() != null ? siteResponse.clientCompany().name() : "";
            case "period" ->
                    DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.startedAt()) + "~" + DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.endedAt());
            case "processStatuses" -> siteResponse.process().status();
            case "createdBy" -> siteResponse.createdBy();
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.createdAt());
            case "hasFile" -> siteResponse.hasFile() ? "Y" : "N";
            case "memo" -> siteResponse.memo();
            case "contractAmount" -> String.valueOf(siteResponse.contractAmount());
            case "managerName" -> siteResponse.manager().username();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public Site getSiteByIdOrThrow(Long siteId) {
        return siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public SiteDetailResponse getSiteById(Long siteId, Long userId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
        User user = userService.getUserByIdOrThrow(userId);
        boolean hasAccess = hasSiteProcessAccess(user, siteId);
        return SiteDetailResponse.from(site, hasAccess);
    }

    private boolean hasSiteProcessAccess(User user, Long siteId) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return false;
        }

        boolean hasGlobalAccess = user.getUserRoles().stream()
                .anyMatch(r -> r.getRole().isHasGlobalSiteProcessAccess());
        if (hasGlobalAccess) {
            return true;
        }

        return user.getUserRoles().stream()
                .flatMap(userRole -> userRole.getRole().getSiteProcesses().stream())
                .anyMatch(siteProcess -> siteProcess.getSite().getId().equals(siteId));
    }

    @Transactional
    public void updateSite(Long siteId, UpdateSiteRequest request) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));

        if (!site.getName().equals(request.name())) {
            validateDuplicateName(request.name());
        }

        site.syncTransientFields();
        Site oldSnapshot = JaversUtils.createSnapshot(javers, site, Site.class);

        site.updateFrom(request, userService, companyService);
        siteRepository.save(site);

        Diff diff = javers.compare(oldSnapshot, site);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            SiteChangeHistory changeHistory = SiteChangeHistory.builder()
                    .site(site)
                    .type(SiteChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            siteChangeHistoryRepository.save(changeHistory);
        }

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (UpdateSiteRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
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
    public Slice<SiteResponse.SiteSimpleResponse> searchSiteByName(String keyword, Pageable pageable) {
        Slice<Site> siteSlice;

        if (keyword == null || keyword.isBlank()) {
            siteSlice = siteRepository.findAllBy(pageable);
        } else {
            siteSlice = siteRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return siteSlice.map(SiteResponse.SiteSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<SiteChangeHistoryResponse> getSiteChangeHistories(Long siteId, Pageable pageable) {
        // 해당 현장 존재 여부 확인 (예외 처리)
        siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));

        // 현장 변경 이력 조회 (페이지 단위)
        Slice<SiteChangeHistory> historySlice = siteChangeHistoryRepository.findBySiteId(siteId, pageable);

        // 엔티티 -> DTO 변환 후 반환
        return historySlice.map(SiteChangeHistoryResponse::from);
    }
}
