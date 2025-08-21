package com.lineinc.erp.api.server.domain.labormanagement.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborChangeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class LaborChangeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_change_history_seq")
    @SequenceGenerator(name = "labor_change_history_seq", sequenceName = "labor_change_history_seq", allocationSize = 1)
    private Long id;

    /**
     * 노무 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor;

    /**
     * 변경 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LaborChangeType type;

    /**
     * 변경사항 JSON
     */
    @Column(columnDefinition = "TEXT")
    private String changes;

    /**
     * 메모
     */
    @Column(columnDefinition = "TEXT")
    private String memo;
}
