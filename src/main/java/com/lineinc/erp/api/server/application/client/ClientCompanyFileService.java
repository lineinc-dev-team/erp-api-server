package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyFileCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyFileUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClientCompanyFileService {
    public void createClientCompanyFile(ClientCompany clientCompany, List<ClientCompanyFileCreateRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) return;
        requests.stream()
                .map(dto -> ClientCompanyFile.builder()
                        .documentName(dto.documentName())
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
        EntitySyncUtils.syncList(
                clientCompany.getFiles(),
                requests,
                ClientCompanyFile::getId,
                ClientCompanyFileUpdateRequest::id,
                (ClientCompanyFileUpdateRequest dto) -> ClientCompanyFile.builder()
                        .documentName(dto.documentName())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build(),
                clientCompany.getFiles()::add
        );
    }
}