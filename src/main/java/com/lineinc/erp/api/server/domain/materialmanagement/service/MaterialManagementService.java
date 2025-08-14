package com.lineinc.erp.api.server.domain.materialmanagement.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.DeleteMaterialManagementsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementDetailViewResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialManagementService {

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final MaterialManagementRepository materialManagementRepository;

    private final MaterialManagementDetailService materialManagementDetailService;
    private final MaterialManagementFileService materialManagementFileService;

    @Transactional
    public void createMaterialManagement(MaterialManagementCreateRequest request) {
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        MaterialManagement materialManagement = MaterialManagement.builder()
                .site(site)
                .siteProcess(siteProcess)
                .inputType(request.inputType())
                .inputTypeDescription(request.inputTypeDescription())
                .deliveryDate(DateTimeFormatUtils.toOffsetDateTime(request.deliveryDate()))
                .memo(request.memo())
                .build();

        // 외주업체 설정
        if (request.outsourcingCompanyId() != null) {
            var outsourcingCompany = outsourcingCompanyRepository.findById(request.outsourcingCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
            materialManagement.changeOutsourcingCompany(outsourcingCompany);
        }

        materialManagementDetailService.createMaterialDetailManagement(materialManagement, request.details());
        materialManagementFileService.createMaterialFileManagement(materialManagement, request.files());
        materialManagementRepository.save(materialManagement);
    }

    @Transactional(readOnly = true)
    public Page<MaterialManagementResponse> getAllMaterialManagements(MaterialManagementListRequest request,
            Pageable pageable) {
        return materialManagementRepository.findAll(request, pageable);
    }

    @Transactional
    public void deleteMaterialManagements(DeleteMaterialManagementsRequest request) {
        List<MaterialManagement> materialManagements = materialManagementRepository
                .findAllById(request.materialManagementIds());
        if (materialManagements.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.MATERIAL_MANAGEMENT_NOT_FOUND);
        }

        for (MaterialManagement materialManagement : materialManagements) {
            materialManagement.markAsDeleted();
        }

        materialManagementRepository.saveAll(materialManagements);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(MaterialManagementListRequest request, Sort sort, List<String> fields) {
        List<MaterialManagementResponse> materialManagementResponses = materialManagementRepository
                .findAllWithoutPaging(request, sort)
                .stream()
                .map(MaterialManagementResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                materialManagementResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "inputType" -> "입고구분";
            case "deliveryDate" -> "납품일";
            case "name" -> "품명";
            case "standard" -> "규격";
            case "usage" -> "용도";
            case "quantity" -> "수량";
            case "unitPrice" -> "단가";
            case "supplyPrice" -> "공급가액";
            case "vat" -> "부가세";
            case "total" -> "합계";
            case "hasFile" -> "첨부파일";
            case "memo" -> "비고";
            default -> field;
        };
    }

    private String getExcelCellValue(MaterialManagementResponse materialManagement, String field) {
        return switch (field) {
            case "id" -> String.valueOf(materialManagement.id());
            case "siteName" -> materialManagement.site().name();
            case "processName" -> materialManagement.process().name();
            case "inputType" -> materialManagement.inputType();
            case "deliveryDate" -> materialManagement.deliveryDate().toString();
            case "name" -> materialManagement.details().get(0).name();
            case "standard" -> materialManagement.details().get(0).standard();
            case "usage" -> materialManagement.details().get(0).usage();
            case "quantity" -> materialManagement.details().get(0).quantity().toString();
            case "unitPrice" -> materialManagement.details().get(0).unitPrice().toString();
            case "supplyPrice" -> materialManagement.details().get(0).supplyPrice().toString();
            case "vat" -> materialManagement.details().get(0).vat().toString();
            case "total" -> materialManagement.details().get(0).total().toString();
            case "hasFile" -> !materialManagement.hasFile() ? "Y" : "N";
            case "memo" -> materialManagement.memo();
            default -> "";
        };
    }

    @Transactional(readOnly = true)
    public MaterialManagementDetailViewResponse getMaterialManagementById(Long id) {
        MaterialManagement materialManagement = materialManagementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MATERIAL_MANAGEMENT_NOT_FOUND));
        return MaterialManagementDetailViewResponse.from(materialManagement);
    }

    @Transactional
    public void updateMaterialManagement(Long id, MaterialManagementUpdateRequest request) {
        MaterialManagement materialManagement = materialManagementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MATERIAL_MANAGEMENT_NOT_FOUND));
        Site site = siteService.getSiteByIdOrThrow(request.siteId());
        SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        if (!siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        materialManagement.changeSite(site);
        materialManagement.changeSiteProcess(siteProcess);

        // 외주업체 업데이트
        if (request.outsourcingCompanyId() != null) {
            var outsourcingCompany = outsourcingCompanyRepository.findById(request.outsourcingCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "외주업체를 찾을 수 없습니다"));
            materialManagement.changeOutsourcingCompany(outsourcingCompany);
        } else {
            materialManagement.changeOutsourcingCompany(null);
        }

        materialManagement.updateFrom(request);

        materialManagementDetailService.updateMaterialManagementDetails(materialManagement, request.details());
        materialManagementFileService.updateMaterialManagementFiles(materialManagement, request.files());
    }
}
