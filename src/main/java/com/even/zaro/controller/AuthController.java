package com.even.zaro.controller;

import com.even.zaro.dto.auth.SignInRequestDto;
import com.even.zaro.dto.auth.SignInResponseDto;
import com.even.zaro.dto.auth.SignUpRequestDto;
import com.even.zaro.dto.auth.SignUpResponseDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signup(@RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = authService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", responseDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signin(@RequestBody SignInRequestDto requestDto) {
        SignInResponseDto responseDto = authService.signIn(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("로그인에 성공했습니다.", responseDto));
    }
}
