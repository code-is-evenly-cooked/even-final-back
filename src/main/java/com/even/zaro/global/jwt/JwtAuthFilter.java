package com.even.zaro.global.jwt;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String token = jwtUtil.resolveToken(request);

//        // Authorization 헤더 없을 경우 쿠키에서 추출 (SSE)
//        if (token == null) {
//            token = jwtUtil.extractTokenFromCookies(request);
//        }

        // 토큰 유효성 검사
        if (token != null && jwtUtil.validateAccessToken(token)) {

            // 로그아웃(블랙리스트) 체크
            if (redisTemplate.hasKey("BL:" + token)) {
                throw new AuthenticationException("BLACKLISTED") {};
            }

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
}
