package com.lineinc.erp.api.server.domain.steelmanagementv2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementChangeHistoryV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementDetailV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementChangeHistoryV2Repository;
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementV2Repository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementDetailV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementChangeHistoryV2Response;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        // 강재수불부 V2 생성
        SteelManagementV2 steelManagementV2 = SteelManagementV2.builder()
                .site(site)
                .siteProcess(siteProcess)
                .build();
        steelManagementV2 = steelManagementV2Repository.save(steelManagementV2);

        // 상세 항목 생성
        for (final SteelManagementDetailV2CreateRequest detailRequest : request.details()) {
            final SteelManagementDetailV2 detail = SteelManagementDetailV2.builder()
                    .steelManagementV2(steelManagementV2)
                    .type(detailRequest.type())
                    .name(detailRequest.name())
                    .specification(detailRequest.specification())
                    .weight(detailRequest.weight())
                    .count(detailRequest.count())
                    .totalWeight(detailRequest.totalWeight())
                    .unitPrice(detailRequest.unitPrice())
                    .amount(detailRequest.amount())
                    .category(detailRequest.category())
                    .fileUrl(detailRequest.fileUrl())
                    .originalFileName(detailRequest.originalFileName())
                    .memo(detailRequest.memo())
                    .build();

            steelManagementV2.getDetails().add(detail);
        }

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
     * 강재수불부 변경 이력 조회 (페이징)
     */
    public Page<SteelManagementChangeHistoryV2Response> getSteelManagementChangeHistoriesWithPaging(
            final Long id,
            final CustomUserDetails loginUser,
            final Pageable pageable) {
        final Page<SteelManagementChangeHistoryV2> historyPage = changeHistoryRepository
                .findBySteelManagementV2IdWithPaging(id, pageable);
        return historyPage.map(history -> SteelManagementChangeHistoryV2Response.from(history, loginUser.getUserId()));
    }
}
