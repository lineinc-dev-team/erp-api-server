package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.ClientCompany;
import com.lineinc.erp.api.server.domain.client.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.ClientCompanyFile;
import com.lineinc.erp.api.server.domain.client.ClientCompanyRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyContactCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyFileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCompanyService {

    private final ClientCompanyRepository clientCompanyRepository;

    @Transactional
    public void createClientCompany(ClientCompanyCreateRequest request) {

        ClientCompany entity = ClientCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .ceoName(request.ceoName())
                .address(request.address())
                .areaCode(request.areaCode())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .paymentMethod(request.paymentMethod())
                .paymentPeriod(request.paymentPeriod())
                .memo(request.memo())
                .build();

        // 담당자 처리
        List<ClientCompanyContactCreateRequest> contacts = request.contacts();
        if (contacts != null && !contacts.isEmpty()) {
            for (ClientCompanyContactCreateRequest contactRequest : contacts) {
                ClientCompanyContact contact = ClientCompanyContact.builder()
                        .name(contactRequest.name())
                        .position(contactRequest.position())
                        .phoneNumber(contactRequest.phoneNumber())
                        .email(contactRequest.email())
                        .memo(contactRequest.memo())
                        .clientCompany(entity)
                        .build();
                entity.getContacts().add(contact);
            }
        }

        List<ClientCompanyFileRequest> filesMetadata = request.files();
        // 파일 메타데이터 처리 (파일은 이미 외부에 업로드되어 URL로 전달된 상태)
        if (filesMetadata != null && !filesMetadata.isEmpty()) {
            for (ClientCompanyFileRequest meta : filesMetadata) {
                ClientCompanyFile clientFile = ClientCompanyFile.builder()
                        .fileUrl(meta.fileUrl())  // ClientCompanyFileMetadataRequest에 fileUrl 필드가 있다고 가정
                        .originalFileName(meta.originalFileName())  // 원본 파일명도 메타데이터에 포함시켜야 함
                        .memo(meta.memo())
                        .documentName(meta.documentName())
                        .clientCompany(entity)
                        .build();
                entity.getFiles().add(clientFile);
            }
        }

        clientCompanyRepository.save(entity);
    }
}