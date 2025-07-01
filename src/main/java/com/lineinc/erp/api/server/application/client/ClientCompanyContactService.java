package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.ClientCompany;
import com.lineinc.erp.api.server.domain.client.ClientCompanyContact;
import com.lineinc.erp.api.server.presentation.v1.client.dto.ClientCompanyContactCreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientCompanyContactService {
    public void createClientCompanyContacts(ClientCompany parent, List<ClientCompanyContactCreateRequest> contacts) {
        if (contacts == null) return;
        for (var dto : contacts) {
            parent.getContacts().add(ClientCompanyContact.builder()
                    .name(dto.name())
                    .position(dto.position())
                    .phoneNumber(dto.phoneNumber())
                    .email(dto.email())
                    .memo(dto.memo())
                    .clientCompany(parent)
                    .build());
        }
    }
}