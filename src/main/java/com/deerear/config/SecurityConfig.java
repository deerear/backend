package com.deerear.config;

import com.deerear.jwt.JwtAuthenticationFilter;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // REST API이므로 CSRF 보안 비활성화
                .csrf(csrf -> csrf.disable())
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 해당 API에 대해서는 모든 요청을 허가
                        // permitAll 필요한 API 추가하길 바람.
                        // restdocs
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/api/members/sign-up").permitAll()	// ⭐
                        .requestMatchers("/api/members/sign-in").permitAll()
                        .requestMatchers("/api/members/login").permitAll()
                        // 리프레시 토큰 API는 인증된 사용자만 접근 가능
                        .requestMatchers("/api/token/refresh").permitAll()
                        // ADMIN 권한이 있어야 요청할 수 있음
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        // USER 권한이 있어야 요청할 수 있음
                        .requestMatchers("/api/members/test").hasRole("USER")
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().authenticated()
                )
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //
    }
}