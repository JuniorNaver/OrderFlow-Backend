// 📁 com.youthcase.orderflow.auth.exception.InvalidEmailException.java

package com.youthcase.orderflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 비밀번호 초기화 요청 시, 사용자가 제공한 이메일이 
 * 등록된 사용자 정보의 이메일과 일치하지 않을 때 발생하는 예외.
 * * HTTP 상태 코드 400 Bad Request를 반환합니다.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }

    // 이메일 불일치 시 사용자 정보를 유추하지 못하도록 일반적인 메시지를 사용할 수 있습니다.
    public InvalidEmailException() {
        super("제공된 정보가 사용자 기록과 일치하지 않습니다.");
    }
}