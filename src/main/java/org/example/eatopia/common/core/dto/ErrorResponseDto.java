package org.example.eatopia.common.core.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;

@Builder
public record ErrorResponseDto (
    int status,
    String code,
    String message,
    String path,
    LocalDateTime timestamp,
    Map<String, String> data
) {

    public static ErrorResponseDto of(int status, String code, String message, String path) {

        return ErrorResponseDto.builder()
            .status(status)
            .code(code)
            .message(message)
            .path(path)
            .timestamp(LocalDateTime.now())
            .data(null)
            .build();
    }

    public static ErrorResponseDto of(int status, String code, String message, String path, Map<String, String> data) {

        return ErrorResponseDto.builder()
            .status(status)
            .code(code)
            .message(message)
            .path(path)
            .timestamp(LocalDateTime.now())
            .data(data)
            .build();
    }
}
