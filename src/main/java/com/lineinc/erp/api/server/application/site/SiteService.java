package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.application.client.ClientCompanyService;
import com.lineinc.erp.api.server.application.user.UserService;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.repository.SiteContractRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteFileRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    private final ClientCompanyService clientCompanyService;
    private final SiteProcessService siteProcessService;
    private final SiteContractService siteContractService;
    private final UserService userService;

    @Transactional
    public void createSite(SiteCreateRequest request) {
        validateDuplicateName(request.name());
        ClientCompany clientCompany = clientCompanyService.getClientCompanyByIdOrThrow(request.clientCompanyId());
        User user = userService.getUserByIdOrThrow(request.userId());

        OffsetDateTime startDate = DateTimeFormatUtils.toOffsetDateTime(request.startDate());
        OffsetDateTime endDate = DateTimeFormatUtils.toOffsetDateTime(request.endDate());

        Site site = siteRepository.save(Site.builder()
                .name(request.name())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .city(request.city())
                .district(request.district())
                .type(request.type())
                .clientCompany(clientCompany)
                .startDate(startDate)
                .endDate(endDate)
                .user(user)
                .contractAmount(request.contractAmount())
                .memo(request.memo())
                .build()
        );

        siteProcessService.createProcess(site, request.process());
        siteContractService.createContracts(site, request.contracts());
    }

    @Transactional(readOnly = true)
    public void validateDuplicateName(String name) {
        if (siteRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_NAME_ALREADY_EXISTS);
        }
    }
}
