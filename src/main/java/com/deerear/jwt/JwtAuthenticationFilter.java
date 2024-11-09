package com.deerear.jwt;

import com.deerear.app.service.CustomUserDetailsService;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response; // HttpServletResponse 추가
        String path = httpRequest.getRequestURI();

        // 인증이 필요 없는 API 목록
        if ("/api/members/sign-up".equals(path)
                || "/api/members/sign-in".equals(path)
                || "/api/members/check-nickname".equals(path)
                || "/api/members/check-email".equals(path)
                || path.startsWith("/api/oauth")
                || "/api/token/refresh".equals(path)
                || path.startsWith("/docs")
                || path.startsWith("/restdoc")
                || path.startsWith("/images")) {
            chain.doFilter(request, response);
            return;
        }

        // Request Header에서 JWT 토큰 추출
        String token = resolveToken(httpRequest);

        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getUsernameFromToken(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());

                    // 토큰이 유효할 경우 Authentication 객체를 SecurityContext에 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 토큰이 유효하지 않을 경우 응답 처리
                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    httpResponse.getWriter().write("유효하지 않은 토큰입니다." );
                    return;
                }
            } catch (ExpiredJwtException e) {
                // 토큰이 만료된 경우 응답 처리
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpResponse.getWriter().write("토큰이 만료되었습니다." );
                return;
            }
        } else {
            // 토큰이 null인 경우 응답 처리
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.getWriter().write("토큰이 null입니다.");
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}