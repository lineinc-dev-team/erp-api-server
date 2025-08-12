package com.lineinc.erp.api.server.interfaces.rest.v1.file.controller;

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.request.PresignedUrlRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.response.PresignedUrlResponse;
import com.lineinc.erp.api.server.shared.annotation.RateLimit;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "파일 업로드 관련 API")
public class FileController {

    private final S3FileService s3FileService;

    @Operation(
            summary = "S3 Presigned URL 발급",
            description = "AWS S3 업로드용 presigned URL을 발급합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류", content = @Content())
    })
    @RateLimit(limit = 20) // 사용자당 1분에 20번 허용
    @PostMapping("/upload-url")
    public ResponseEntity<SuccessResponse<PresignedUrlResponse>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        return ResponseEntity.ok(SuccessResponse.of(
                s3FileService.generatePresignedUrl(request)
        ));
    }
}