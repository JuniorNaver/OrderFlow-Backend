package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * MimeMessage 객체를 직접 구성하여 발송하는 저수준(Low-Level) 메서드
     */
    @Override
    public void sendMimeMessage(MimeMessage mimeMessage) {
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("이메일 발송 중 오류 발생: " + e.getMessage());
            // 🚨 런타임 예외로 전환하여 GlobalExceptionHandler에서 처리되도록 합니다.
            throw new RuntimeException("MimeMessage 발송에 실패했습니다.", e);
        }
    }

    /**
     * 일반적인 이메일 발송 (재설정 이메일 등)을 위한 고수준(High-Level) 메서드
     */
    @Override
    public void sendEmail(String to, String subject, String content) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // MimeMessageHelper를 사용하여 메시지 구성
            // 두 번째 인자 true: 멀티파트 활성화, 세 번째 인자 "UTF-8": 인코딩 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            // 본문 내용이 HTML 형식임을 명시합니다.
            helper.setText(content, true);

            // 저수준 메서드를 호출하여 발송 로직 재사용
            sendMimeMessage(message);

            System.out.println("✅ 이메일 발송 완료. To: " + to);

        } catch (MessagingException e) {
            System.err.println("이메일 구성 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("이메일 구성에 실패했습니다.", e);
        }
    }
}