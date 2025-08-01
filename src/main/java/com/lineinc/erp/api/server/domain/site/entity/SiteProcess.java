package com.lineinc.erp.api.server.domain.site.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteProcessUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SiteProcess extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_process_seq")
    @SequenceGenerator(name = "site_process_seq", sequenceName = "site_process_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;  // 현장

    @Column(nullable = false)
    private String name;  // 공정명

    @Column
    private String officePhone;  // 사무실 연락처

    @Column
    private SiteProcessStatus status;  // 진행 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @Setter
    private User manager;

    @Column(columnDefinition = "TEXT")
    private String memo;  // 비고

    public void updateFrom(SiteProcessUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.officePhone()).ifPresent(val -> this.officePhone = val);
        Optional.ofNullable(request.status()).ifPresent(val -> this.status = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}