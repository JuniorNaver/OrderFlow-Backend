package com.youthcase.orderflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * GlobalExceptionHandler에서 클라이언트에게 반환할 표준 에러 응답 형식입니다.
 */
@Getter
@Setter
@AllArgsConstructor // 핸들러에서 new ErrorResponseDTO(status, message) 형태로 생성하기 위해 필요
public class ErrorResponseDTO {

    // HTTP 상태 코드 (e.g., 400, 401)
    private final int status;

    // 클라이언트에게 보여줄 상세 에러 메시지
    private final String message;

    // 발생한 요청 경로 (선택적이지만, API 응답에 유용)
    // 현재 GlobalExceptionHandler에서 사용되지 않으므로 제거하거나, 생성자에서 제거하고 Setter로 설정할 수 있습니다.
    // 여기서는 SimpleAllArgsConstructor를 위해 두 필드만 남기거나, 핸들러를 수정해야 합니다.

    /* * GlobalExceptionHandler에서 new ErrorResponseDTO(int, String) 형태로 사용하고 있으므로,
     * 현재 DTO는 status와 message 두 필드만 가진다고 가정합니다.
     */
}