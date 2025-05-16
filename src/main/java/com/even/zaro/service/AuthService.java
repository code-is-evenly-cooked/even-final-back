package com.even.zaro.service;

import com.even.zaro.dto.auth.*;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.repository.EmailTokenRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
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
    private final EmailVerifyService emailService;
    private final EmailTokenRepository emailTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;


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

        // 이메일 인증 메일 발송
        sendEmailVerification(user);

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
                user.getProfileImage(),
                user.getProvider()
        );
    }

    @Transactional
    public RefreshResponseDto refreshAccessToken(String refreshToken) {
        String token = jwtUtil.extractBearerPrefix(refreshToken);

        if (!jwtUtil.validateRefreshToken(token)) {
            throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtUtil.getUserIdFromRefreshToken(token);

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        String savedToken = redisTemplate.opsForValue().get("refresh:" + userId);

        if (savedToken == null || !savedToken.equals(token)) {
            throw new UserException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(new JwtUserInfoDto(user.getId()));

        return new RefreshResponseDto(newAccessToken);
    }

    @Transactional
    public void sendEmailVerification(User user) {
        emailTokenRepository.deleteByUser(user);
        emailTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30);
        emailTokenRepository.save(new EmailToken(token, user, expiredAt));

        emailService.sendVerificationEmail(user, token);
    }

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

        emailToken.verify();
        emailTokenRepository.save(emailToken);
        emailToken.getUser().verify(); // PENDING->ACTIVE user 엔티티 메서드
        userRepository.save(emailToken.getUser());
    }

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
