package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyFileRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClientCompanyFileService {
    public void createClientCompanyFile(ClientCompany clientCompany, List<ClientCompanyFileRequest> files) {
        if (Objects.isNull(files) || files.isEmpty()) return;
        files.stream()
                .map(dto -> ClientCompanyFile.builder()
                        .documentName(dto.documentName())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build())
                .forEach(clientCompany.getFiles()::add);
    }
}