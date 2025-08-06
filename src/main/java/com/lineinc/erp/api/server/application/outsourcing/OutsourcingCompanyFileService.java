package com.lineinc.erp.api.server.application.outsourcing;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyContactFileRequest;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyFileService {
    @Transactional
    public void createOutsourcingCompanyFiles(
            OutsourcingCompany outsourcingCompany,
            List<OutsourcingCompanyContactFileRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) return;

        List<OutsourcingCompanyFile> files = requests.stream()
                .map(req -> OutsourcingCompanyFile.builder()
                        .outsourcingCompany(outsourcingCompany)
                        .name(req.name())
                        .fileUrl(req.fileUrl())
                        .originalFileName(req.originalFileName())
                        .memo(req.memo())
                        .build())
                .collect(Collectors.toList());

        outsourcingCompany.getFiles().addAll(files);
    }
}
