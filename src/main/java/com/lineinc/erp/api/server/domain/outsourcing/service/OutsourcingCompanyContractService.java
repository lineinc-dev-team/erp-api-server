package com.lineinc.erp.api.server.domain.outsourcing.service;

import com.lineinc.erp.api.server.domain.outsourcing.entity.*;
import com.lineinc.erp.api.server.domain.outsourcing.repository.*;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.*;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OutsourcingCompanyContractService {
    
    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractContactRepository contactRepository;
    private final OutsourcingCompanyContractFileRepository fileRepository;
    private final OutsourcingCompanyContractWorkerRepository workerRepository;
    private final OutsourcingCompanyContractEquipmentRepository equipmentRepository;
    private final OutsourcingCompanyContractDriverRepository driverRepository;
    private final OutsourcingCompanyContractConstructionRepository constructionRepository;
    private final SiteRepository siteRepository;
    private final SiteProcessRepository siteProcessRepository;
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    
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
        
        OutsourcingCompanyContract contract = OutsourcingCompanyContract.builder()
                .site(site)
                .siteProcess(siteProcess)
                .outsourcingCompany(outsourcingCompany)
                .type(request.type())
                .typeDescription(request.typeDescription())
                .contractStartDate(request.contractStartDate() != null ? 
                        request.contractStartDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null)
                .contractEndDate(request.contractEndDate() != null ? 
                        request.contractEndDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null)
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
                    .fileName(workerRequest.fileName())
                    .fileUrl(workerRequest.fileUrl())
                    .originalFileName(workerRequest.originalFileName())
                    .memo(workerRequest.memo())
                    .build();
            
            workerRepository.save(worker);
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
                    .driverLicenseName(driverRequest.driverLicenseName())
                    .driverLicenseFileUrl(driverRequest.driverLicenseFileUrl())
                    .driverLicenseOriginalFileName(driverRequest.driverLicenseOriginalFileName())
                    .safetyEducationName(driverRequest.safetyEducationName())
                    .safetyEducationFileUrl(driverRequest.safetyEducationFileUrl())
                    .safetyEducationOriginalFileName(driverRequest.safetyEducationOriginalFileName())
                    .etcDocumentName(driverRequest.etcDocumentName())
                    .etcDocumentFileUrl(driverRequest.etcDocumentFileUrl())
                    .etcDocumentOriginalFileName(driverRequest.etcDocumentOriginalFileName())
                    .memo(driverRequest.memo())
                    .build();
            
            driverRepository.save(driver);
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
}

