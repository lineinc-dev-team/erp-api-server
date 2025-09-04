package com.lineinc.erp.api.server.domain.outsourcingcontract.entity;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractWorkerUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class OutsourcingCompanyContractWorker extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_worker_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_worker_seq", sequenceName = "outsourcing_company_contract_worker_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    @DiffInclude
    @Column(nullable = false)
    private String name;

    @DiffInclude
    @Column
    private String category;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String taskDescription; // 작업내용

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    // 인력 서류 목록
    @DiffInclude
    @Setter
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractWorkerFile> files = new ArrayList<>();

    /**
     * DTO의 정보로 엔티티를 업데이트합니다.
     */
    public void updateFrom(OutsourcingCompanyContractWorkerUpdateRequest request) {
        if (request.name() != null) {
            this.name = request.name();
        }
        if (request.category() != null) {
            this.category = request.category();
        }
        if (request.taskDescription() != null) {
            this.taskDescription = request.taskDescription();
        }
        if (request.memo() != null) {
            this.memo = request.memo();
        }

        // 파일 정보 동기화
        if (request.files() != null) {
            EntitySyncUtils.syncList(
                    this.files,
                    request.files(),
                    (fileDto) -> OutsourcingCompanyContractWorkerFile.builder()
                            .worker(this)
                            .fileUrl(fileDto.fileUrl())
                            .originalFileName(fileDto.originalFileName())
                            .build());
        }
    }
}