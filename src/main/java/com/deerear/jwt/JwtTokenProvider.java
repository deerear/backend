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

    // 리프레시 토큰에서 사용자 이름 추출하는 메서드 추가
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();  // 토큰의 주제 (subject)로 사용자 이름을 사용한다고 가정
    }

    // AccessToken과 RefreshToken을 생성하는 메서드

    public MemberSignInResponseDto generateToken(Authentication authentication) {
        long now = System.currentTimeMillis();

        // AccessToken 생성
        String accessToken = generateAccessToken(authentication, now);

        // Authentication 객체에서 사용자 이름 추출
        String username = authentication.getName();

        // RefreshToken 생성 - 사용자 이름을 기반으로 생성
        String refreshToken = generateRefreshToken(username, now);

      //  log.debug("Generated AccessToken: {}", accessToken);
       // log.debug("Generated RefreshToken: {}", refreshToken);

        return MemberSignInResponseDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(Authentication authentication, long now) {
        // 권한 정보 추출
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 사용자 이메일 (혹은 다른 식별자를 Subject로 설정)
        String subject = authentication.getName();

        // 만료 시간 설정 (30분)
        Date accessTokenExpiresIn = new Date(now + 1800000); // 30분 후 만료

        // 토큰 생성
        return Jwts.builder()
                .setSubject(subject)  // sub 클레임
                .claim("auth", authorities)  // 권한 정보를 포함한 auth 클레임 추가
                .setIssuedAt(new Date(now))  // iat 클레임 (발급 시간)
                .setExpiration(accessTokenExpiresIn)  // exp 클레임 (만료 시간)
                .setIssuer("deerear")  // iss 클레임
                .signWith(key, SignatureAlgorithm.HS256)  // 서명 알고리즘
                .compact();
    }

    // RefreshToken 생성 메서드
    public String generateRefreshToken(String username, long now) {
        // Refresh Token 생성 - 2주 (14일 * 24시간 * 60분 * 60초 * 1000밀리초)
        Date refreshTokenExpiresIn = new Date(now + 1209600000); // 2주 = 1209600000밀리초

        // 사용자 이름을 주제로 설정
        return Jwts.builder()
                .setSubject(username)  // 사용자 이름을 subject로 설정
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // 액세스 토큰의 경우 권한 정보가 필요함
        if (claims.get("auth") == null) {
            // 권한 정보가 없는 경우 리프레시 토큰인지 확인
            if (isRefreshToken(token)) {
                // 리프레시 토큰일 경우 권한 정보 체크하지 않음
                return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
            } else {
                throw new BizException("권한 정보가 없는 토큰입니다.", ErrorCode.INVALID_INPUT, "토큰: " + token);
            }
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .filter(role -> role != null && !role.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        UserDetails principal = new User(claims.getSubject(), "password_placeholder", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    // 리프레시 토큰인지 확인하는 메서드
    private boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("auth") == null; // 권한 정보가 없으면 리프레시 토큰으로 간주
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            log.error("Invalid Refresh Token", e);
            return false;
        }
    }


    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}