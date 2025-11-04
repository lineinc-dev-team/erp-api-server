package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstructionGroup;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractContact;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriverFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorkerFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractConstructionGroupRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractConstructionRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractContactRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractDriverRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractFileRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractSubEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractWorkerFileRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractWorkerRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.DeleteOutsourcingCompanyContractsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.ContractListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionCreateRequestV2;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractContactCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractContstructionCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractDriverCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractDriverFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractSubEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractWorkerCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractWorkerFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionGroupResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractEquipmentResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractWorkerResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OutsourcingCompanyContractService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractContactRepository contactRepository;
    private final OutsourcingCompanyContractFileRepository fileRepository;
    private final OutsourcingCompanyContractWorkerRepository workerRepository;
    private final OutsourcingCompanyContractWorkerFileRepository workerFileRepository;
    private final OutsourcingCompanyContractEquipmentRepository equipmentRepository;
    private final OutsourcingCompanyContractDriverRepository driverRepository;
    private final OutsourcingCompanyContractConstructionRepository constructionRepository;
    private final OutsourcingCompanyContractConstructionGroupRepository constructionGroupRepository;
    private final OutsourcingCompanyContractHistoryRepository contractHistoryRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository contractChangeHistoryRepository;
    private final OutsourcingCompanyContractSubEquipmentRepository subEquipmentRepository;
    private final Javers javers;
    private final OutsourcingCompanyContractContactService contractContactService;
    private final OutsourcingCompanyContractFileService contractFileService;
    private final OutsourcingCompanyContractWorkerService contractWorkerService;
    private final OutsourcingCompanyContractEquipmentService contractEquipmentService;
    private final OutsourcingCompanyContractDriverService contractDriverService;
    private final OutsourcingCompanyContractConstructionService contractConstructionService;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final UserService userService;
    private final S3FileService s3FileService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;

    /**
     * 외주업체 계약을 생성합니다.
     */
    public void createContract(final OutsourcingCompanyContractCreateRequest request, final Long userId) {
        log.info("외주업체 계약 생성 시작: {}", request);

        // 1. 기본 계약 정보 생성
        final OutsourcingCompanyContract contract = createMainContract(request);

        // 2. 계약 담당자 생성
        if (request.contacts() != null && !request.contacts().isEmpty()) {
            createContractContacts(contract, request.contacts());
        }

        // 3. 계약 첨부파일 생성
        if (request.files() != null && !request.files().isEmpty()) {
            createContractFiles(contract, request.files());
        }

        // 4. 계약 인력 생성
        if (request.workers() != null && !request.workers().isEmpty()) {
            createContractWorkers(contract, request.workers());
        }

        // 5. 계약 장비 생성
        if (request.equipments() != null && !request.equipments().isEmpty()) {
            createContractEquipments(contract, request.equipments());
        }

        // 6. 계약 운전자 생성
        if (request.drivers() != null && !request.drivers().isEmpty()) {
            createContractDrivers(contract, request.drivers());
        }

        // 7. 계약 공사항목 생성
        if (request.constructions() != null && !request.constructions().isEmpty()) {
            createContractConstructions(contract, request.constructions());
        } else if (request.constructionsV2() != null && !request.constructionsV2().isEmpty()) {
            createContractConstructionsV2(contract, request.constructionsV2());
        }

        // 8. 계약 이력 생성
        createContractHistory(contract);

        final OutsourcingCompanyContractChangeHistory changeHistory = OutsourcingCompanyContractChangeHistory.builder()
                .outsourcingCompanyContract(contract)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(userService.getUserByIdOrThrow(userId))
                .build();
        contractChangeHistoryRepository.save(changeHistory);
    }

    /**
     * 기본 계약 정보를 생성합니다.
     */
    private OutsourcingCompanyContract createMainContract(final OutsourcingCompanyContractCreateRequest request) {
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());

        final OffsetDateTime contractStartDate = DateTimeFormatUtils.toOffsetDateTime(request.contractStartDate());
        final OffsetDateTime contractEndDate = DateTimeFormatUtils.toOffsetDateTime(request.contractEndDate());

        final OutsourcingCompanyContract contract = OutsourcingCompanyContract.builder()
                .contractName(request.contractName())
                .site(site)
                .siteProcess(siteProcess)
                .outsourcingCompany(outsourcingCompany)
                .type(request.type())
                .typeDescription(request.typeDescription())
                .contractStartDate(contractStartDate)
                .contractEndDate(contractEndDate)
                .contractAmount(request.contractAmount())
                .defaultDeductions(request.defaultDeductionsType())
                .defaultDeductionsDescription(request.defaultDeductionsDescription())
                .taxInvoiceCondition(request.taxInvoiceCondition())
                .taxInvoiceIssueDayOfMonth(request.taxInvoiceIssueDayOfMonth())
                .category(request.category())
                .workTypeName(request.workTypeName())
                .status(request.status() != null ? request.status() : OutsourcingCompanyContractStatus.IN_PROGRESS)
                .memo(request.memo())
                .build();

        return contractRepository.save(contract);
    }

    /**
     * 계약 담당자를 생성합니다.
     */
    private void createContractContacts(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractContactCreateRequest> contacts) {

        if (contacts == null || contacts.isEmpty())
            return;

        final long mainCount = contacts.stream().filter(OutsourcingCompanyContractContactCreateRequest::isMain).count();
        if (mainCount != 1) {
            throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
        }

        for (final OutsourcingCompanyContractContactCreateRequest contactRequest : contacts) {
            final OutsourcingCompanyContractContact contact = OutsourcingCompanyContractContact.builder()
                    .outsourcingCompanyContract(contract)
                    .name(contactRequest.name())
                    .department(contactRequest.department())
                    .position(contactRequest.position())
                    .landlineNumber(contactRequest.landlineNumber())
                    .phoneNumber(contactRequest.phoneNumber())
                    .email(contactRequest.email())
                    .memo(contactRequest.memo())
                    .isMain(contactRequest.isMain())
                    .build();

            contactRepository.save(contact);
        }
    }

    /**
     * 계약 첨부파일을 생성합니다.
     */
    private void createContractFiles(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractFileCreateRequest> files) {
        for (final OutsourcingCompanyContractFileCreateRequest fileRequest : files) {
            final OutsourcingCompanyContractFile file = OutsourcingCompanyContractFile.builder()
                    .outsourcingCompanyContract(contract)
                    .name(fileRequest.name())
                    .fileUrl(fileRequest.fileUrl())
                    .originalFileName(fileRequest.originalFileName())
                    .type(fileRequest.type())
                    .memo(fileRequest.memo())
                    .build();

            fileRepository.save(file);
        }
    }

    /**
     * 계약 인력을 생성합니다.
     */
    private void createContractWorkers(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractWorkerCreateRequest> workers) {
        for (final OutsourcingCompanyContractWorkerCreateRequest workerRequest : workers) {
            final OutsourcingCompanyContractWorker worker = OutsourcingCompanyContractWorker.builder()
                    .outsourcingCompanyContract(contract)
                    .name(workerRequest.name())
                    .category(workerRequest.category())
                    .taskDescription(workerRequest.taskDescription())
                    .memo(workerRequest.memo())
                    .build();

            workerRepository.save(worker);

            // 인력 서류가 있으면 생성
            if (workerRequest.files() != null && !workerRequest.files().isEmpty()) {
                createContractWorkerFiles(worker, workerRequest.files());
            }
        }
    }

    /**
     * 계약 장비를 생성합니다.
     */
    private void createContractEquipments(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractEquipmentCreateRequest> equipments) {
        for (final OutsourcingCompanyContractEquipmentCreateRequest equipmentRequest : equipments) {
            final OutsourcingCompanyContractEquipment equipment = OutsourcingCompanyContractEquipment.builder()
                    .outsourcingCompanyContract(contract)
                    .specification(equipmentRequest.specification())
                    .vehicleNumber(equipmentRequest.vehicleNumber())
                    .category(equipmentRequest.category())
                    .unitPrice(equipmentRequest.unitPrice())
                    .subtotal(equipmentRequest.subtotal())
                    .taskDescription(equipmentRequest.taskDescription())
                    .type(equipmentRequest.type())
                    .memo(equipmentRequest.memo())
                    .build();

            equipmentRepository.save(equipment);

            // 보조장비가 있으면 생성
            if (equipmentRequest.subEquipments() != null && !equipmentRequest.subEquipments().isEmpty()) {
                createContractSubEquipments(equipment, equipmentRequest.subEquipments());
            }
        }
    }

    /**
     * 계약 운전자를 생성합니다.
     */
    private void createContractDrivers(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractDriverCreateRequest> drivers) {
        for (final OutsourcingCompanyContractDriverCreateRequest driverRequest : drivers) {
            final OutsourcingCompanyContractDriver driver = OutsourcingCompanyContractDriver.builder()
                    .outsourcingCompanyContract(contract)
                    .name(driverRequest.name())
                    .memo(driverRequest.memo())
                    .build();

            driverRepository.save(driver);

            // 드라이버 서류가 있으면 생성
            if (driverRequest.files() != null && !driverRequest.files().isEmpty()) {
                createContractDriverFiles(driver, driverRequest.files());
            }
        }
    }

    /**
     * 계약 인력 서류를 생성합니다.
     */
    private void createContractWorkerFiles(final OutsourcingCompanyContractWorker worker,
            final List<OutsourcingCompanyContractWorkerFileCreateRequest> files) {
        for (final OutsourcingCompanyContractWorkerFileCreateRequest fileRequest : files) {
            final OutsourcingCompanyContractWorkerFile file = OutsourcingCompanyContractWorkerFile.builder()
                    .worker(worker)
                    .fileUrl(fileRequest.fileUrl())
                    .originalFileName(fileRequest.originalFileName())
                    .build();

            workerFileRepository.save(file);
        }
    }

    /**
     * 계약 운전자 서류를 생성합니다.
     */
    private void createContractDriverFiles(final OutsourcingCompanyContractDriver driver,
            final List<OutsourcingCompanyContractDriverFileCreateRequest> files) {
        for (final OutsourcingCompanyContractDriverFileCreateRequest fileRequest : files) {
            final OutsourcingCompanyContractDriverFile file = OutsourcingCompanyContractDriverFile.builder()
                    .driver(driver)
                    .documentType(fileRequest.documentType())
                    .fileUrl(fileRequest.fileUrl())
                    .originalFileName(fileRequest.originalFileName())
                    .build();

            // 파일을 드라이버의 files 리스트에 추가
            driver.getFiles().add(file);
        }
    }

    /**
     * 계약 공사항목을 생성합니다.
     */
    private void createContractConstructions(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractContstructionCreateRequest> constructions) {
        for (final OutsourcingCompanyContractContstructionCreateRequest constructionRequest : constructions) {
            final OutsourcingCompanyContractConstruction construction = OutsourcingCompanyContractConstruction.builder()
                    .outsourcingCompanyContract(contract)
                    .item(constructionRequest.item())
                    .specification(constructionRequest.specification())
                    .unit(constructionRequest.unit())
                    .unitPrice(constructionRequest.unitPrice())
                    .contractQuantity(constructionRequest.contractQuantity())
                    .contractPrice(constructionRequest.contractPrice())
                    .outsourcingContractQuantity(constructionRequest.outsourcingContractQuantity())
                    .outsourcingContractUnitPrice(constructionRequest.outsourcingContractUnitPrice())
                    .outsourcingContractPrice(constructionRequest.outsourcingContractPrice())
                    .memo(constructionRequest.memo())
                    .build();

            constructionRepository.save(construction);
        }
    }

    /**
     * 계약 공사항목을 생성합니다. (V2 - WorkType 포함)
     */
    private void createContractConstructionsV2(final OutsourcingCompanyContract contract,
            final List<OutsourcingCompanyContractConstructionCreateRequestV2> constructionsV2) {
        for (final OutsourcingCompanyContractConstructionCreateRequestV2 workTypeRequest : constructionsV2) {
            // WorkType 생성
            final OutsourcingCompanyContractConstructionGroup constructionGroup = OutsourcingCompanyContractConstructionGroup
                    .builder()
                    .outsourcingCompanyContract(contract)
                    .itemName(workTypeRequest.itemName())
                    .build();

            final OutsourcingCompanyContractConstructionGroup savedConstructionGroup = constructionGroupRepository
                    .save(constructionGroup);

            // 해당 WorkType에 속하는 공사항목들 생성
            if (workTypeRequest.items() != null) {
                for (final OutsourcingCompanyContractContstructionCreateRequest itemRequest : workTypeRequest.items()) {
                    final OutsourcingCompanyContractConstruction construction = OutsourcingCompanyContractConstruction
                            .builder()
                            .outsourcingCompanyContract(contract)
                            .constructionGroup(savedConstructionGroup)
                            .item(itemRequest.item())
                            .specification(itemRequest.specification())
                            .unit(itemRequest.unit())
                            .unitPrice(itemRequest.unitPrice())
                            .contractQuantity(itemRequest.contractQuantity())
                            .contractPrice(itemRequest.contractPrice())
                            .outsourcingContractQuantity(itemRequest.outsourcingContractQuantity())
                            .outsourcingContractUnitPrice(itemRequest.outsourcingContractUnitPrice())
                            .outsourcingContractPrice(itemRequest.outsourcingContractPrice())
                            .memo(itemRequest.memo())
                            .build();

                    constructionRepository.save(construction);
                }
            }
        }
    }

    /**
     * 계약 보조장비를 생성합니다.
     */
    private void createContractSubEquipments(final OutsourcingCompanyContractEquipment equipment,
            final List<OutsourcingCompanyContractSubEquipmentCreateRequest> subEquipments) {
        for (final OutsourcingCompanyContractSubEquipmentCreateRequest subEquipmentRequest : subEquipments) {
            final OutsourcingCompanyContractSubEquipment subEquipment = OutsourcingCompanyContractSubEquipment.builder()
                    .equipment(equipment)
                    .type(subEquipmentRequest.type())
                    .description(subEquipmentRequest.description())
                    .unitPrice(subEquipmentRequest.unitPrice())
                    .taskDescription(subEquipmentRequest.taskDescription())
                    .memo(subEquipmentRequest.memo())
                    .build();

            subEquipmentRepository.save(subEquipment);
        }
    }

    /**
     * 계약 이력을 생성합니다.
     */
    private void createContractHistory(final OutsourcingCompanyContract contract) {
        final OutsourcingCompanyContractHistory history = OutsourcingCompanyContractHistory.builder()
                .outsourcingCompany(contract.getOutsourcingCompany())
                .contract(contract)
                .build();

        contractHistoryRepository.save(history);
    }

    /**
     * 외주업체별 계약 이력을 페이징하여 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<ContractHistoryResponse> getContractHistoryByCompany(
            final Long companyId,
            final Pageable pageable) {

        final Page<OutsourcingCompanyContractHistory> historyPage = contractHistoryRepository
                .findByOutsourcingCompanyIdWithPaging(companyId, pageable);

        // ContractHistoryResponse로 변환
        return historyPage.map(ContractHistoryResponse::from);
    }

    /**
     * 검색 조건에 따라 외주계약 리스트를 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<ContractListResponse> getContractList(final Long userId, final ContractListSearchRequest searchRequest,
            final Pageable pageable) {
        log.info("외주계약 리스트 조회 시작: searchRequest={}, pageable={}", searchRequest, pageable);

        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);

        final Page<OutsourcingCompanyContract> contractPage = contractRepository.findBySearchConditions(searchRequest,
                pageable, accessibleSiteIds);

        return contractPage.map(ContractListResponse::from);
    }

    /**
     * 외주업체 계약 목록을 엑셀로 다운로드합니다.
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(final CustomUserDetails user, final ContractListSearchRequest request,
            final Sort sort, final List<String> fields) {
        final User userEntity = userService.getUserByIdOrThrow(user.getUserId());
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(userEntity);
        final List<ContractListResponse> contractResponses = contractRepository.findAllWithoutPaging(request, sort,
                accessibleSiteIds)
                .stream()
                .map(ContractListResponse::from)
                .toList();

        final Workbook workbook = ExcelExportUtils.generateWorkbook(
                contractResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);

        final String fileUrl = s3FileService.uploadExcelToS3(workbook,
                ExcelDownloadHistoryType.OUTSOURCING_COMPANY_CONTRACT.name());

        excelDownloadHistoryService.recordDownload(
                ExcelDownloadHistoryType.OUTSOURCING_COMPANY_CONTRACT,
                userService.getUserByIdOrThrow(user.getUserId()), fileUrl);

        return workbook;
    }

    /**
     * 엑셀 헤더명을 반환합니다.
     */
    private String getExcelHeaderName(final String field) {
        return switch (field) {
            case "id" -> "No.";
            case "siteName" -> "현장명";
            case "processName" -> "공정명";
            case "companyName" -> "외주업체명";
            case "businessNumber" -> "사업자등록번호";
            case "contractType" -> "구분";
            case "contractPeriod" -> "계약기간";
            case "contractAmount" -> "계약금액(총액)";
            case "defaultDeductions" -> "공제항목";
            case "taxInvoiceCondition" -> "세금계산서 발행조건";
            case "contactName" -> "담당자";
            case "createdAt" -> "작성일자";
            case "contractStatus" -> "상태";
            case "memo" -> "비고";
            case "hasGuaranteeCertificate" -> "보증서";
            case "hasContractCertificate" -> "계약서";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값을 반환합니다.
     */
    private String getExcelCellValue(final ContractListResponse contract, final String field) {
        return switch (field) {
            case "id" -> String.valueOf(contract.id());
            case "siteName" -> contract.siteName();
            case "processName" -> contract.processName();
            case "companyName" -> contract.companyName();
            case "businessNumber" -> contract.businessNumber();
            case "contractType" -> contract.contractType();
            case "contractPeriod" -> {
                final String startDate = DateTimeFormatUtils.formatKoreaLocalDate(contract.contractStartDate());
                final String endDate = DateTimeFormatUtils.formatKoreaLocalDate(contract.contractEndDate());
                yield startDate + " ~ " + endDate;
            }
            case "contractAmount" ->
                contract.contractAmount() != null ? String.format("%,d", contract.contractAmount()) : "";
            case "defaultDeductions" -> contract.defaultDeductions();
            case "taxInvoiceCondition" -> contract.taxInvoiceCondition();
            case "contactName" -> {
                if (contract.contacts() != null && !contract.contacts().isEmpty()) {
                    yield contract.contacts().stream()
                            .map(contact -> contact.name())
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                yield "";
            }
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(contract.createdAt());
            case "contractStatus" -> contract.contractStatus();
            case "memo" -> contract.memo();
            case "hasGuaranteeCertificate" -> contract.hasGuaranteeCertificate() ? "Y" : "N";
            case "hasContractCertificate" -> contract.hasContractCertificate() ? "Y" : "N";
            default -> null;
        };
    }

    /**
     * 외주업체 계약 상세 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public ContractDetailResponse getContractDetail(final Long contractId) {
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));
        return ContractDetailResponse.from(contract);
    }

    /**
     * 외주업체 계약 인력 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractWorkerResponse> getContractWorkers(final Long contractId, final Pageable pageable) {
        // 계약이 존재하는지 확인
        if (!contractRepository.existsById(contractId)) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        final Page<OutsourcingCompanyContractWorker> page = workerRepository.findByOutsourcingCompanyContractId(
                contractId,
                pageable);
        return page.map(ContractWorkerResponse::from);
    }

    /**
     * 외주업체 계약 장비 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractEquipmentResponse> getContractEquipments(final Long contractId, final Pageable pageable) {
        // 계약이 존재하는지 확인
        if (!contractRepository.existsById(contractId)) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        final Page<OutsourcingCompanyContractEquipment> page = equipmentRepository.findByOutsourcingCompanyContractId(
                contractId,
                pageable);
        return page.map(ContractEquipmentResponse::from);
    }

    /**
     * 외주업체 계약 기사(운전자) 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractDriverResponse> getContractDrivers(final Long contractId, final Pageable pageable) {
        // 계약이 존재하는지 확인
        if (!contractRepository.existsById(contractId)) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        final Page<OutsourcingCompanyContractDriver> page = driverRepository.findByOutsourcingCompanyContractId(
                contractId,
                pageable);
        return page.map(ContractDriverResponse::from);
    }

    /**
     * 외주업체별 계약 장비 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractEquipmentResponse.ContractEquipmentSimpleResponse> getContractEquipmentsByCompany(
            final Long companyId, final Long siteId, final List<OutsourcingCompanyContractType> types,
            final Pageable pageable) {
        final List<Long> contractIds = getContractIdsByCompanyAndSite(companyId, siteId, types);

        // 계약 ID들로 장비 조회
        final Page<OutsourcingCompanyContractEquipment> page = equipmentRepository
                .findByOutsourcingCompanyContractIdIn(contractIds, pageable);
        return page.map(ContractEquipmentResponse.ContractEquipmentSimpleResponse::from);
    }

    /**
     * 외주업체별 계약 기사(운전자) 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractDriverResponse.ContractDriverSimpleResponse> getContractDriversByCompany(final Long companyId,
            final Long siteId, final Pageable pageable) {
        final List<Long> contractIds = getContractIdsByCompanyAndSite(companyId, siteId, null);

        // 계약 ID들로 기사 조회
        final Page<OutsourcingCompanyContractDriver> page = driverRepository
                .findByOutsourcingCompanyContractIdIn(contractIds, pageable);
        return page.map(ContractDriverResponse.ContractDriverSimpleResponse::from);
    }

    /**
     * 외주업체별 계약 인력 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractWorkerResponse.ContractWorkerSimpleResponse> getContractWorkersByCompany(final Long companyId,
            final Long siteId, final Pageable pageable) {
        final List<Long> contractIds = getContractIdsByCompanyAndSite(companyId, siteId, null);

        // 계약 ID들로 인력 조회
        final Page<OutsourcingCompanyContractWorker> page = workerRepository
                .findByOutsourcingCompanyContractIdIn(contractIds, pageable);
        return page.map(ContractWorkerResponse.ContractWorkerSimpleResponse::from);
    }

    /**
     * 외주업체별 계약 공사항목 그룹 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponse> getContractConstructionGroupsByCompany(
            final Long companyId, final Long siteId, final Pageable pageable) {
        final List<Long> contractIds = getContractIdsByCompanyAndSite(companyId, siteId, null);

        // 계약 ID들로 공사항목 그룹 조회
        final Page<OutsourcingCompanyContractConstructionGroup> page = constructionGroupRepository
                .findByOutsourcingCompanyContractIdIn(contractIds, pageable);
        return page.map(group -> ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponse.from(group));
    }

    /**
     * 외주업체 ID, 항목 그룹 ID, 공사항목 이름으로 규격 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<String> getSpecificationsByConditions(final String itemName, final Long constructionGroupId,
            final Long outsourcingCompanyId) {
        return constructionRepository.findDistinctSpecificationsByConditions(itemName, constructionGroupId,
                outsourcingCompanyId);
    }

    /**
     * 외주업체 계약 공사항목 그룹 정보를 Slice로 조회합니다. (V2)
     */
    @Transactional(readOnly = true)
    public Slice<ContractConstructionGroupResponse> getContractConstructionGroups(final Long contractId,
            final Pageable pageable) {
        // 계약이 존재하는지 확인
        if (!contractRepository.existsById(contractId)) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        final Page<OutsourcingCompanyContractConstructionGroup> page = constructionGroupRepository
                .findByOutsourcingCompanyContractId(contractId, pageable);
        return page.map(ContractConstructionGroupResponse::from);
    }

    /**
     * 외주업체 계약 공사항목 정보를 Slice로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<ContractConstructionResponse> getContractConstructions(final Long contractId,
            final Pageable pageable) {
        // 계약이 존재하는지 확인
        if (!contractRepository.existsById(contractId)) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        final Page<OutsourcingCompanyContractConstruction> page = constructionRepository
                .findByOutsourcingCompanyContractId(
                        contractId,
                        pageable);
        return page.map(ContractConstructionResponse::from);
    }

    /**
     * 외주업체 계약을 수정합니다.
     */
    public void updateContract(final Long contractId, final OutsourcingCompanyContractUpdateRequest request,
            final Long userId) {

        // 1. 계약이 존재하는지 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        contract.syncTransientFields();
        // 2. 변경 전 스냅샷 생성
        final OutsourcingCompanyContract oldSnapshot = JaversUtils.createSnapshot(javers, contract,
                OutsourcingCompanyContract.class);

        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());
        final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());

        // 3. 기본 계약 정보 수정 (updateFrom 메서드 사용)
        contract.updateFrom(request, site, siteProcess, outsourcingCompany);

        // 4. 변경사항 추출 및 변경 히스토리 저장
        final Diff diff = javers.compare(oldSnapshot, contract);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final OutsourcingCompanyContractChangeHistory changeHistory = OutsourcingCompanyContractChangeHistory
                    .builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.BASIC)
                    .changes(changesJson)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            contractChangeHistoryRepository.save(changeHistory);
        }

        // 5. 담당자 정보 수정
        if (request.contacts() != null) {
            contractContactService.updateContractContacts(contract.getId(), request.contacts(), userId);
        }

        // 6. 첨부파일 정보 수정
        if (request.files() != null) {
            contractFileService.updateContractFiles(contract.getId(), request.files(), userId);
        }

        // 7. 인력 정보 수정
        if (request.workers() != null) {
            contractWorkerService.updateContractWorkers(contract.getId(), request.workers(), userId);
        }

        // 8. 장비 정보 수정
        if (request.equipments() != null) {
            contractEquipmentService.updateContractEquipments(contract.getId(), request.equipments(), userId);
        }

        // 9. 운전자 정보 수정
        if (request.drivers() != null) {
            contractDriverService.updateContractDrivers(contract.getId(), request.drivers(), userId);
        }

        // 10. 공사항목 정보 수정
        if (request.constructions() != null) {
            contractConstructionService.updateContractConstructions(contract.getId(), request.constructions(), userId);
        }

        // 10. 공사항목 정보 수정 V2
        if (request.constructions() == null && request.constructionsV2() != null) {
            contractConstructionService.updateContractConstructionsV2(contract.getId(), request.constructionsV2(),
                    userId);
        }

        // 11. 사용자 정의 변경 이력 저장
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final OutsourcingCompanyContractUpdateRequest.ChangeHistoryRequest historyRequest : request
                    .changeHistories()) {
                contractChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getOutsourcingCompanyContract().getId().equals(contract.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

    }

    /**
     * 외주업체 계약들을 삭제합니다 (소프트 삭제).
     */
    public void deleteContracts(final DeleteOutsourcingCompanyContractsRequest request) {
        final List<Long> contractIds = request.contractIds();

        // 계약들이 존재하는지 확인
        final List<OutsourcingCompanyContract> contracts = contractRepository.findAllById(contractIds);

        if (contracts.size() != contractIds.size()) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        // 각 계약에 대해 소프트 삭제 처리
        for (final OutsourcingCompanyContract contract : contracts) {
            contract.markAsDeleted();
        }
    }

    /**
     * 계약 변경 이력을 조회합니다.
     */
    public Slice<OutsourcingCompanyContractChangeHistory> getContractChangeHistories(final Long contractId,
            final Pageable pageable) {
        return contractChangeHistoryRepository.findByOutsourcingCompanyContractId(contractId,
                pageable);
    }

    /**
     * 계약 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<ContractChangeHistoryResponse> getContractChangeHistoriesWithPaging(final Long contractId,
            final Pageable pageable, final Long userId) {
        final OutsourcingCompanyContract contract = getContractByIdOrThrow(contractId);

        final Page<OutsourcingCompanyContractChangeHistory> historyPage = contractChangeHistoryRepository
                .findByOutsourcingCompanyContractWithPaging(contract, pageable);
        return historyPage.map(history -> ContractChangeHistoryResponse.from(history, userId));
    }

    private List<Long> getContractIdsByCompanyAndSite(final Long companyId, final Long siteId,
            final List<OutsourcingCompanyContractType> types) {
        // 해당 외주업체와 현장의 계약 ID들을 조회
        final List<OutsourcingCompanyContractType> searchTypes = (types == null || types.isEmpty()) ? null : types;
        return contractRepository.findContractIdsByCompanyAndSiteAndTypes(companyId, siteId, searchTypes);
    }

    @Transactional(readOnly = true)
    public OutsourcingCompanyContractDriver getDriverByIdOrThrow(final Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_DRIVER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public OutsourcingCompanyContractEquipment getEquipmentByIdOrThrow(final Long equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Slice<ContractEquipmentResponse.ContractEquipmentSimpleResponse> searchVehicleNumber(final String keyword,
            final Pageable pageable) {
        final String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        final Slice<OutsourcingCompanyContractEquipment> equipmentSlice = equipmentRepository
                .findAllByVehicleNumber(searchKeyword, pageable);

        return equipmentSlice.map(ContractEquipmentResponse.ContractEquipmentSimpleResponse::from);
    }

    /**
     * 외주업체 계약 이름(계약명)으로 키워드 검색
     */
    @Transactional(readOnly = true)
    public Slice<ContractListResponse.ContractSimpleResponse> searchByName(final String keyword,
            final List<OutsourcingCompanyContractType> types, final Long outsourcingCompanyId,
            final Pageable pageable) {
        final String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        // 빈 리스트인 경우 null로 변환 (쿼리에서 모든 타입 검색하도록)
        final List<OutsourcingCompanyContractType> searchTypes = (types == null || types.isEmpty()) ? null : types;
        final Slice<OutsourcingCompanyContract> contracts = contractRepository
                .findByTypeDescriptionAndKeyword(searchKeyword, searchTypes, outsourcingCompanyId, pageable);

        return contracts.map(ContractListResponse.ContractSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public OutsourcingCompanyContract getContractByIdOrThrow(final Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));
    }
}
