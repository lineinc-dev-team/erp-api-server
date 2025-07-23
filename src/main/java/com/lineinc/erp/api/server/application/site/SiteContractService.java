package com.lineinc.erp.api.server.application.site;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteContract;
import com.lineinc.erp.api.server.domain.site.entity.SiteFile;
import com.lineinc.erp.api.server.domain.site.repository.SiteContractRepository;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteContractCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteContractUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SiteContractService {

    private final SiteContractRepository siteContractRepository;
    private final SiteFileService siteFileService;

    public void createContracts(Site site, List<SiteContractCreateRequest> requests) {
        for (SiteContractCreateRequest contractReq : requests) {
            SiteContract contract = siteContractRepository.save(SiteContract.builder()
                    .site(site)
                    .name(contractReq.name())
                    .amount(contractReq.amount())
                    .memo(contractReq.memo())
                    .build()
            );

            if (contractReq.files() != null && !contractReq.files().isEmpty()) {
                siteFileService.createFiles(contract, contractReq.files());
            }
        }
    }

    @Transactional
    public void updateContracts(Site site, List<SiteContractUpdateRequest> requests) {
        EntitySyncUtils.syncList(
                site.getContracts(),
                requests,
                (SiteContractUpdateRequest dto) -> {
                    SiteContract contract = SiteContract.builder()
                            .site(site)
                            .name(dto.name())
                            .amount(dto.amount())
                            .memo(dto.memo())
                            .build();

                    if (dto.files() != null && !dto.files().isEmpty()) {
                        contract.setFiles(dto.files().stream()
                                .map(fileDto -> SiteFile.builder()
                                        .siteContract(contract)
                                        .name(fileDto.name())
                                        .fileUrl(fileDto.fileUrl())
                                        .originalFileName(fileDto.originalFileName())
                                        .memo(fileDto.memo())
                                        .type(fileDto.type())
                                        .build()
                                )
                                .collect(Collectors.toList())
                        );
                    }

                    return contract;
                }
        );
    }
}
