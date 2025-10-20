package com.youthcase.orderflow.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증되지 않은 사용자(토큰 없음, 토큰 만료, 토큰 오류 등)가 보호된 리소스에 접근할 때 호출됩니다.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 💡 1. Filter에서 설정한 예외 Attribute 확인
        String exception = (String) request.getAttribute("jwt_exception");

        // 최종 응답 메시지를 저장할 변수
        String errorMessage;

        if (exception != null) {
            // Filter에서 명시적으로 오류가 설정된 경우 (유효하지 않은 토큰)
            errorMessage = exception;
        } else {
            // SecurityContext에 인증 정보가 없어 EntryPoint가 호출된 일반적인 경우 (토큰 없음)
            errorMessage = "인증 정보(JWT 토큰)가 없거나 유효하지 않습니다.";
        }

        // 2. 응답 상태 코드 및 Content Type 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 3. 클라이언트에게 전달할 오류 메시지 작성
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", errorMessage); // 💡 동적으로 메시지 설정
        errorDetails.put("path", request.getRequestURI());

        // 4. JSON 형태로 응답 스트림에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}