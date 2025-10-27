package com.lineinc.erp.api.server.domain.sitemanagementcost.service.v1;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.RequiredArgsConstructor;

/**
 * 현장관리비 Service
 */
@Service
@RequiredArgsConstructor
public class SiteManagementCostService {

    private final SiteManagementCostRepository siteManagementCostRepository;
    private final SiteManagementCostChangeHistoryRepository siteManagementCostChangeHistoryRepository;

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final UserService userService;

    /**
     * 현장관리비 생성
     */
    @Transactional
    public SiteManagementCostResponse createSiteManagementCost(
            final SiteManagementCostCreateRequest request,
            final CustomUserDetails userDetails) {

        // 현장 조회
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());

        // 공정 조회
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 중복 체크: 동일한 년월, 현장, 공정에 대한 데이터가 이미 존재하는지 확인
        siteManagementCostRepository.findByYearMonthAndSiteAndSiteProcess(
                request.yearMonth(),
                site,
                siteProcess)
                .ifPresent(_ -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            ValidationMessages.SITE_MANAGEMENT_COST_ALREADY_EXISTS);
                });

        // 현장관리비 생성
        final SiteManagementCost siteManagementCost = SiteManagementCost.builder()
                .yearMonth(request.yearMonth())
                .site(site)
                .siteProcess(siteProcess)
                .employeeSalary(request.employeeSalary())
                .employeeSalaryMemo(request.employeeSalaryMemo())
                .regularRetirementPension(request.regularRetirementPension())
                .regularRetirementPensionMemo(request.regularRetirementPensionMemo())
                .retirementDeduction(request.retirementDeduction())
                .retirementDeductionMemo(request.retirementDeductionMemo())
                .majorInsuranceRegular(request.majorInsuranceRegular())
                .majorInsuranceRegularMemo(request.majorInsuranceRegularMemo())
                .majorInsuranceDaily(request.majorInsuranceDaily())
                .majorInsuranceDailyMemo(request.majorInsuranceDailyMemo())
                .contractGuaranteeFee(request.contractGuaranteeFee())
                .contractGuaranteeFeeMemo(request.contractGuaranteeFeeMemo())
                .equipmentGuaranteeFee(request.equipmentGuaranteeFee())
                .equipmentGuaranteeFeeMemo(request.equipmentGuaranteeFeeMemo())
                .nationalTaxPayment(request.nationalTaxPayment())
                .nationalTaxPaymentMemo(request.nationalTaxPaymentMemo())
                .headquartersManagementCost(request.headquartersManagementCost())
                .headquartersManagementCostMemo(request.headquartersManagementCostMemo())
                .build();

        final SiteManagementCost savedEntity = siteManagementCostRepository.save(siteManagementCost);

        // 변경 이력 저장
        final User user = userService.getUserByIdOrThrow(userDetails.getUserId());
        final SiteManagementCostChangeHistory changeHistory = SiteManagementCostChangeHistory.builder()
                .siteManagementCost(savedEntity)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(user)
                .build();
        siteManagementCostChangeHistoryRepository.save(changeHistory);

        return SiteManagementCostResponse.from(savedEntity);
    }
}
