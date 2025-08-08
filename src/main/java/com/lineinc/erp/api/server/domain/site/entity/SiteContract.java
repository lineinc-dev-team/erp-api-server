package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.sitecontract.SiteContractUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SiteContract extends BaseEntity implements UpdatableFrom<SiteContractUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_contract_seq")
    @SequenceGenerator(name = "site_contract_seq", sequenceName = "site_contract_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;  // 현장

    @Column(nullable = false)
    @DiffInclude
    private String name;  // 계약명

    @Column
    @DiffInclude
    private Long amount;  // 계약금액

    @Column
    @DiffInclude
    private String memo;  // 비고

    @Setter
    @DiffInclude
    @Builder.Default
    @OneToMany(mappedBy = "siteContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteFile> files = new ArrayList<>();

    @Override
    public void updateFrom(SiteContractUpdateRequest request) {
        this.name = request.name();
        this.amount = request.amount();
        this.memo = request.memo();

        if (request.files() != null) {
            EntitySyncUtils.syncList(
                    this.files,
                    request.files(),
                    (fileDto) -> SiteFile.builder()
                            .siteContract(this)
                            .name(fileDto.name())
                            .fileUrl(fileDto.fileUrl())
                            .originalFileName(fileDto.originalFileName())
                            .memo(fileDto.memo())
                            .type(fileDto.type())
                            .build()
            );
        }
    }
}