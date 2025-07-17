package com.lineinc.erp.api.server.presentation.v1.file.controller;

import com.lineinc.erp.api.server.application.file.S3FileService;
import com.lineinc.erp.api.server.common.response.SuccessResponse;
import com.lineinc.erp.api.server.presentation.v1.file.dto.response.PresignedUrlResponse;
import com.lineinc.erp.api.server.presentation.v1.menu.dto.response.MenuWithPermissionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "파일 업로드 관련 API")
public class FileController {

    private final S3FileService s3FileService;

    @Operation(
            summary = "S3 Presigned URL 발급",
            description = "지정한 파일 이름과 Content-Type에 대해 AWS S3 업로드용 presigned URL을 발급합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류", content = @Content())
    })
    @GetMapping("/upload-url")
    public ResponseEntity<SuccessResponse<PresignedUrlResponse>> getPresignedUrl(
            @RequestParam @NotBlank String contentType
    ) {
        Map<String, Object> stringObjectMap = s3FileService.generatePresignedUrl(contentType);
        PresignedUrlResponse response = new PresignedUrlResponse(stringObjectMap);
        return ResponseEntity.ok(SuccessResponse.of(response));

    }
}