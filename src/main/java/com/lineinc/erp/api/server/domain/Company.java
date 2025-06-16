package com.lineinc.erp.api.server.domain;

import com.lineinc.erp.api.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Company extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;
}