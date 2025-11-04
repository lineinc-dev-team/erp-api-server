package com.lineinc.erp.api.server.domain.role.entity;

import java.io.Serial;
import java.io.Serializable;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "role_id,site_id"),
        @Index(columnList = "role_id,process_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class RoleSiteProcess extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final String SEQUENCE_NAME = "role_site_process_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.ROLE_ID, nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = AppConstants.SITE_ID)
    private Site site;

    @ManyToOne
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    private SiteProcess process;
}
