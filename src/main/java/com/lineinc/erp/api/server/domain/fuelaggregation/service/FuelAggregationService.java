package com.lineinc.erp.api.server.domain.fuelaggregation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelInfoRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelInfoCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuelAggregationService {

    private final FuelAggregationRepository fuelAggregationRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final FuelInfoRepository fuelInfoRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractService outsourcingCompanyContractService;

    @Transactional
    public void createFuelAggregation(FuelAggregationCreateRequest request) {
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        FuelAggregation fuelAggregation = FuelAggregation.builder()
                .site(site)
                .siteProcess(siteProcess)
                .date(DateTimeFormatUtils.toOffsetDateTime(request.date()))
                .weather(request.weather())
                .build();

        fuelAggregationRepository.save(fuelAggregation);

        for (FuelInfoCreateRequest fuelInfo : request.fuelInfos()) {
            // 업체, 기사, 장비 ID 검증
            OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(fuelInfo.outsourcingCompanyId());
            OutsourcingCompanyContractDriver driver = outsourcingCompanyContractService
                    .getDriverByIdOrThrow(fuelInfo.driverId());
            OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                    .getEquipmentByIdOrThrow(fuelInfo.equipmentId());

            FuelInfo fuelInfoEntity = FuelInfo.builder()
                    .fuelAggregation(fuelAggregation)
                    .outsourcingCompany(outsourcingCompany)
                    .driver(driver)
                    .equipment(equipment)
                    .fuelType(fuelInfo.fuelType())
                    .fuelAmount(fuelInfo.fuelAmount())
                    .memo(fuelInfo.memo())
                    .build();
            fuelInfoRepository.save(fuelInfoEntity);
        }

    }

    @Transactional(readOnly = true)
    public Page<FuelAggregationListResponse> getAllFuelAggregations(FuelAggregationListRequest request,
            Pageable pageable) {
        return fuelAggregationRepository.findAll(request, pageable);
    }
}
