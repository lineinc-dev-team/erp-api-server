package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactCreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClientCompanyContactService {
    public void createClientCompanyContacts(ClientCompany clientCompany, List<ClientCompanyContactCreateRequest> contacts) {
        if (Objects.isNull(contacts) || contacts.isEmpty()) return;
        contacts.stream()
                .map(dto -> ClientCompanyContact.builder()
                        .name(dto.name())
                        .position(dto.position())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build())
                .forEach(clientCompany.getContacts()::add);
    }
}