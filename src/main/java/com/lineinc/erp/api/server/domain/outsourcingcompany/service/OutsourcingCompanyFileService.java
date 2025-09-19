package com.lineinc.erp.api.server.domain.outsourcingcompany.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.repository.OutsourcingCompanyChangeRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyFileService {
    private final Javers javers;
    private final OutsourcingCompanyChangeRepository outsourcingCompanyChangeRepository;

    @Transactional
    public void createOutsourcingCompanyFiles(
            final OutsourcingCompany outsourcingCompany,
            final List<OutsourcingCompanyFileCreateRequest> requests) {
        if (requests == null || requests.isEmpty())
            return;

        final List<OutsourcingCompanyFile> files = requests.stream()
                .map(req -> OutsourcingCompanyFile.builder()
                        .outsourcingCompany(outsourcingCompany)
                        .name(req.name())
                        .fileUrl(req.fileUrl())
                        .originalFileName(req.originalFileName())
                        .type(req.type())
                        .memo(req.memo())
                        .build())
                .collect(Collectors.toList());

        outsourcingCompany.getFiles().addAll(files);
    }

    @Transactional
    public void updateOutsourcingCompanyFiles(
            final OutsourcingCompany company,
            final List<OutsourcingCompanyFileUpdateRequest> requests) {
        final List<OutsourcingCompanyFile> beforeFiles = company.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, OutsourcingCompanyFile.class))
                .toList();

        EntitySyncUtils.syncList(
                company.getFiles(),
                requests,
                (final OutsourcingCompanyFileUpdateRequest dto) -> OutsourcingCompanyFile.builder()
                        .outsourcingCompany(company)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        final List<OutsourcingCompanyFile> afterFiles = new ArrayList<>(company.getFiles());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        final Set<Long> beforeIds = beforeFiles.stream()
                .map(OutsourcingCompanyFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyFile::getId, f -> f));

        for (final OutsourcingCompanyFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            final OutsourcingCompanyFile after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyChangeHistory history = OutsourcingCompanyChangeHistory.builder()
                    .outsourcingCompany(company)
                    .type(OutsourcingChangeType.ATTACHMENT)
                    .changes(json)
                    .build();
            outsourcingCompanyChangeRepository.save(history);
        }
    }
}
