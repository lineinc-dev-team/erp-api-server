package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;

public interface FuelInfoWithReportDate {
    FuelInfo getFuelInfo();

    OffsetDateTime getReportDate();
}
