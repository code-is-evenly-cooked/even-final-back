package com.even.zaro.service;

import com.even.zaro.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.verification.url}")
    private String emailVerificationUrl;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(User user, String token) {

        String link = emailVerificationUrl + "?token=" + token;
        String subject = "[ZARO] 회원가입 이메일 인증";
        String text = """
            안녕하세요 %s님,
            아래 링크를 클릭하여 회원가입을 완료해주세요.
            %s
        """.formatted(user.getNickname(), link);

        send(user.getEmail(), subject, text);
    }

    public void sendPasswordResetEmail(String email, String token) {

        String linkDev = "http://localhost:3000/password-reset?token=" + token; // 개발용 프론트 작업 완료 후 삭제 예정
        String link = frontendUrl + "/password-reset?token=" + token;
        String subject = "[ZARO] 비밀번호 재설정";
        String text = """
            비밀번호 재설정을 원하신다면 아래 링크를 클릭해주세요.
            [local 개발용]
            %s
        
            [배포용]
            %s
        """.formatted(linkDev, link);

        send(email, subject, text);
    }

    private void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
