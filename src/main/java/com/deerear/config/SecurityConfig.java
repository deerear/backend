package com.deerear.config;

import com.deerear.app.service.CustomUserDetailsService;
import com.deerear.jwt.JwtAuthenticationFilter;
import com.deerear.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfig corsConfig) throws Exception {
        return http
                // REST API이므로 CSRF 보안 비활성화
                .csrf(csrf -> csrf.disable())
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 예외 처리 추가
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"인증되지 않은 접근입니다.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"접근 권한이 없습니다.\"}");
                        }))
                .authorizeHttpRequests(authz -> authz
                        // 해당 API에 대해서는 모든 요청을 허가
                        // permitAll 필요한 API 추가하길 바람.
                        // restdocs
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/restdoc/**").permitAll()
                        .requestMatchers("/api/members/sign-up").permitAll()	// ⭐
                        .requestMatchers("/api/members/sign-in").permitAll()
                        .requestMatchers("/api/oauth/**").permitAll()
                        .requestMatchers("/api/members/check-nickname").permitAll()
                        .requestMatchers("/api/members/check-email").permitAll()
                        .requestMatchers("/api/members/login/**").permitAll()
                        // 리프레시 토큰 API는 인증된 사용자만 접근 가능
                        .requestMatchers("/api/token/refresh").permitAll()
                        // ADMIN 권한이 있어야 요청할 수 있음
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilter(corsConfig.corsFilter())
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // TODO 연구 필요 @2024-10-07 unknwoon
        // WARNING: If you are configuring WebSecurity to ignore requests, consider using permitAll via HttpSecurity#authorizeHttpRequests instead. See the configure Javadoc for additional details.
        // https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //
    }
}