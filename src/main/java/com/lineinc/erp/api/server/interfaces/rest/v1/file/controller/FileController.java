package com.lineinc.erp.api.server.interfaces.rest.v1.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.interfaces.rest.common.BaseController;
import com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.request.PresignedUrlRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.response.PresignedUrlResponse;
import com.lineinc.erp.api.server.shared.dto.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "파일 관리", description = "파일 업로드 관련 API")
public class FileController extends BaseController {

    private final S3FileService s3FileService;

    @Operation(summary = "S3 Presigned URL 발급", description = "AWS S3 업로드용 presigned URL을 발급합니다")
    @PostMapping("/upload-url")
    public ResponseEntity<SuccessResponse<PresignedUrlResponse>> getPresignedUrl(
            @Valid @RequestBody final PresignedUrlRequest request) {
        return ResponseEntity.ok(SuccessResponse.of(
                s3FileService.generatePresignedUrl(request)));
    }
}