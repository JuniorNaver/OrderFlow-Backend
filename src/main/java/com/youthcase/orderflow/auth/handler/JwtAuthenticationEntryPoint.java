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

        // 1. 응답 상태 코드 및 Content Type 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 2. 클라이언트에게 전달할 오류 메시지 작성
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("error", "Unauthorized");

        // authException.getMessage()를 통해 구체적인 오류를 전달할 수 있습니다.
        errorDetails.put("message", "인증 정보(JWT 토큰)가 없거나 유효하지 않습니다.");
        errorDetails.put("path", request.getRequestURI());

        // 3. JSON 형태로 응답 스트림에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}