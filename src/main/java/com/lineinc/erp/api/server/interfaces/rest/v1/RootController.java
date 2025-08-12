package com.lineinc.erp.api.server.interfaces.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "health")
public class RootController {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content()),
    })
    @Operation(summary = "ERP API 서버 상태 확인용 루트 엔드포인트")
    @GetMapping("/")
    public String home() {
        return "ERP API Server is running.";
    }
}