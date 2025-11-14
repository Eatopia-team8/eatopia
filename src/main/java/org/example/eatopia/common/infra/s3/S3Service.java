package org.example.eatopia.common.infra.s3;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.common.infra.s3.dto.request.S3PresignedUrlRequest;
import org.example.eatopia.common.infra.s3.dto.response.S3PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private static final int MAX_FILE_COUNT = 10;

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        // S3Client가 IAM Task Role을 사용하도록 수정
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build();
        log.info("S3Client 초기화 완료 - region: {}, bucket: {}", region, bucketName);
    }

    @PreDestroy
    public void destroy() {
        if (s3Client != null) {
            s3Client.close();
            log.info("S3Client 종료 완료");
        }
    }

    // 단건
    public S3PresignedUrlResponse createPresignedUrl(String fileName, String contentType) {
        S3PresignedUrlRequest.FileInfo fileInfo = new S3PresignedUrlRequest.FileInfo(fileName, contentType);
        fileInfo.validateImageFile();
        return generatePresignedUrls(List.of(fileInfo));
    }

    // 다중
    public S3PresignedUrlResponse createPresignedUrls(S3PresignedUrlRequest request) {
        validateFileCount(request.files().size());
        request.files().forEach(S3PresignedUrlRequest.FileInfo::validateImageFile);
        return generatePresignedUrls(request.files());
    }

    // 공통 presigned URL 생성
    private S3PresignedUrlResponse generatePresignedUrls(List<S3PresignedUrlRequest.FileInfo> files) {
        List<S3PresignedUrlResponse.PresignedUrlInfo> urls = files.stream()
                .map(file -> {
                    String uniqueName = createUniqueFileName(file.fileName());
                    String key = "products/" + uniqueName;

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.contentType())
                            .build();

                    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(builder ->
                            builder.signatureDuration(Duration.ofMinutes(5))
                                    .putObjectRequest(putObjectRequest)
                    );

                    URL uploadUrl = presignedRequest.url();
                    String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

                    log.info("Presigned URL 생성 완료 - key: {}, fileName: {}", key, file.fileName());

                    return new S3PresignedUrlResponse.PresignedUrlInfo(
                            file.fileName(),
                            uploadUrl.toString(),
                            imageUrl,
                            key
                    );
                })
                .toList();

        return S3PresignedUrlResponse.from(urls);
    }

    // 파일명 유효화 + UUID 추가
    private String createUniqueFileName(String originalFileName) {
        String sanitized = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "_" + sanitized;
    }

    // 최대 파일 개수 검증
    private void validateFileCount(int fileCount) {
        if (fileCount > MAX_FILE_COUNT) {
            throw new IllegalArgumentException("파일은 최대 " + MAX_FILE_COUNT + "개까지 업로드 가능합니다.");
        }
    }

    // S3 파일 삭제 (CloudFront 대응)
    public void deleteFile(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);

            if (key == null || key.isEmpty()) {
                log.error("S3 key 추출 실패 - imageUrl: {}", imageUrl);
                return;
            }

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);

            log.info("S3 파일 삭제 완료 - key: {}", key);
        } catch (software.amazon.awssdk.core.exception.SdkException e) {
            log.error("S3 파일 삭제 실패 - imageUrl: {}, error: {}", imageUrl, e.getMessage(), e);
        }
    }

    // URL에서 key 추출 (CloudFront 및 S3 URL 모두 대응)
    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        // 이미 key만 있는 경우
        if (!imageUrl.startsWith("http")) {
            return imageUrl;
        }

        try {
            java.net.URI uri = new java.net.URI(imageUrl);
            String path = uri.getPath();

            // path의 맨 앞 '/' 제거
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            // S3 Path-style URL은 경로에 버킷 이름이 포함되므로 제거
            String bucketPrefix = bucketName + "/";
            if (path.startsWith(bucketPrefix)) {
                return path.substring(bucketPrefix.length());
            }

            // CloudFront URL 또는 S3 Virtual-hosted-style URL의 경우
            // '/'가 제거된 경로 자체가 key
            return path;

        } catch (java.net.URISyntaxException e) {
            log.error("Key 추출 중 URI 구문 분석 오류 발생 - imageUrl: {}", imageUrl, e);
            return null;
        }
    }
}