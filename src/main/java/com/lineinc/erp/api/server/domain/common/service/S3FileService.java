package com.lineinc.erp.api.server.domain.common.service;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.request.PresignedUrlRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.file.dto.response.PresignedUrlResponse;
import com.lineinc.erp.api.server.shared.enums.FileMimeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * S3 파일 업로드를 위한 Presigned URL 생성 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService {

    // 상수 정의
    private static final int PRESIGNED_URL_EXPIRY_MINUTES = 3;

    private final S3Presigner s3Presigner;

    @Value("${AWS_CDN_URL}")
    private String cdnUrl;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    /**
     * 파일 업로드를 위한 Presigned URL을 생성합니다.
     * 
     * @param request Presigned URL 생성 요청 정보
     * @return Presigned URL과 CDN 접근 URL이 포함된 응답
     * @throws IllegalArgumentException 지원하지 않는 파일 타입인 경우
     */
    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        log.debug("Presigned URL 생성 요청: contentType={}, uploadTarget={}",
                request.contentType(), request.uploadTarget());

        // 지원하는 파일 타입인지 검증
        if (!FileMimeType.isSupported(request.contentType())) {
            log.warn("지원하지 않는 파일 타입: {}", request.contentType());
            throw new IllegalArgumentException(ValidationMessages.UNSUPPORTED_CONTENT_TYPE);
        }

        // 파일 타입에서 확장자 추출 및 고유 파일명 생성
        FileMimeType mimeType = FileMimeType.fromMime(request.contentType());
        if (mimeType == null) {
            log.error("FileMimeType.fromMime()이 null을 반환: {}", request.contentType());
            throw new IllegalStateException(ValidationMessages.FILE_PROCESS_ERROR);
        }

        String uniqueFileName = generateUniqueFileName(request.uploadTarget().getDirectory(), mimeType);
        log.debug("생성된 파일명: {}", uniqueFileName);

        // S3 Presigned URL 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRY_MINUTES))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String cdnAccessUrl = cdnUrl + uniqueFileName;

        log.debug("Presigned URL 생성 완료: uploadTarget={}", request.uploadTarget());
        return PresignedUrlResponse.of(presignedRequest.url().toString(), cdnAccessUrl);
    }

    /**
     * 고유한 파일명을 생성합니다.
     * 
     * @param directory 파일이 저장될 디렉토리
     * @param mimeType  파일 MIME 타입 enum
     * @return 고유한 파일명 (디렉토리/UUID.확장자)
     */
    private String generateUniqueFileName(String directory, FileMimeType mimeType) {
        return directory + "/" + UUID.randomUUID() + mimeType.getExtension();
    }
}