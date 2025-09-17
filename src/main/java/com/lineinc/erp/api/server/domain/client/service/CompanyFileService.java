package com.lineinc.erp.api.server.domain.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyChangeHistoryChangeType;
import com.lineinc.erp.api.server.domain.client.repository.CompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyFileService {
    private final CompanyChangeHistoryRepository companyChangeHistoryRepository;
    private final Javers javers;

    public void createClientCompanyFile(final ClientCompany clientCompany,
            final List<ClientCompanyFileCreateRequest> requests) {
        requests.stream()
                .map(dto -> ClientCompanyFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .type(dto.type())
                        .build())
                .forEach(clientCompany.getFiles()::add);
    }

    /**
     * ClientCompany의 파일들을 요청에 맞게 수정, 생성, 삭제 처리합니다.
     * - 요청 리스트가 null이면 아무 작업도 수행하지 않습니다.
     * - 빈 리스트를 전달하면 기존 파일 전부 soft delete 처리합니다.
     *
     * @param clientCompany 파일이 속한 ClientCompany 엔티티
     * @param requests      수정 요청 리스트 (null일 경우 무시)
     */
    @Transactional
    public void updateClientCompanyFiles(final ClientCompany clientCompany,
            final List<ClientCompanyFileUpdateRequest> requests) {
        final List<ClientCompanyFile> beforeFiles = clientCompany.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, ClientCompanyFile.class))
                .toList();

        EntitySyncUtils.syncList(
                clientCompany.getFiles(),
                requests,
                (final ClientCompanyFileUpdateRequest dto) -> ClientCompanyFile.builder()
                        .name(dto.name())
                        .type(dto.type())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build());

        final List<ClientCompanyFile> afterFiles = new ArrayList<>(clientCompany.getFiles());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        final Set<Long> beforeIds = beforeFiles.stream()
                .map(ClientCompanyFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final ClientCompanyFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }
        // 수정된 파일
        final Map<Long, ClientCompanyFile> afterMap = afterFiles.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(ClientCompanyFile::getId, c -> c));

        for (final ClientCompanyFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final ClientCompanyFile after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final ClientCompanyChangeHistory history = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .type(ClientCompanyChangeHistoryChangeType.ATTACHMENT)
                    .changes(json)
                    .build();
            companyChangeHistoryRepository.save(history);
        }

    }
}