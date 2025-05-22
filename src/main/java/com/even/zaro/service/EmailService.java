package com.even.zaro.service;

import com.even.zaro.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.verification.url}")
    private String emailVerificationUrl;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(User user, String token) {

        String verifyLink = emailVerificationUrl + "?token=" + token;
        String subject = "[ZARO] 회원가입 이메일 인증";
        String html = loadTemplate("email/email-verification-template.html", verifyLink);

        sendHtml(user.getEmail(), subject, html);
    }

    public void sendPasswordResetEmail(String email, String token) {

        String linkDev = "http://localhost:3000/password-reset?token=" + token; // 개발용 프론트 작업 완료 후 삭제 예정
        String verifyLink = frontendUrl + "/password-reset?token=" + token;
        String subject = "[ZARO] 비밀번호 재설정";
        String html = loadTemplate("email/password-reset-template.html", verifyLink, linkDev);

        sendHtml(email, subject, html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }

    private String loadTemplate(String path, String link) {
        return loadTemplate(path, link, "");
    }

    private String loadTemplate(String path, String link, String devLink) {
        try {
            var resource = new ClassPathResource(path);
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return html.replace("{VERIFY_LINK}", link)
                    .replace("{DEV_LINK}", devLink); // 없으면 공백 처리
        } catch (IOException e) {
            throw new RuntimeException("템플릿 로딩 실패", e);
        }
    }
}
