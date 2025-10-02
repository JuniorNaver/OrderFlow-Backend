package com.youthcase.orderflow.auth.dto; // 또는 global.error 패키지에 위치

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String message;

    // private String path; // 선택적: 오류가 발생한 요청 경로
}