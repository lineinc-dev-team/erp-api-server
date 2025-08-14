package com.lineinc.erp.api.server.domain.materialmanagement.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementChangeType;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
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
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialManagementService {

    private final MaterialManagementRepository materialManagementRepository;
    private final MaterialManagementDetailService materialManagementDetailService;
    private final MaterialManagementFileService materialManagementFileService;
    private final MaterialManagementChangeHistoryRepository changeHistoryRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final Javers javers;

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
            case "outsourcingCompanyName" -> "납품업체명";
            case "inputType" -> "투입구분";
            case "inputTypeDescription" -> "투입구분 상세";
            case "deliveryDate" -> "납품일자";
            case "name" -> "품명";
            case "standard" -> "규격";
            case "usage" -> "사용용도";
            case "quantity" -> "수량";
            case "unitPrice" -> "단가";
            case "supplyPrice" -> "공급가";
            case "vat" -> "부가세";
            case "total" -> "합계";
            case "hasFile" -> "첨부파일";
            case "memo" -> "비고";
            default -> "";
        };
    }

    private String getExcelCellValue(MaterialManagementResponse materialManagement, String field) {
        return switch (field) {
            case "siteName" -> materialManagement.site() != null ? materialManagement.site().name() : "";
            case "processName" -> materialManagement.process() != null ? materialManagement.process().name() : "";
            case "outsourcingCompanyName" ->
                materialManagement.outsourcingCompany() != null ? materialManagement.outsourcingCompany().name() : "";
            case "inputType" -> materialManagement.inputType();
            case "inputTypeDescription" -> materialManagement.inputTypeDescription();
            case "deliveryDate" ->
                materialManagement.deliveryDate() != null ? materialManagement.deliveryDate().toString() : "";
            case "name" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).name()
                    : "";
            case "standard" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).standard()
                    : "";
            case "usage" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).usage()
                    : "";
            case "quantity" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).quantity().toString()
                    : "";
            case "unitPrice" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).unitPrice().toString()
                    : "";
            case "supplyPrice" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).supplyPrice().toString()
                    : "";
            case "vat" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).vat().toString()
                    : "";
            case "total" -> materialManagement.details() != null && !materialManagement.details().isEmpty()
                    ? materialManagement.details().get(0).total().toString()
                    : "";
            case "hasFile" -> materialManagement.hasFile() ? "Y" : "N";
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

        // 외주업체 조회
        OutsourcingCompany outsourcingCompany = null;
        if (request.outsourcingCompanyId() != null) {
            outsourcingCompany = outsourcingCompanyRepository.findById(request.outsourcingCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
        }

        // 변경 전 상태 저장 (Javers 스냅샷)
        materialManagement.syncTransientFields();
        MaterialManagement oldSnapshot = JaversUtils.createSnapshot(javers, materialManagement,
                MaterialManagement.class);

        // updateFrom 메서드에서 모든 필드 업데이트
        materialManagement.updateFrom(request, site, siteProcess, outsourcingCompany);

        materialManagementDetailService.updateMaterialManagementDetails(materialManagement, request.details());
        materialManagementFileService.updateMaterialManagementFiles(materialManagement, request.files());

        // Javers를 사용하여 변경사항 추적
        Diff diff = javers.compare(oldSnapshot, materialManagement);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        // 변경사항이 있을 때만 수정이력 생성
        if (!simpleChanges.isEmpty()) {
            MaterialManagementChangeHistory changeHistory = MaterialManagementChangeHistory.builder()
                    .materialManagement(materialManagement)
                    .type(MaterialManagementChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            changeHistoryRepository.save(changeHistory);
        }
    }
}
