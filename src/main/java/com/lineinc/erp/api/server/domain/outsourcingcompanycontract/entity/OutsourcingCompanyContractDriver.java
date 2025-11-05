package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractDriverUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(indexes = {
        @Index(columnList = "name")
})
public class OutsourcingCompanyContractDriver extends BaseEntity {
    private static final String SEQUENCE_NAME = "outsourcing_company_contract_driver_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffInclude
    @Column(nullable = false)
    private String name;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_ID, nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    // 드라이버 서류 목록
    @DiffInclude
    @OneToMany(mappedBy = AppConstants.OUTSOURCING_COMPANY_CONTRACT_DRIVER_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter
    private List<OutsourcingCompanyContractDriverFile> files = new ArrayList<>();

    /**
     * 운전자 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractDriverUpdateRequest request) {
        this.name = request.name();
        this.memo = request.memo();

        // 파일 정보 동기화
        if (request.files() != null) {
            EntitySyncUtils.syncList(
                    this.files,
                    request.files(),
                    (fileDto) -> OutsourcingCompanyContractDriverFile.builder()
                            .driver(this)
                            .documentType(fileDto.documentType())
                            .fileUrl(fileDto.fileUrl())
                            .originalFileName(fileDto.originalFileName())
                            .build());
        }
    }
}
