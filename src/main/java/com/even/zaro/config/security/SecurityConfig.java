package com.even.zaro.config.security;

import com.even.zaro.global.jwt.JwtAuthFilter;
import com.even.zaro.global.jwt.JwtAuthenticationEntryPoint;
import com.even.zaro.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${cors.backend-origin}")
    private String backendOrigin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // JWT 사용으로 필요 없음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/health/**").permitAll() // health  
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // 스웨거
                        .requestMatchers("/api/auth/**").permitAll() // auth 인증 없이
                                .requestMatchers("/api/posts/home").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/search/es").permitAll() // search 로 합쳐질 예정
                                .requestMatchers( "/api/posts/rank").permitAll()
                                .requestMatchers("/api/profile/{userId}").permitAll()
                                .requestMatchers("/api/es/reindex").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/favorite/{groupId}/items").permitAll() // 그룹, 즐겨찾기 조회는 인증 필요 x
                                .requestMatchers(HttpMethod.GET, "/api/map/place").permitAll() // 그룹, 즐겨찾기 조회는 인증 필요 x
                                .requestMatchers(HttpMethod.GET, "/api/group/user/{userId}/group").permitAll()
//                        .requestMatchers("/**").permitAll() // 전체 인증 없이 개발용
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://zaro.vercel.app");
        configuration.addAllowedOrigin(backendOrigin);
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
