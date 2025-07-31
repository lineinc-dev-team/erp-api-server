package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyFileCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyFileUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientCompanyFileService {
    private final ClientCompanyChangeHistoryRepository clientCompanyChangeHistoryRepository;

    public void createClientCompanyFile(ClientCompany clientCompany, List<ClientCompanyFileCreateRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) return;
        requests.stream()
                .map(dto -> ClientCompanyFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
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
    public void updateClientCompanyFiles(ClientCompany clientCompany, List<ClientCompanyFileUpdateRequest> requests) {
        // 원본 파일 목록 복사
        List<ClientCompanyFile> originalFiles = new java.util.ArrayList<>(clientCompany.getFiles());

        EntitySyncUtils.syncList(
                clientCompany.getFiles(),
                requests,
                (ClientCompanyFileUpdateRequest dto) -> ClientCompanyFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build()
        );

        List<ClientCompanyFile> updatedFiles = clientCompany.getFiles();
        List<String> changes = new java.util.ArrayList<>();

        // 감지된 삭제
        for (ClientCompanyFile original : originalFiles) {
            if (original.isDeleted()) {
                changes.add("첨부파일 삭제: " + original.getName());
            }
        }

        // 감지된 추가
        for (ClientCompanyFile updated : updatedFiles) {
            if (updated.getId() == null) {
                changes.add("첨부파일 추가: " + updated.getName());
            }
        }

        // 변경 이력 저장 - 하나의 row에 통합 기록
        if (!changes.isEmpty()) {
            String combinedChange = String.join("\n", changes);
            clientCompanyChangeHistoryRepository.save(ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .changeDetail(combinedChange)
                    .build());
        }
    }
}