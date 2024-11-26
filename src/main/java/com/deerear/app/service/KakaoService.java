package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.KakaoDto;
import com.deerear.app.dto.MemberSignInResponseDto;
import com.deerear.app.dto.MemberSignUpResponseDto;
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
public class KakaoService {

    @Value("${KAKAO_CLIENT_ID}")
    private String KAKAO_CLIENT_ID;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${KAKAO_REDIRECT_URI}")
    private String KAKAO_REDIRECT_URL;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private static final String KAKAO_API_URI = "https://kapi.kakao.com";

    public String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL
                + "&response_type=code";
    }


    public KakaoDto getKakaoInfo(String code) throws Exception {
        if (code == null) throw new Exception("Failed to get authorization code");

        String accessToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("client_secret", KAKAO_CLIENT_SECRET);
            params.add("redirect_uri", KAKAO_REDIRECT_URL);
            params.add("code", code);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = (String) jsonObj.get("access_token");
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        return getUserInfoWithToken(accessToken);
    }

    @Transactional
    public MemberSignUpResponseDto signup(KakaoDto kakaoInfo) {
        Member newMember = Member.builder()
                .email(kakaoInfo.getEmail())
                .nickname(kakaoInfo.getNickname())
                .build();

        memberRepository.save(newMember);

        return MemberSignUpResponseDto.builder()
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .build();
    }

    private KakaoDto getUserInfoWithToken(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject kakaoAccount = (JSONObject) jsonObj.get("kakao_account");

        String id = String.valueOf(jsonObj.get("id"));
        String email = kakaoAccount.containsKey("email") ? (String) kakaoAccount.get("email") : null;
        String nickname = kakaoAccount.containsKey("profile") ? (String) ((JSONObject) kakaoAccount.get("profile")).get("nickname") : null;

        return KakaoDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();
    }

    @Transactional
    public MemberSignInResponseDto signIn(KakaoDto kakaoInfo) {
        String email = kakaoInfo.getEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member member;

        if (optionalMember.isEmpty()) {
            MemberSignUpResponseDto signUpResponseDto = signup(kakaoInfo);
            member = memberRepository.findByEmail(signUpResponseDto.getEmail())
                    .orElseThrow(() -> new BizException("멤버 정보를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));
        } else {
            member = optionalMember.get();
            if (member.getPassword() != null && !member.getPassword().isEmpty()) {
                throw new BizException("해당 이메일은 일반 로그인으로 가입되었습니다. 일반 로그인을 시도해 주세요.", ErrorCode.USERNAME_ALREADY_EXISTS, "email :" + email);
            }
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(),
                null,
                member.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MemberSignInResponseDto memberSignInResponseDto = jwtTokenProvider.generateToken(authentication);
        saveRefreshToken(member, memberSignInResponseDto);

        return MemberSignInResponseDto.toDto(
                memberSignInResponseDto.getGrantType(),
                memberSignInResponseDto.getAccessToken(),
                memberSignInResponseDto.getRefreshToken()
        );
    }

    private void saveRefreshToken(Member member, MemberSignInResponseDto memberSignInResponseDto) {
        String refreshToken = memberSignInResponseDto.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
    }
}