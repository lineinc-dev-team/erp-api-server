package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClientCompanyService {

    private final ClientCompanyRepository clientCompanyRepository;
    private final ClientCompanyContactService contactService;
    private final ClientCompanyFileService fileService;

    @Transactional
    public void createClientCompany(ClientCompanyCreateRequest request) {
        // 1. ClientCompany 객체 먼저 빌드
        ClientCompany clientCompany = ClientCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .ceoName(request.ceoName())
                .address(request.address())
                .landlineNumber(request.landlineNumber())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .paymentMethod(request.paymentMethod())
                .paymentPeriod(request.paymentPeriod())
                .memo(request.memo())
                .isActive(request.isActive())
                .build();

        // 2. 자식 엔티티 생성 + 연관관계 설정
        contactService.createClientCompanyContacts(clientCompany, request.contacts());
        fileService.createClientCompanyFile(clientCompany, request.files());

        // 3. 모든 연관관계 설정 후 save
        clientCompanyRepository.save(clientCompany);
    }

    @Transactional(readOnly = true)
    public Page<ClientCompanyResponse> getAllClientCompanies(ClientCompanyListRequest request, Pageable pageable) {
        return clientCompanyRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public ClientCompany getClientCompanyByIdOrThrow(Long id) {
        return clientCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deleteClientCompany(Long id) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);
        clientCompany.markAsDeleted();
    }
}