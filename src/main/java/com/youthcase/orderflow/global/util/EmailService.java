package com.youthcase.orderflow.global.util;

import jakarta.mail.internet.MimeMessage;

public interface EmailService {

    /**
     * 지정된 주소로 이메일을 발송합니다.
     * @param to 수신자 이메일 주소
     * @param subject 이메일 제목
     * @param content 이메일 본문 내용 (HTML 또는 일반 텍스트)
     */
    void sendEmail(String to, String subject, String content);

    /**
     * MimeMessage 객체를 직접 구성하여 발송합니다.
     * (HTML 본문, 첨부 파일 등 복잡한 구성을 위해 사용됩니다.)
     * @param mimeMessage 발송할 MimeMessage 객체
     */
    void sendMimeMessage(MimeMessage mimeMessage); // 🚨 추가된 메서드
}