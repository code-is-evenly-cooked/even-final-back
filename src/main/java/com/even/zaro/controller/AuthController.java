package com.even.zaro.controller;

import com.even.zaro.dto.auth.*;
import com.even.zaro.entity.PasswordResetToken;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signup(@RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = authService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", responseDto));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signin(@RequestBody SignInRequestDto requestDto) {
        SignInResponseDto responseDto = authService.signIn(requestDto);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", responseDto));
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh 토큰을 입력해 Access Token을 재발급 받습니다. 상단 Authorize에 Refresh Token을 입력하세요.",
            security = {@SecurityRequirement(name = "refresh-token")})
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refreshToken(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken) {
        RefreshResponseDto response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("액세스 토큰 재생성을 성공했습니다.", response));
    }


    @Operation(summary = "이메일 인증 확인", description = "프론트에서 호출하지 않음. 서버에서 처리. 토큰 확인용 입니다.")
    @GetMapping("/email/verify")
    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        try {
            authService.verifyEmailToken(token);
        } catch (UserException e) {
            if (e.getErrorCode() == ErrorCode.EMAIL_TOKEN_EXPIRED) {
                response.sendRedirect(frontendUrl + "/login?status=expired");
            } else if (e.getErrorCode() == ErrorCode.EMAIL_TOKEN_ALREADY_VERIFIED) {
                response.sendRedirect(frontendUrl + "/login?status=already-verified");
            } else {
                response.sendRedirect(frontendUrl + "/login?status=error");
            }
        }
        response.sendRedirect(frontendUrl + "/login");
    }

    @Operation(summary = "이메일 인증 메일 전송", description = "회원가입 시 입력한 이메일로 인증 메일을 보냅니다.")
    @PostMapping("/email/resend")
    public ResponseEntity<ApiResponse<Void>> resendEmail(@RequestBody ResendEmailRequestDto requestDto) {
        authService.resendVerificationEmail(requestDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("이메일 인증 메일이 재전송되었습니다."));
    }

    @PostMapping("/email/password")
    public ResponseEntity<ApiResponse<PasswordResetEmailResponseDto>> sendResetEmail(
            @RequestBody PasswordResetEmailRequestDto requestDto) {
        authService.sendPasswordResetEmail(requestDto);
        PasswordResetEmailResponseDto response = authService.sendPasswordResetEmail(requestDto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 메일이 전송되었습니다.", response));
    }
}
