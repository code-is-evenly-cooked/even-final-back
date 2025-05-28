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
import jakarta.servlet.http.Cookie;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String token = jwtUtil.resolveToken(request);

        // Authorization 헤더 없을 경우 쿠키에서 추출 (SSE)
        if (token == null) {
            token = extractTokenFromCookies(request);
        }

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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/refresh");
    }

    // 쿠키에서 access_token 꺼내기
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
