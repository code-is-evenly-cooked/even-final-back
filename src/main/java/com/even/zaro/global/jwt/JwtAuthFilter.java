package com.even.zaro.global.jwt;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private final Cache<String, Boolean> expiredTokenLogCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // 5분 후 삭제
            .maximumSize(1000) // 최대 1000개
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 추출
        String token = jwtUtil.resolveToken(request);

//        // Authorization 헤더 없을 경우 쿠키에서 추출 (SSE)
//        if (token == null) {
//            token = jwtUtil.extractTokenFromCookies(request);
//        }

        // 토큰 유효성 검사
        try {
            if (token != null && jwtUtil.validateAccessToken(token)) {

                // 로그아웃(블랙리스트) 체크
                if (redisTemplate.hasKey("BL:" + token)) {
                    log.warn("블랙리스트 토큰 접근 시도: {}", token);
                    throw new AuthenticationException("BLACKLISTED") {
                    };
                }

                Claims claims = jwtUtil.parseClaims(token);
                Long userId = Long.valueOf(claims.getSubject());

                // 인증 객체 생성
                JwtUserInfoDto userInfo = new JwtUserInfoDto(userId);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfo, null, null);

                // 시큐리티 컨텍스트에 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ExpiredJwtException e) {
            if (expiredTokenLogCache.getIfPresent(token) == null) {
                log.warn("만료된 JWT: {}", e.getMessage());
                expiredTokenLogCache.put(token, true);
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/refresh") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs");
    }
}
