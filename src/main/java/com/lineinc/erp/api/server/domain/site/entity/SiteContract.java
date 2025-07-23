package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteContractUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;  // 현장

    @Column(nullable = false)
    private String name;  // 계약명

    @Column
    private Long amount;  // 계약금액

    @Column
    private String memo;  // 비고

    @Setter
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