package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.application.client.ClientCompanyService;
import com.lineinc.erp.api.server.application.user.UserService;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.common.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.DeleteSitesRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteUpdateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteDetailResponse;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
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

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private final ClientCompanyService clientCompanyService;
    private final SiteProcessService siteProcessService;
    private final SiteContractService siteContractService;
    private final UserService userService;

    @Transactional
    public void createSite(SiteCreateRequest request) {
        validateDuplicateName(request.name());
        ClientCompany clientCompany = clientCompanyService.getClientCompanyByIdOrThrow(request.clientCompanyId());
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
    public Page<SiteResponse> getAllSites(SiteListRequest request, Pageable pageable) {
        return siteRepository.findAll(request, pageable);
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
    public Workbook downloadExcel(SiteListRequest request, Sort sort, List<String> fields) {
        List<SiteResponse> siteResponses = siteRepository.findAllWithoutPaging(request, sort)
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
            case "clientCompanyName" -> siteResponse.clientCompany().name();
            case "period" ->
                    DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.startedAt()) + "~" + DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.endedAt());
            case "processStatuses" -> siteResponse.process().status();
            case "createdBy" -> siteResponse.createdBy();
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(siteResponse.createdAt());
            case "hasFile" -> siteResponse.hasFile() ? "Y" : "N";
            case "memo" -> siteResponse.memo();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public Site getSiteByIdOrThrow(Long siteId) {
        return siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public SiteDetailResponse getSiteById(Long siteId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
        return SiteDetailResponse.from(site);
    }

    @Transactional
    public void updateSite(Long siteId, SiteUpdateRequest request) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));

        if (!site.getName().equals(request.name())) {
            validateDuplicateName(request.name());
        }

        site.updateFrom(request);

        if (request.userId() != null) {
            User user = userService.getUserByIdOrThrow(request.userId());
            site.changeUser(user);
        }

        if (request.clientCompanyId() != null) {
            ClientCompany clientCompany = clientCompanyService.getClientCompanyByIdOrThrow(request.clientCompanyId());
            site.changeClientCompany(clientCompany);
        }

        if (request.process() != null) {
            siteProcessService.updateProcess(site, request.process());
        }

        if (request.contracts() != null) {
            siteContractService.updateContracts(site, request.contracts());
        }

        siteRepository.save(site);
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

}
