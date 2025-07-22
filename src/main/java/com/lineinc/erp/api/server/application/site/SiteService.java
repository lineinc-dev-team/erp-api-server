package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.repository.SiteContractRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteFileRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteProcessRepository siteProcessRepository;
    private final SiteContractRepository siteContractRepository;
    private final SiteFileRepository siteFileRepository;
    private final ClientCompanyRepository clientCompanyRepository;
    private final UserRepository userRepository;

    private final SiteProcessService siteProcessService;

    @Transactional
    public void createSite(SiteCreateRequest request) {
        if (!clientCompanyRepository.existsById(request.clientCompanyId())) {
            throw new IllegalArgumentException(ValidationMessages.CLIENT_COMPANY_NOT_FOUND);
        }

        OffsetDateTime startDate = DateTimeFormatUtils.toOffsetDateTime(request.startDate());
        OffsetDateTime endDate = DateTimeFormatUtils.toOffsetDateTime(request.endDate());

        Site site = siteRepository.save(Site.builder()
                .name(request.name())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .city(request.city())
                .district(request.district())
                .type(request.type())
                .clientCompany(clientCompanyRepository.getReferenceById(request.clientCompanyId()))
                .startDate(startDate)
                .endDate(endDate)
                .user(userRepository.getReferenceById(request.userId()))
                .contractAmount(request.contractAmount())
                .memo(request.memo())
                .build()
        );

        siteProcessService.createProcess(site, request.process());
//        for (SiteContractCreateRequest contractReq : request.contracts()) {
//            SiteContract contract = siteContractRepository.save(SiteContract.builder()
//                    .site(site)
//                    .name(contractReq.name())
//                    .amount(contractReq.amount())
//                    .memo(contractReq.memo())
//                    .build()
//            );
//
//            for (SiteFileCreateRequest fileReq : contractReq.files()) {
//                siteFileRepository.save(SiteFile.builder()
//                        .siteContract(contract)
//                        .name(fileReq.name())
//                        .fileUrl(fileReq.fileUrl())
//                        .originalFileName(fileReq.originalFileName())
//                        .memo(fileReq.memo())
//                        .type(fileReq.type())
//                        .build()
//                );
//            }
//        }
    }
}
