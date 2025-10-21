package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstructionGroup;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractConstructionGroupRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractConstructionRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionUpdateRequestV2;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContractConstructionService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final OutsourcingCompanyContractConstructionRepository outsourcingCompanyContractConstructionRepository;
    private final OutsourcingCompanyContractConstructionGroupRepository constructionGroupRepository;
    private final Javers javers;
    private final UserService userService;

    /**
     * 계약 공사항목 정보를 수정합니다.
     */
    @Transactional
    public void updateContractConstructions(final Long contractId,
            final List<OutsourcingCompanyContractConstructionUpdateRequest> constructions, final Long userId) {
        // 1. 계약 존재 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성
        final List<OutsourcingCompanyContractConstruction> beforeConstructions = contract.getConstructions().stream()
                .map(construction -> {
                    final OutsourcingCompanyContractConstruction snapshot = JaversUtils.createSnapshot(javers,
                            construction,
                            OutsourcingCompanyContractConstruction.class);
                    return snapshot;
                })
                .toList();

        // 3. 공사항목 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getConstructions(),
                constructions,
                (final OutsourcingCompanyContractConstructionUpdateRequest dto) -> {
                    final OutsourcingCompanyContractConstruction construction = OutsourcingCompanyContractConstruction
                            .builder()
                            .outsourcingCompanyContract(contract)
                            .item(dto.item())
                            .specification(dto.specification())
                            .unit(dto.unit())
                            .unitPrice(dto.unitPrice())
                            .contractQuantity(dto.contractQuantity())
                            .contractPrice(dto.contractPrice())
                            .outsourcingContractQuantity(dto.outsourcingContractQuantity())
                            .outsourcingContractPrice(dto.outsourcingContractPrice())
                            .memo(dto.memo())
                            .build();
                    return construction;
                });

        // 저장을 명시적으로 호출
        contractRepository.save(contract);

        // 4. 변경사항 추출 및 변경 히스토리 저장
        final List<OutsourcingCompanyContractConstruction> afterConstructions = new ArrayList<>(
                contract.getConstructions());
        final Set<Map<String, String>> allChanges = new LinkedHashSet<>(); // 중복 제거를 위해 Set 사용

        final Set<Long> beforeIds = beforeConstructions.stream()
                .map(OutsourcingCompanyContractConstruction::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyContractConstruction after : afterConstructions) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyContractConstruction> afterMap = afterConstructions.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractConstruction::getId, c -> c));

        for (final OutsourcingCompanyContractConstruction before : beforeConstructions) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final OutsourcingCompanyContractConstruction after = afterMap.get(before.getId());

            // 공사항목 단위 변경 감지
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 5. 변경 히스토리 저장
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.CONSTRUCTION)
                    .changes(json)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            changeHistoryRepository.save(history);
        }
    }

    /**
     * 계약 공사항목 그룹 정보를 수정합니다. (V2)
     */
    @Transactional
    public void updateContractConstructionsV2(final Long contractId,
            final List<OutsourcingCompanyContractConstructionUpdateRequestV2> constructionsV2, final Long userId) {
        // 1. 계약 존재 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성 (공사항목 포함)
        final List<OutsourcingCompanyContractConstructionGroup> beforeGroups = contract.getConstructionGroups().stream()
                .map(group -> {
                    final OutsourcingCompanyContractConstructionGroup snapshot = JaversUtils.createSnapshot(javers,
                            group,
                            OutsourcingCompanyContractConstructionGroup.class);
                    // 공사항목 목록도 깊은 복사로 생성
                    snapshot.getConstructions().addAll(group.getConstructions().stream()
                            .map(construction -> JaversUtils.createSnapshot(javers, construction,
                                    OutsourcingCompanyContractConstruction.class))
                            .collect(Collectors.toList()));
                    return snapshot;
                })
                .toList();

        // 3. 그룹 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getConstructionGroups(),
                constructionsV2,
                (final OutsourcingCompanyContractConstructionUpdateRequestV2 dto) -> {
                    final OutsourcingCompanyContractConstructionGroup group = OutsourcingCompanyContractConstructionGroup
                            .builder()
                            .outsourcingCompanyContract(contract)
                            .itemName(dto.itemName())
                            .build();

                    // 요청에 포함된 공사항목이 있다면 OutsourcingCompanyContractConstruction 객체로 변환하여 등록
                    if (dto.constructions() != null && !dto.constructions().isEmpty()) {
                        group.getConstructions().addAll(dto.constructions().stream()
                                .map(constructionDto -> {
                                    final OutsourcingCompanyContractConstruction construction = OutsourcingCompanyContractConstruction
                                            .builder()
                                            .outsourcingCompanyContract(contract)
                                            .constructionGroup(group)
                                            .item(constructionDto.item())
                                            .specification(constructionDto.specification())
                                            .unit(constructionDto.unit())
                                            .unitPrice(constructionDto.unitPrice())
                                            .contractQuantity(constructionDto.contractQuantity())
                                            .contractPrice(constructionDto.contractPrice())
                                            .outsourcingContractQuantity(constructionDto.outsourcingContractQuantity())
                                            .outsourcingContractPrice(constructionDto.outsourcingContractPrice())
                                            .memo(constructionDto.memo())
                                            .build();
                                    return construction;
                                })
                                .collect(Collectors.toList()));
                    }
                    return group;
                });

        // 저장을 명시적으로 호출
        contractRepository.save(contract);

        // 5. 변경사항 추출 및 변경 히스토리 저장
        final List<OutsourcingCompanyContractConstructionGroup> afterGroups = new ArrayList<>(
                contract.getConstructionGroups());
        final Set<Map<String, String>> allChanges = new LinkedHashSet<>(); // 중복 제거를 위해 Set 사용

        final Set<Long> beforeGroupIds = beforeGroups.stream()
                .map(OutsourcingCompanyContractConstructionGroup::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 추가된 그룹 감지
        for (final OutsourcingCompanyContractConstructionGroup after : afterGroups) {
            if (after.getId() == null || !beforeGroupIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 그룹 감지
        final Map<Long, OutsourcingCompanyContractConstructionGroup> afterMap = afterGroups.stream()
                .filter(g -> g.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractConstructionGroup::getId, g -> g));

        for (final OutsourcingCompanyContractConstructionGroup before : beforeGroups) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final OutsourcingCompanyContractConstructionGroup after = afterMap.get(before.getId());

            // 그룹 변경 감지 (공사항목은 제외)
            // 공사항목을 임시로 제거하여 비교
            final List<OutsourcingCompanyContractConstruction> beforeConstructionsTemp = before.getConstructions();
            final List<OutsourcingCompanyContractConstruction> afterConstructionsTemp = after.getConstructions();
            before.getConstructions().clear();
            after.getConstructions().clear();

            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);

            // 공사항목 목록 복원
            before.getConstructions().addAll(beforeConstructionsTemp);
            after.getConstructions().addAll(afterConstructionsTemp);

            // 그룹이 그대로 존재하는 경우에만 공사항목 변경사항 감지
            final List<OutsourcingCompanyContractConstruction> beforeConstructions = before.getConstructions();
            final List<OutsourcingCompanyContractConstruction> afterConstructions = after.getConstructions();
            final Set<Long> beforeConstructionIds = beforeConstructions.stream()
                    .map(OutsourcingCompanyContractConstruction::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 공사항목 추가 감지
            for (final OutsourcingCompanyContractConstruction afterConstruction : afterConstructions) {
                if (afterConstruction.getId() == null || !beforeConstructionIds.contains(afterConstruction.getId())) {
                    allChanges.add(JaversUtils.extractAddedEntityChange(javers, afterConstruction));
                }
            }

            // 공사항목 수정 감지
            final Map<Long, OutsourcingCompanyContractConstruction> afterConstructionMap = afterConstructions.stream()
                    .filter(c -> c.getId() != null)
                    .collect(Collectors.toMap(OutsourcingCompanyContractConstruction::getId, c -> c));

            for (final OutsourcingCompanyContractConstruction beforeConstruction : beforeConstructions) {
                if (beforeConstruction.getId() == null || !afterConstructionMap.containsKey(beforeConstruction.getId()))
                    continue;

                final OutsourcingCompanyContractConstruction afterConstruction = afterConstructionMap
                        .get(beforeConstruction.getId());

                final Diff constructionDiff = javers.compare(beforeConstruction, afterConstruction);
                final List<Map<String, String>> constructionModified = JaversUtils.extractModifiedChanges(javers,
                        constructionDiff);
                allChanges.addAll(constructionModified);
            }
        }

        // 변경 히스토리 저장
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.CONSTRUCTION)
                    .changes(json)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            changeHistoryRepository.save(history);
        }
    }

    public OutsourcingCompanyContractConstruction getOutsourcingCompanyContractConstructionByIdOrThrow(
            final Long id) {
        return outsourcingCompanyContractConstructionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_NOT_FOUND));
    }
}
