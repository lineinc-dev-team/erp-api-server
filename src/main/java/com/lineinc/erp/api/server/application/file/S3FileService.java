package com.lineinc.erp.api.server.application.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.cdn-url}")
    private String cdnUrl;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public Map<String, Object> generatePresignedUrl(String contentType) {
        String uniqueFileName = "temp/" + UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String cdnAccessUrl = cdnUrl.endsWith("/")
                ? cdnUrl + uniqueFileName
                : cdnUrl + "/" + uniqueFileName;

        Map<String, Object> response = new HashMap<>();
        response.put("uploadUrl", presignedRequest.url().toString());
        response.put("url", cdnAccessUrl);
        response.put("key", uniqueFileName);

        return response;
    }
}