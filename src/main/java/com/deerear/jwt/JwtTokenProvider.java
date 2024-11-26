package com.deerear.jwt;

import com.deerear.app.dto.MemberSignInResponseDto;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public MemberSignInResponseDto generateToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        String accessToken = generateAccessToken(authentication, now);
        String username = authentication.getName();
        String refreshToken = generateRefreshToken(username, now);

        return MemberSignInResponseDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(Authentication authentication, long now) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date accessTokenExpiresIn = new Date(now + 1800000); // 30분

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .setIssuer("deerear")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username, long now) {
        Date refreshTokenExpiresIn = new Date(now + 1209600000); // 2주

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")  // 토큰 타입 추가
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get("type", String.class);

        // 액세스 토큰 검증
        if (!"access".equals(tokenType)) {
            throw new BizException("유효하지 않은 토큰 타입입니다.",
                    ErrorCode.INVALID_INPUT,
                    "Token type: " + tokenType);
        }

        if (claims.get("auth") == null) {
            throw new BizException("권한 정보가 없는 토큰입니다.",
                    ErrorCode.INVALID_INPUT,
                    "Token: " + token);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .filter(role -> role != null && !role.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                throw new BizException("유효하지 않은 토큰 타입입니다.",
                        ErrorCode.INVALID_INPUT,
                        "Token type: " + tokenType);
            }

            if (claims.getExpiration().before(new Date())) {
                throw new BizException("만료된 토큰입니다.",
                        ErrorCode.INVALID_INPUT,
                        "Token: " + token);
            }

            return true;
        } catch (ExpiredJwtException e) {
            throw new BizException("만료된 토큰입니다.",
                    ErrorCode.INVALID_INPUT,
                    "Token: " + token);
        } catch (Exception e) {
            throw new BizException("유효하지 않은 토큰입니다.",
                    ErrorCode.INVALID_INPUT,
                    "Token: " + token + ", Error: " + e.getMessage());
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 리프레시 토큰 타입 검증
            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                log.info("Invalid refresh token type: {}", tokenType);
                return false;
            }

            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Refresh token validation failed", e);
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}