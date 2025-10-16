package org.example.eatopia.common.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

/**
 * 모든 API응답을 위한 공통 래퍼클래스
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public Response(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    //성공응답(데이터 포함)
    public static <T> Response<T> success(T data) {
        return new Response<>(true, data, null);
    }

    //성공응답(데이터 미포함)
    public static Response<Void> success() {
        return new Response<>(true, null, null);
    }

    //실패응답(ErrorCode 사용)
    public static Response<Void> error(ErrorCode errorCode) {
        return new Response<>(false, null, new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    //실패응답(코드, 메시지 직접 입력)
    public static Response<Void> error(ErrorCode errorCode, String message) {
        return new Response<>(false, null, new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
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
