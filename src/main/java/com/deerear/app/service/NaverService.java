package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.*;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverService {

    @Value("${NAVER_CLIENT_ID}")
    private String NAVER_CLIENT_ID;

    @Value("${NAVER_CLIENT_SECRET}")
    private String NAVER_CLIENT_SECRET;

    @Value("${NAVER_REDIRECT_URI}")
    private String NAVER_REDIRECT_URL;


    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String NAVER_AUTH_URI = "https://nid.naver.com";
    private static final String NAVER_API_URI = "https://openapi.naver.com";

    public String getNaverLogin() {
        return NAVER_AUTH_URI + "/oauth2.0/authorize"
                + "?client_id=" + NAVER_CLIENT_ID
                + "&redirect_uri=" + NAVER_REDIRECT_URL
                + "&response_type=code";
    }

    public NaverDto getNaverInfo(String code) throws Exception {
        if (code == null) throw new Exception("Failed to get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", NAVER_CLIENT_ID);
            params.add("client_secret", NAVER_CLIENT_SECRET);
            params.add("code", code);
            params.add("redirect_uri", NAVER_REDIRECT_URL);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    NAVER_AUTH_URI + "/oauth2.0/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = (String) jsonObj.get("access_token");
            refreshToken = (String) jsonObj.get("refresh_token");
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        return getUserInfoWithToken(accessToken);
    }

    @Transactional
    public MemberSignUpResponseDto signup(NaverDto naverInfo) {
        // 신규 회원 객체 생성
        Member newMember = Member.builder()
                .email(naverInfo.getEmail())
                .nickname(naverInfo.getNickname())
                .build();

        // 회원 가입
        memberRepository.save(newMember);

        // 회원 가입 후 MemberSignUpResponseDto 생성
        return MemberSignUpResponseDto.builder()
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .build();
    }


    private NaverDto getUserInfoWithToken(String accessToken) throws Exception {
        // HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                NAVER_API_URI + "/v1/nid/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        // Response 데이터 파싱
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject account = (JSONObject) jsonObj.get("response");

        String id = String.valueOf(account.get("id"));
        String email = String.valueOf(account.get("email"));
        String nickname = String.valueOf(account.get("nickname"));

        return NaverDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();
    }

    @Transactional
    public MemberSignInResponseDto signIn(NaverDto naverInfo) {
        String email = naverInfo.getEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member member;

        if (optionalMember.isEmpty()) {
            // 신규 회원 가입 처리
            MemberSignUpResponseDto signUpResponseDto = signup(naverInfo);

            // 신규로 생성된 회원을 가져오기
            member = memberRepository.findByEmail(signUpResponseDto.getEmail())
                    .orElseThrow(() -> new BizException("멤버 정보를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));
        } else {
            // 기존 회원일 경우 회원 정보 사용
            member = optionalMember.get();
        }

        // OAuth 사용자의 경우 비밀번호 체크는 생략
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                null,
                member.getAuthorities()
        );

        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        MemberSignInResponseDto memberSignInResponseDto = jwtTokenProvider.generateToken(authentication);
        saveRefreshToken(member, memberSignInResponseDto);

        // 액세스 및 리프레시 토큰 반환
        return MemberSignInResponseDto.toDto(
                memberSignInResponseDto.getAccessToken(),
                memberSignInResponseDto.getRefreshToken()
        );
    }

    private void saveRefreshToken(Member member, MemberSignInResponseDto memberSignInResponseDto) {
        String refreshToken = memberSignInResponseDto.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member); // 데이터베이스에 리프레시 토큰 저장
    }

}