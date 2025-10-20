package com.youthcase.orderflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * GlobalExceptionHandler에서 클라이언트에게 반환할 표준 에러 응답 형식입니다.
 */
@Getter
@AllArgsConstructor // 핸들러에서 new ErrorResponseDTO(status, message) 형태로 생성하기 위해 필요
public class ErrorResponseDTO {

    // HTTP 상태 코드 (e.g., 400, 401)
    private final int status;

    // 클라이언트에게 보여줄 상세 에러 메시지
    private final String message;
}