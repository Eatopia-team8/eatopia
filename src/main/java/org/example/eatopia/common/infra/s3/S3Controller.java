package org.example.eatopia.common.infra.s3;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("v1/s3/presigned-url")
    public ResponseEntity<Response<String>> getPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        String url = s3Service.createPresignedUrl(fileName, contentType);
        return ResponseEntity.ok(Response.success(url));
    }
}
