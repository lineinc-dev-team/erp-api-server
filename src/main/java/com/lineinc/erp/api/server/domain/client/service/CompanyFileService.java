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
                .map(dto -> ClientCompanyFile.createFrom(dto, clientCompany))
                .forEach(clientCompany.getFiles()::add);
    }

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