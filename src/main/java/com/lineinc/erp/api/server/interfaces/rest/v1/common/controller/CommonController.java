package com.lineinc.erp.api.server.interfaces.rest.v1.common.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lineinc.erp.api.server.domain.common.enums.BankType;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/common")
@Tag(name = "공통")
public class CommonController {

    @Operation(summary = "은행 목록 조회")
    @GetMapping("/banks")
    public ResponseEntity<SuccessResponse<List<String>>> getBanks(
            @RequestParam(required = false) final String keyword) {
        final List<String> banks = Arrays.stream(BankType.values()).map(BankType::getLabel)
                .filter(label -> keyword == null || keyword.isBlank() || label.contains(keyword)).toList();

        return ResponseEntity.ok(SuccessResponse.of(banks));
    }
}
