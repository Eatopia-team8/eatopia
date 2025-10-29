package org.example.eatopia.common.infra.s3;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.common.infra.s3.dto.request.S3PresignedUrlRequest;
import org.example.eatopia.common.infra.s3.dto.response.S3PresignedUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    // 단건
    @GetMapping("/v1/s3/presigned-url")
    public ResponseEntity<Response<S3PresignedUrlResponse>> getPresignedUrl(@RequestParam String fileName,
                                                                            @RequestParam String contentType
    ) {
        S3PresignedUrlResponse response = s3Service.createPresignedUrl(fileName, contentType);
        return ResponseEntity.ok(Response.success(response));
    }

    // 다중
    @PostMapping("/v1/s3/presigned-urls")
    public ResponseEntity<Response<S3PresignedUrlResponse>> getPresignedUrls(@RequestBody @Valid S3PresignedUrlRequest request
    ) {
        S3PresignedUrlResponse response = s3Service.createPresignedUrls(request);
        return ResponseEntity.ok(Response.success(response));
    }
}
