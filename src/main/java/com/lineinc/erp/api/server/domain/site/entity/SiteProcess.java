package com.lineinc.erp.api.server.domain.site.entity;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.UpdateSiteProcessRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
        @Index(columnList = "name"),
        @Index(columnList = "created_at")
})
public class SiteProcess extends BaseEntity {

    private static final String SEQUENCE_NAME = "site_process_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID, nullable = false)
    private Site site; // 현장

    @DiffInclude
    @Column(nullable = false)
    private String name; // 공정명

    @DiffInclude
    @Column
    private String officePhone; // 사무실 연락처

    @DiffIgnore
    @Column
    private SiteProcessStatus status; // 진행 상태

    @DiffIgnore
    @ManyToOne
    @JoinColumn
    @Setter
    private User manager;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    @Transient
    @DiffInclude
    private String managerName;

    @Transient
    @DiffInclude
    private String statusName;

    public void syncTransientFields() {
        this.managerName = this.manager != null ? this.manager.getUsername() : null;
        this.statusName = this.status != null ? this.status.getLabel() : null;
    }

    public void updateFrom(final UpdateSiteProcessRequest request, final User user) {
        this.name = request.name();
        this.officePhone = request.officePhone();
        this.status = request.status();
        this.memo = request.memo();
        this.manager = user;
        syncTransientFields();
    }
}