package com.lineinc.erp.api.server.domain.steelmanagementv2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementChangeHistoryV2Repository;
import com.lineinc.erp.api.server.domain.steelmanagementv2.repository.SteelManagementV2Repository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementDetailV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementDetailV2UpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2CreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2UpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementChangeHistoryV2Response;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2DetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
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
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.processId());

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
            final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(detailRequest.outsourcingCompanyId());
            final SteelManagementDetailV2 detail = SteelManagementDetailV2.builder()
                    .steelManagementV2(steelManagementV2)
                    .outsourcingCompany(outsourcingCompany)
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
            final Pageable pageable) {
        return steelManagementV2Repository.findAll(request, pageable);
    }

    /**
     * 강재수불부 상세 조회
     */
    public SteelManagementV2DetailResponse getSteelManagementV2ById(
            final Long id,
            final com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type type) {
        final SteelManagementV2 steelManagementV2 = steelManagementV2Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND));

        final List<SteelManagementDetailV2> beforeDetails = steelManagementV2.getDetails().stream()
                .filter(detail -> detail.getType() == request.type())
                .map(detail -> JaversUtils.createSnapshot(javers, detail, SteelManagementDetailV2.class))
                .toList();

        // 2. 요청 ID 목록
        final Set<Long> requestIds = request.details().stream()
                .map(SteelManagementDetailV2UpdateRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 해당 타입의 기존 항목 중 요청에 없는 것은 논리삭제 (다른 타입은 건드리지 않음!)
        steelManagementV2.getDetails().stream()
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
                final SteelManagementDetailV2 existingDetail = steelManagementV2.getDetails().stream()
                        .filter(d -> d.getId() != null && d.getId().equals(dto.id()))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                ValidationMessages.STEEL_MANAGEMENT_NOT_FOUND));
                existingDetail.updateFrom(dto, outsourcingCompany);
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
                        .unitPrice(dto.unitPrice())
                        .amount(dto.amount())
                        .category(dto.category())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build();
                steelManagementV2.getDetails().add(newDetail);
            }
        }

        final List<SteelManagementDetailV2> afterDetails = new ArrayList<>(steelManagementV2.getDetails()).stream()
                .filter(detail -> detail.getType() == request.type())
                .toList();
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
            final SteelManagementChangeHistoryV2Type historyType = SteelManagementChangeHistoryV2Type
                    .valueOf(request.type().name());
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
        final Page<SteelManagementChangeHistoryV2> historyPage = changeHistoryRepository
                .findBySteelManagementV2IdWithPaging(id, pageable);
        return historyPage.map(history -> SteelManagementChangeHistoryV2Response.from(history, loginUser.getUserId()));
    }
}
