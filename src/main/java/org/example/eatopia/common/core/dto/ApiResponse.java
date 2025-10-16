package org.example.eatopia.common.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

/**
 * 모든 API응답을 위한 공통 래퍼클래스
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    //성공응답(데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    //성공응답(데이터 미포함)
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    //실패응답(ErrorCode 사용)
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, new ErrorResponse(errorCode));
    }

    //실패응답(코드, 메시지 직접 입력)
    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(errorCode));
    }

    /**
     * 에러 정보를 담는 내부 클래스
     */
    @Getter
    private static class ErrorResponse {

        private final String code;
        private final String message;

        ErrorResponse(ErrorCode errorCode) {
            this.code = errorCode.getCode();
            this.message = errorCode.getMessage();
        }

        ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
