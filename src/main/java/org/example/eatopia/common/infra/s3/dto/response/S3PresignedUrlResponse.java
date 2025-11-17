package org.example.eatopia.common.infra.s3.dto.response;

import java.util.List;

public record S3PresignedUrlResponse(

        List<PresignedUrlInfo> presignedUrls
) {

    public static S3PresignedUrlResponse from(List<PresignedUrlInfo> presignedUrls) {

        return new S3PresignedUrlResponse(presignedUrls);
    }

    public record PresignedUrlInfo(
            String fileName,
            String uploadUrl,
            String imageUrl,
            String key
    ) {
    }
}
