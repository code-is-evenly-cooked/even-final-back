package com.even.zaro.jwt;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String token = jwtUtil.resolveToken(request);

        // 토큰 유효성 검사
        if (token != null && jwtUtil.validateAccessToken(token)) {
            Claims claims = jwtUtil.parseClaims(token);
            Long userId = Long.valueOf(claims.getSubject());

            // 인증 객체 생성
            JwtUserInfoDto userInfo = new JwtUserInfoDto(userId);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfo, null, null);

            // 시큐리티 컨텍스트에 저장
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request,response);
    }
}
