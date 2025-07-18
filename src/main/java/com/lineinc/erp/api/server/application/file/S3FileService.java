package com.lineinc.erp.api.server.application.file;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.enums.FileMimeType;
import com.lineinc.erp.api.server.presentation.v1.file.dto.request.PresignedUrlRequest;
import com.lineinc.erp.api.server.presentation.v1.file.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.cdn-url}")
    private String cdnUrl;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        if (!FileMimeType.isSupported(request.contentType())) {
            throw new IllegalArgumentException(ValidationMessages.UNSUPPORTED_CONTENT_TYPE);
        }

        FileMimeType mimeType = FileMimeType.fromMime(request.contentType());
        String uniqueFileName = request.uploadTarget().getDirectory() + "/" + UUID.randomUUID() + mimeType.getExtension();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String cdnAccessUrl = cdnUrl + uniqueFileName;

        return PresignedUrlResponse.of(presignedRequest.url().toString(), cdnAccessUrl);
    }
}