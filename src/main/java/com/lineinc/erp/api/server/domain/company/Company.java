package com.lineinc.erp.api.server.domain.company;

import com.lineinc.erp.api.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;
}