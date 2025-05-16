package com.even.zaro.controller;

import com.even.zaro.dto.auth.SignInRequestDto;
import com.even.zaro.dto.auth.SignInResponseDto;
import com.even.zaro.dto.auth.SignUpRequestDto;
import com.even.zaro.dto.auth.SignUpResponseDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/email/verify")
    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        authService.verifyEmailToken(token);
        response.sendRedirect(frontendUrl + "/login");
    }
}
