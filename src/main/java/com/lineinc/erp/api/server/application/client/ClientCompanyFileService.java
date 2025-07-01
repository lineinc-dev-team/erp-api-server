package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.ClientCompany;
import com.lineinc.erp.api.server.domain.client.ClientCompanyFile;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyFileRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientCompanyFileService {
    public void createClientCompanyFile(ClientCompany parent, List<ClientCompanyFileRequest> files) {
        if (files == null) return;
        for (var dto : files) {
            parent.getFiles().add(ClientCompanyFile.builder()
                    .documentName(dto.documentName())
                    .fileUrl(dto.fileUrl())
                    .originalFileName(dto.originalFileName())
                    .memo(dto.memo())
                    .clientCompany(parent)
                    .build());
        }
    }
}