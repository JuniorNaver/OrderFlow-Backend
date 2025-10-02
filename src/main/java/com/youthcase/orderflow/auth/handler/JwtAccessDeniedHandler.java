package com.youthcase.orderflow.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증은 되었으나 (로그인은 했으나) 필요한 권한(Role/Authority)이 없어 접근이 거부될 때 호출됩니다.
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 1. 응답 상태 코드 및 Content Type 설정
        response.setStatus(HttpStatus.FORBIDDEN.value()); // 403 Forbidden
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 2. 클라이언트에게 전달할 오류 메시지 작성
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("error", "Forbidden");

        // 어떤 권한이 부족했는지 등에 대한 메시지를 전달할 수 있습니다.
        errorDetails.put("message", "접근 권한이 부족합니다. 필요한 권한이 부여되지 않았습니다.");
        errorDetails.put("path", request.getRequestURI());

        // 3. JSON 형태로 응답 스트림에 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}