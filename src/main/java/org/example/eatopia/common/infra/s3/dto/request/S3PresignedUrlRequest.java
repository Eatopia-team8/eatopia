package org.example.eatopia.common.infra.s3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record S3PresignedUrlRequest(

        @NotEmpty(message = "파일 정보는 최소 1개 이상 필요합니다.")
        List<FileInfo> files
) {
    public record FileInfo(

            @NotBlank(message = "파일명은 필수입니다.")
            String fileName,

            @NotBlank(message = "Content-Type은 필수입니다.")
            String contentType
    ) {
        // 이미지 파일 검증
        public void validateImageFile() {

            if (!contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            List<String> allowedTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");
            if (!allowedTypes.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다. (jpeg, jpg, png, webp만 가능)");
            }
        }
    }
}
