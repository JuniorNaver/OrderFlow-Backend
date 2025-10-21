package com.youthcase.orderflow.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련 오류 (401 Unauthorized)
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "아이디, 비밀번호 또는 워크스페이스가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A002", "사용자를 찾을 수 없습니다."),
    USER_ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "A003", "비활성화된 계정입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않거나 만료된 Refresh Token입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 토큰입니다."),

    // 비즈니스 로직 오류 (409 Conflict)
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "B001", "이미 존재하는 사용자 ID입니다."),

    // 서버 오류 (500 Internal Server Error)
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "이메일 발송에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}