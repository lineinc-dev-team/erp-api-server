package com.lineinc.erp.api.server.domain.company.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}