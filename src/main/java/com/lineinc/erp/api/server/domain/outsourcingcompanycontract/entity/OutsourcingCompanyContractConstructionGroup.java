package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionUpdateRequestV2;
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
public class OutsourcingCompanyContractConstructionGroup extends BaseEntity {

    static final String SEQUENCE_NAME = "outsourcing_company_contract_construction_group_seq";

    @Id
    @DiffInclude
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @DiffInclude
    @Column(nullable = false)
    private String itemName; // 항목명

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_id", nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    @Builder.Default
    @DiffInclude
    @OneToMany(mappedBy = "constructionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<OutsourcingCompanyContractConstruction> constructions = new ArrayList<>(); // 공사항목 목록

    /**
     * 공사항목 그룹 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractConstructionUpdateRequestV2 request) {
        this.itemName = request.itemName();

        // 공사항목 정보 동기화
        if (request.items() != null) {
            EntitySyncUtils.syncList(
                    this.constructions,
                    request.items(),
                    (constructionDto) -> OutsourcingCompanyContractConstruction.builder()
                            .outsourcingCompanyContract(this.outsourcingCompanyContract)
                            .constructionGroup(this)
                            .item(constructionDto.item())
                            .specification(constructionDto.specification())
                            .unit(constructionDto.unit())
                            .unitPrice(constructionDto.unitPrice())
                            .contractQuantity(constructionDto.contractQuantity())
                            .contractPrice(constructionDto.contractPrice())
                            .outsourcingContractQuantity(constructionDto.outsourcingContractQuantity())
                            .outsourcingContractUnitPrice(constructionDto.outsourcingContractUnitPrice())
                            .outsourcingContractPrice(constructionDto.outsourcingContractPrice())
                            .memo(constructionDto.memo())
                            .build());
        }
    }
}