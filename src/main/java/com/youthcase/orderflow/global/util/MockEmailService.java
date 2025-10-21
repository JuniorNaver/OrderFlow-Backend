package com.youthcase.orderflow.global.util;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("mockEmailService")
public class MockEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String text) {
        // 실제 이메일 전송 로직이 구현될 곳입니다.
        // 현재는 콘솔에 로그만 남기고 성공적으로 처리된 것처럼 동작합니다.
        log.info("--- [Mock Email Service] ---");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Content: \n{}", text);
        log.info("----------------------------");
    }

    @Override
    public void sendMimeMessage(MimeMessage mimeMessage) {
        // 🚨 MimeMessage 전송 호출을 확인하는 로그를 추가하여 구현을 완료합니다.
        log.info("--- [Mock Email Service] ---");
        log.info("MimeMessage sent (Mocked). Subject: {}", "Unknown (Check MimeMessage content)");
        log.info("----------------------------");
    }
}