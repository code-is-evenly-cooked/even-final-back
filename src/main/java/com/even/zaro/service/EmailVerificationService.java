package com.even.zaro.service;

import com.even.zaro.entity.EmailToken;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.EmailTokenRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailTokenRepository emailTokenRepository;

    // 회원가입 인증 메일 토큰 발급
    @Transactional
    public void sendEmailVerification(User user) {
        emailTokenRepository.deleteByUser(user);
        emailTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30);
        emailTokenRepository.save(new EmailToken(token, user, expiredAt));

        emailService.sendVerificationEmail(user, token);
    }

    // 회원가입 인증 메일 토큰 검증 및 status 변경
    @Transactional
    public void verifyEmailToken(String token) {
        EmailToken emailToken = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_TOKEN_NOT_FOUND));

        if (emailToken.isVerified()) {
            throw new UserException(ErrorCode.EMAIL_TOKEN_ALREADY_VERIFIED);
        }

        if (emailToken.isExpired()) {
            throw new UserException(ErrorCode.EMAIL_TOKEN_EXPIRED);
        }

        User user = emailToken.getUser();

        emailToken.verify();
        emailTokenRepository.save(emailToken);
        user.updateStatusToActive();
        userRepository.save(user);
    }

    // 회원가입 인증 메일 재전송
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND));

        if (user.getStatus() == Status.ACTIVE) {
            throw new UserException(ErrorCode.EMAIL_TOKEN_ALREADY_VERIFIED);
        }

        sendEmailVerification(user);
    }
}
