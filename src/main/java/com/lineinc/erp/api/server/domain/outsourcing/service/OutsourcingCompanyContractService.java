package com.lineinc.erp.api.server.domain.outsourcing.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriverFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractHistory;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorkerFile;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractConstructionRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractContactRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractDriverFileRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractDriverRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractFileRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractSubEquipmentRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractWorkerFileRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractWorkerRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.ContractListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.DeleteOutsourcingCompanyContractsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractContactCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractContstructionCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractDriverCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractDriverFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractSubEquipmentCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractWorkerCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractWorkerFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.ContractListResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;

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
    private final OutsourcingCompanyContractDriverFileRepository driverFileRepository;
    private final OutsourcingCompanyContractEquipmentRepository equipmentRepository;
    private final OutsourcingCompanyContractDriverRepository driverRepository;
    private final OutsourcingCompanyContractConstructionRepository constructionRepository;
    private final OutsourcingCompanyContractHistoryRepository contractHistoryRepository;
    private final SiteRepository siteRepository;
    private final SiteProcessRepository siteProcessRepository;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final OutsourcingCompanyContractSubEquipmentRepository subEquipmentRepository;

    /**
     * 외주업체 계약을 생성합니다.
     */
    public void createContract(OutsourcingCompanyContractCreateRequest request) {
        log.info("외주업체 계약 생성 시작: {}", request);

        // 1. 기본 계약 정보 생성
        OutsourcingCompanyContract contract = createMainContract(request);

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
        }

        // 8. 계약 이력 생성
        createContractHistory(contract);

        log.info("외주업체 계약 생성 완료: ID={}", contract.getId());
    }

    /**
     * 기본 계약 정보를 생성합니다.
     */
    private OutsourcingCompanyContract createMainContract(OutsourcingCompanyContractCreateRequest request) {
        Site site = siteRepository.findById(request.siteId())
                .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.SITE_NOT_FOUND));

        SiteProcess siteProcess = siteProcessRepository.findById(request.siteProcessId())
                .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_FOUND));

        OutsourcingCompany outsourcingCompany = outsourcingCompanyRepository.findById(request.outsourcingCompanyId())
                .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        OffsetDateTime contractStartDate = DateTimeFormatUtils.toOffsetDateTime(request.contractStartDate());
        OffsetDateTime contractEndDate = DateTimeFormatUtils.toOffsetDateTime(request.contractEndDate());

        OutsourcingCompanyContract contract = OutsourcingCompanyContract.builder()
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
                .status(request.status() != null ? request.status() : OutsourcingCompanyContractStatus.IN_PROGRESS)
                .memo(request.memo())
                .build();

        return contractRepository.save(contract);
    }

    /**
     * 계약 담당자를 생성합니다.
     */
    private void createContractContacts(OutsourcingCompanyContract contract,
            List<OutsourcingCompanyContractContactCreateRequest> contacts) {
        for (OutsourcingCompanyContractContactCreateRequest contactRequest : contacts) {
            OutsourcingCompanyContractContact contact = OutsourcingCompanyContractContact.builder()
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
    private void createContractFiles(OutsourcingCompanyContract contract,
            List<OutsourcingCompanyContractFileCreateRequest> files) {
        for (OutsourcingCompanyContractFileCreateRequest fileRequest : files) {
            OutsourcingCompanyContractFile file = OutsourcingCompanyContractFile.builder()
                    .outsourcingCompanyContract(contract)
                    .name(fileRequest.name())
                    .fileUrl(fileRequest.fileUrl())
                    .originalFileName(fileRequest.originalFileName())
                    .memo(fileRequest.memo())
                    .build();

            fileRepository.save(file);
        }
    }

    /**
     * 계약 인력을 생성합니다.
     */
    private void createContractWorkers(OutsourcingCompanyContract contract,
            List<OutsourcingCompanyContractWorkerCreateRequest> workers) {
        for (OutsourcingCompanyContractWorkerCreateRequest workerRequest : workers) {
            OutsourcingCompanyContractWorker worker = OutsourcingCompanyContractWorker.builder()
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
    private void createContractEquipments(OutsourcingCompanyContract contract,
            List<OutsourcingCompanyContractEquipmentCreateRequest> equipments) {
        for (OutsourcingCompanyContractEquipmentCreateRequest equipmentRequest : equipments) {
            OutsourcingCompanyContractEquipment equipment = OutsourcingCompanyContractEquipment.builder()
                    .outsourcingCompanyContract(contract)
                    .specification(equipmentRequest.specification())
                    .vehicleNumber(equipmentRequest.vehicleNumber())
                    .category(equipmentRequest.category())
                    .unitPrice(equipmentRequest.unitPrice())
                    .subtotal(equipmentRequest.subtotal())
                    .taskDescription(equipmentRequest.taskDescription())
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
    private void createContractDrivers(OutsourcingCompanyContract contract,
            List<OutsourcingCompanyContractDriverCreateRequest> drivers) {
        for (OutsourcingCompanyContractDriverCreateRequest driverRequest : drivers) {
            OutsourcingCompanyContractDriver driver = OutsourcingCompanyContractDriver.builder()
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
    private void createContractWorkerFiles(OutsourcingCompanyContractWorker worker,
            List<OutsourcingCompanyContractWorkerFileCreateRequest> files) {
        for (OutsourcingCompanyContractWorkerFileCreateRequest fileRequest : files) {
            OutsourcingCompanyContractWorkerFile file = OutsourcingCompanyContractWorkerFile.builder()
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
    private void createContractDriverFiles(OutsourcingCompanyContractDriver driver,
            List<OutsourcingCompanyContractDriverFileCreateRequest> files) {
        for (OutsourcingCompanyContractDriverFileCreateRequest fileRequest : files) {
            OutsourcingCompanyContractDriverFile file = OutsourcingCompanyContractDriverFile.builder()
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
    private void createContractConstructions(OutsourcingCompanyContract contract,
            List<OutsourcingCompanyContractContstructionCreateRequest> constructions) {
        for (OutsourcingCompanyContractContstructionCreateRequest constructionRequest : constructions) {
            OutsourcingCompanyContractConstruction construction = OutsourcingCompanyContractConstruction.builder()
                    .outsourcingCompanyContract(contract)
                    .item(constructionRequest.item())
                    .specification(constructionRequest.specification())
                    .unit(constructionRequest.unit())
                    .unitPrice(constructionRequest.unitPrice())
                    .contractQuantity(constructionRequest.contractQuantity())
                    .contractPrice(constructionRequest.contractPrice())
                    .outsourcingContractQuantity(constructionRequest.outsourcingContractQuantity())
                    .outsourcingContractPrice(constructionRequest.outsourcingContractPrice())
                    .memo(constructionRequest.memo())
                    .build();

            constructionRepository.save(construction);
        }
    }

    /**
     * 계약 보조장비를 생성합니다.
     */
    private void createContractSubEquipments(OutsourcingCompanyContractEquipment equipment,
            List<OutsourcingCompanyContractSubEquipmentCreateRequest> subEquipments) {
        for (OutsourcingCompanyContractSubEquipmentCreateRequest subEquipmentRequest : subEquipments) {
            OutsourcingCompanyContractSubEquipment subEquipment = OutsourcingCompanyContractSubEquipment.builder()
                    .equipment(equipment)
                    .type(subEquipmentRequest.type())
                    .memo(subEquipmentRequest.memo())
                    .build();

            subEquipmentRepository.save(subEquipment);
        }
    }

    /**
     * 계약 이력을 생성합니다.
     */
    private void createContractHistory(OutsourcingCompanyContract contract) {
        OutsourcingCompanyContractHistory history = OutsourcingCompanyContractHistory.builder()
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
            Long companyId,
            Pageable pageable) {

        Page<OutsourcingCompanyContractHistory> historyPage = contractHistoryRepository
                .findByOutsourcingCompanyIdWithPaging(companyId, pageable);

        // ContractHistoryResponse로 변환
        return historyPage.map(ContractHistoryResponse::from);
    }

    /**
     * 검색 조건에 따라 외주계약 리스트를 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<ContractListResponse> getContractList(ContractListSearchRequest searchRequest, Pageable pageable) {
        log.info("외주계약 리스트 조회 시작: searchRequest={}, pageable={}", searchRequest, pageable);

        Page<OutsourcingCompanyContract> contractPage = contractRepository.findBySearchConditions(searchRequest,
                pageable);

        return contractPage.map(ContractListResponse::from);
    }

    /**
     * 외주업체 계약 목록을 엑셀로 다운로드합니다.
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(ContractListSearchRequest request, Sort sort, List<String> fields) {
        List<ContractListResponse> contractResponses = contractRepository.findAllWithoutPaging(request, sort)
                .stream()
                .map(ContractListResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                contractResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    /**
     * 엑셀 헤더명을 반환합니다.
     */
    private String getExcelHeaderName(String field) {
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
            case "hasFile" -> "첨부파일";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값을 반환합니다.
     */
    private String getExcelCellValue(ContractListResponse contract, String field) {
        return switch (field) {
            case "id" -> String.valueOf(contract.id());
            case "siteName" -> contract.siteName();
            case "processName" -> contract.processName();
            case "companyName" -> contract.companyName();
            case "businessNumber" -> contract.businessNumber();
            case "contractType" -> contract.contractType();
            case "contractPeriod" -> {
                String startDate = DateTimeFormatUtils.formatKoreaLocalDate(contract.contractStartDate());
                String endDate = DateTimeFormatUtils.formatKoreaLocalDate(contract.contractEndDate());
                yield startDate + " ~ " + endDate;
            }
            case "contractAmount" -> contract.contractAmount() != null ? String.valueOf(contract.contractAmount()) : "";
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
            case "hasFile" -> contract.hasFile() ? "Y" : "N";
            default -> null;
        };
    }

    /**
     * 외주업체 계약들을 삭제합니다 (소프트 삭제).
     */
    public void deleteContracts(DeleteOutsourcingCompanyContractsRequest request) {
        List<Long> contractIds = request.contractIds();

        // 계약들이 존재하는지 확인
        List<OutsourcingCompanyContract> contracts = contractRepository.findAllById(contractIds);

        if (contracts.size() != contractIds.size()) {
            throw new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND);
        }

        // 각 계약에 대해 소프트 삭제 처리
        for (OutsourcingCompanyContract contract : contracts) {
            contract.markAsDeleted();
        }
    }

}
