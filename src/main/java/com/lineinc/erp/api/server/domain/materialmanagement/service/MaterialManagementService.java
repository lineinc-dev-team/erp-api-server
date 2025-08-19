package com.lineinc.erp.api.server.domain.materialmanagement.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementChangeType;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementDetailRepository;
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
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
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
    private final MaterialManagementDetailRepository materialManagementDetailRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final Javers javers;
    private final MaterialManagementChangeHistoryRepository materialManagementChangeHistoryRepository;

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
        // TODO: 플랫 구조로 변경 후 수정 필요
        throw new UnsupportedOperationException("엑셀 다운로드는 현재 플랫 구조 변경으로 인해 일시적으로 비활성화되었습니다.");
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
            case "name" -> materialManagement.detail() != null ? materialManagement.detail().name() : "";
            case "standard" -> materialManagement.detail() != null ? materialManagement.detail().standard() : "";
            case "usage" -> materialManagement.detail() != null ? materialManagement.detail().usage() : "";
            case "quantity" -> materialManagement.detail() != null && materialManagement.detail().quantity() != null
                    ? materialManagement.detail().quantity().toString()
                    : "";
            case "unitPrice" -> materialManagement.detail() != null && materialManagement.detail().unitPrice() != null
                    ? materialManagement.detail().unitPrice().toString()
                    : "";
            case "supplyPrice" ->
                materialManagement.detail() != null && materialManagement.detail().supplyPrice() != null
                        ? materialManagement.detail().supplyPrice().toString()
                        : "";
            case "vat" -> materialManagement.detail() != null && materialManagement.detail().vat() != null
                    ? materialManagement.detail().vat().toString()
                    : "";
            case "total" -> materialManagement.detail() != null && materialManagement.detail().total() != null
                    ? materialManagement.detail().total().toString()
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

    public Slice<MaterialManagementNameResponse> getMaterialManagementNames(String keyword, Pageable pageable) {
        Slice<Object[]> resultSlice;

        if (keyword == null || keyword.isBlank()) {
            resultSlice = materialManagementDetailRepository.findAllDistinctNames(pageable);
        } else {
            resultSlice = materialManagementDetailRepository.findDistinctNamesByKeyword(keyword, pageable);
        }

        return resultSlice.map(result -> new MaterialManagementNameResponse((Long) result[1], (String) result[0]));
    }

    @Transactional
    public void updateMaterialManagement(Long id, MaterialManagementUpdateRequest request) {
        MaterialManagement materialManagement = materialManagementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.MATERIAL_MANAGEMENT_NOT_FOUND));

        // siteId가 null이면 기존 site 유지, 아니면 새로운 site 검증
        Site site = request.siteId() != null ? siteService.getSiteByIdOrThrow(request.siteId())
                : materialManagement.getSite();

        // siteProcessId가 null이면 기존 siteProcess 유지, 아니면 새로운 siteProcess 검증
        SiteProcess siteProcess = request.siteProcessId() != null
                ? siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId())
                : materialManagement.getSiteProcess();

        // 새로운 site와 siteProcess가 모두 제공된 경우에만 검증
        if (request.siteId() != null && request.siteProcessId() != null &&
                !siteProcess.getSite().getId().equals(site.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.SITE_PROCESS_NOT_MATCH_SITE);
        }

        // 외주업체 조회
        OutsourcingCompany outsourcingCompany = null;
        if (request.outsourcingCompanyId() != null) {
            outsourcingCompany = outsourcingCompanyRepository.findById(request.outsourcingCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
        }

        materialManagement.syncTransientFields();
        // 변경 전 상태 저장 (Javers 스냅샷)
        MaterialManagement oldSnapshot = JaversUtils.createSnapshot(javers, materialManagement,
                MaterialManagement.class);

        // updateFrom 메서드에서 모든 필드 업데이트
        materialManagement.updateFrom(request, site, siteProcess, outsourcingCompany);

        // 자재 상세 정보가 있는 경우에만 업데이트
        if (request.details() != null) {
            materialManagementDetailService.updateMaterialManagementDetails(materialManagement, request.details());
        }

        // 파일 정보가 있는 경우에만 업데이트
        if (request.files() != null) {
            materialManagementFileService.updateMaterialManagementFiles(materialManagement, request.files());
        }

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

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (MaterialManagementUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                materialManagementChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getMaterialManagement().getId().equals(materialManagement.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }
    }
}
