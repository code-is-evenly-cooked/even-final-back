package com.even.zaro.service;

import com.even.zaro.dto.auth.SignInRequestDto;
import com.even.zaro.dto.auth.SignInResponseDto;
import com.even.zaro.dto.auth.SignUpRequestDto;
import com.even.zaro.dto.auth.SignUpResponseDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    // 상수
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[_!@#$%^&*])[A-Za-z\\d_!@#$%^&*]{6,}$";
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣_-]{2,12}$";
    // 필드, 생성자
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    // 메서드
    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String nickname = requestDto.getNickname();

        // 유효성 검사
        if (email == null || email.isBlank()) {
            throw new UserException(ErrorCode.EMAIL_REQUIRED);
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new UserException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (password == null || password.isBlank()) {
            throw new UserException(ErrorCode.PASSWORD_REQUIRED);
        }
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new UserException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (nickname == null || nickname.isBlank()) {
            throw new UserException(ErrorCode.NICKNAME_REQUIRED);
        }
        if (!Pattern.matches(NICKNAME_REGEX, nickname)) {
            throw new UserException(ErrorCode.INVALID_NICKNAME);
        }

        // 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        // 저장
        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .nickname(nickname)
                        .profileImage("https://your-cdn.com/default.png")
                        .provider(Provider.LOCAL)
                        .status(Status.PENDING)
                        .build()
        );

        return new SignUpResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }

    public SignInResponseDto signIn(SignInRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);
        }

        String[] tokens = jwtUtil.generateToken(new JwtUserInfoDto(user.getId()));
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        user.updateLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return new SignInResponseDto(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
