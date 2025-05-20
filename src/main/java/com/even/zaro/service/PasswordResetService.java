package com.even.zaro.service;

import com.even.zaro.dto.auth.PasswordResetEmailRequestDto;
import com.even.zaro.dto.auth.PasswordResetEmailResponseDto;
import com.even.zaro.dto.auth.PasswordResetRequestDto;
import com.even.zaro.entity.PasswordResetToken;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.PasswordResetRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;

    // 비밀번호 재설정 토큰 생성 및 메일 전송
    @Transactional
    public PasswordResetEmailResponseDto sendPasswordResetEmail(PasswordResetEmailRequestDto requestDto) {
        String email = requestDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        passwordResetRepository.deleteByEmail(email);
        passwordResetRepository.flush();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30);

        passwordResetRepository.save(new PasswordResetToken(email, token, expiredAt, false));

        emailService.sendPasswordResetEmail(email, token);

        return new PasswordResetEmailResponseDto(email);
    }

    // 메일 토큰 검증 및 비밀번호 재설정
    @Transactional
    public void resetPassword(PasswordResetRequestDto requestDto) {
        String token = requestDto.getToken();
        String newPassword = requestDto.getNewPassword();

        PasswordResetToken resetToken = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new UserException(ErrorCode.RESET_TOKEN_NOT_FOUND));

        if (resetToken.isUsed()) {
            throw new UserException(ErrorCode.RESET_TOKEN_ALREADY_USED);
        }

        if (resetToken.isExpired()) {
            throw new UserException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.markUsed();
        passwordResetRepository.save(resetToken);
    }
}
