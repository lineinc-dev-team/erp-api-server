package com.lineinc.erp.api.server.domain.labormanagement.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class LaborFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_file_seq")
    @SequenceGenerator(name = "labor_file_seq", sequenceName = "labor_file_seq", allocationSize = 1)
    private Long id;

    /**
     * 노무 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor;

    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @DiffInclude
    @Column
    private String name;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @DiffInclude
    @Column
    private String fileUrl; // S3 경로

    /**
     * 업로드된 파일의 원본 파일명
     */
    @DiffInclude
    @Column
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모

    /**
     * 노무를 설정합니다.
     */
    public void setLabor(Labor labor) {
        this.labor = labor;
    }
}
