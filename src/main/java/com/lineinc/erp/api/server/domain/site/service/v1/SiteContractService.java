package com.lineinc.erp.api.server.domain.site.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteChangeHistory;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.site.enums.SiteChangeHistoryType;
import com.lineinc.erp.api.server.domain.site.repository.SiteChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.CreateSiteContractRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteContractUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteContractService {

    private final SiteContractRepository siteContractRepository;
    private final SiteFileService siteFileService;
    private final Javers javers;
    private final SiteChangeHistoryRepository siteChangeHistoryRepository;

    public void createContracts(final Site site, final List<CreateSiteContractRequest> requests) {
        for (final CreateSiteContractRequest contractReq : requests) {
            final SiteContract contract = siteContractRepository.save(SiteContract.builder()
                    .site(site)
                    .name(contractReq.name())
                    .amount(contractReq.amount())
                    .memo(contractReq.memo())
                    .build());

            if (contractReq.files() != null && !contractReq.files().isEmpty()) {
                siteFileService.createFiles(contract, contractReq.files());
            }
        }
    }

    @Transactional
    public void updateContracts(final Site site, final List<SiteContractUpdateRequest> requests) {
        // 1. 현재 계약서 목록을 복사해 변경 전 상태(snapshot) 보관
        final List<SiteContract> beforeContracts = site.getContracts().stream()
                .map(contract -> {
                    return JaversUtils.createSnapshot(javers, contract, SiteContract.class);
                })
                .toList();

        // 2. 요청(requests) 기반으로 계약서 목록을 동기화 (추가/수정/삭제 반영)
        EntitySyncUtils.syncList(
                site.getContracts(),
                requests,
                (final SiteContractUpdateRequest dto) -> {
                    final SiteContract contract = SiteContract.builder()
                            .site(site)
                            .name(dto.name())
                            .amount(dto.amount())
                            .memo(dto.memo())
                            .build();
                    // 요청에 포함된 첨부파일이 있다면 SiteFile 객체로 변환하여 등록
                    if (dto.files() != null && !dto.files().isEmpty()) {
                        contract.setFiles(dto.files().stream()
                                .map(fileDto -> SiteFile.builder()
                                        .siteContract(contract)
                                        .fileUrl(fileDto.fileUrl())
                                        .originalFileName(fileDto.originalFileName())
                                        .memo(fileDto.memo())
                                        .type(fileDto.type())
                                        .build())
                                .collect(Collectors.toList()));
                    }
                    return contract;
                });

        // 저장을 명시적으로 호출
        siteContractRepository.saveAll(site.getContracts());

        // 3. 동기화 이후 현재 계약서 상태 저장
        final List<SiteContract> afterContracts = new ArrayList<>(site.getContracts());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 4. 새롭게 추가된 계약서 감지
        final Set<Long> beforeIds = beforeContracts.stream()
                .map(SiteContract::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final SiteContract after : afterContracts) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 5. 기존 계약서 중 수정된 항목 감지
        final Map<Long, SiteContract> afterMap = afterContracts.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(SiteContract::getId, c -> c));

        for (final SiteContract before : beforeContracts) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final SiteContract after = afterMap.get(before.getId());

            // 계약서 단위 변경 감지 (첨부파일은 포함되지 않음)
            final Diff diff = javers.compare(before, after);

            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);

            // 파일 추가 감지
            final List<SiteFile> beforeFiles = before.getFiles();
            final List<SiteFile> afterFiles = after.getFiles();

            final Set<Long> beforeFileIds = beforeFiles.stream()
                    .map(SiteFile::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (final SiteFile afterFile : afterFiles) {
                if (afterFile.getId() == null || !beforeFileIds.contains(afterFile.getId())) {
                    allChanges.add(JaversUtils.extractAddedEntityChange(javers, afterFile));
                }
            }
        }

        // 7. 변경된 이력이 있다면 SiteChangeHistory 엔티티로 저장
        if (!allChanges.isEmpty()) {
            // property가 "id"인 변경사항 제외
            final List<Map<String, String>> filteredChanges = allChanges.stream()
                    .filter(change -> !"id".equals(change.get("property")))
                    .toList();

            if (!filteredChanges.isEmpty()) {
                final String json = javers.getJsonConverter().toJson(filteredChanges);
                final SiteChangeHistory changeHistory = SiteChangeHistory.builder()
                        .site(site)
                        .type(SiteChangeHistoryType.CONTRACT)
                        .changes(json)
                        .build();
                siteChangeHistoryRepository.save(changeHistory);
            }
        }
    }
}
