package com.youthcase.orderflow.auth.exception;

// 사용자 관련 예외를 위한 패키지 경로를 사용하세요.

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
}
