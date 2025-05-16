package com.even.zaro.service;

import com.even.zaro.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerifyService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.verification.url}")
    private String emailVerificationUrl;

    @Override
    public void sendVerificationEmail(User user, String token) {

        String link = emailVerificationUrl + "?token=" + token;
        String subject = "[ZARO] 회원가입 이메일 인증";
        String text = "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n" + link;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
