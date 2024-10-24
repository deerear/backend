package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.domain.PostImage;
import com.deerear.app.dto.*;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static com.deerear.app.util.StaticFiles.deleteImage;
import static com.deerear.app.util.StaticFiles.saveImage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public MemberSignInResponseDto signIn(MemberSignInRequestDto memberSignInRequestDto) {
        validateSignIn(memberSignInRequestDto.getEmail(), memberSignInRequestDto.getPassword());

        Member member = memberRepository.findByEmail(memberSignInRequestDto.getEmail())
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + memberSignInRequestDto.getEmail()));

        boolean matches = passwordEncoder.matches(memberSignInRequestDto.getPassword(), member.getPassword());
        if (!matches) {
            throw new BizException("비밀번호가 잘못되었습니다.", ErrorCode.INVALID_PASSWORD, "password: " + "");
        }

        Authentication authentication = authenticateUser(memberSignInRequestDto.getEmail(), memberSignInRequestDto.getPassword());
        MemberSignInResponseDto memberSignInResponseDto = jwtTokenProvider.generateToken(authentication);

        saveRefreshToken(member, memberSignInResponseDto);

        return MemberSignInResponseDto.toDto(memberSignInResponseDto.getAccessToken(), memberSignInResponseDto.getRefreshToken());
    }

    private void validateSignIn(String email, String password) {
        if (email == null || password == null) {
            throw new BizException("이메일이나 비밀번호를 입력해주세요.", ErrorCode.INVALID_INPUT, "email: " + email);
        }
    }

    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    private void saveRefreshToken(Member member, MemberSignInResponseDto memberSignInResponseDto) {
        String refreshToken = memberSignInResponseDto.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member); // 데이터베이스에 리프레시 토큰 저장
    }

    @Transactional
    public MemberSignUpResponseDto signUp(MemberSingUpRequestDto memberSingUpRequestDto) {
        validateSignUp(memberSingUpRequestDto);

        String encodedPassword = passwordEncoder.encode(memberSingUpRequestDto.getPassword());

        Member savedMember = memberRepository.save(memberSingUpRequestDto.toEntity(encodedPassword));

        return MemberSignUpResponseDto.toDto(savedMember);
    }

    private void validateSignUp(MemberSingUpRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new BizException("이미 사용 중인 사용자 이메일입니다.", ErrorCode.USERNAME_ALREADY_EXISTS, "email: " + requestDto.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public MemberCheckNicknameResponseDto checkNickname(MemberCheckNicknameRequestDto memberCheckNicknameRequestDto) {
        validateCheckNickname(memberCheckNicknameRequestDto);

        return MemberCheckNicknameResponseDto.toDto(true);  // 사용 가능한 닉네임이면 true 반환
    }

    private void validateCheckNickname(MemberCheckNicknameRequestDto memberCheckNicknameRequestDto) {
        //TODO 에러코드 추후 협의해봐야할듯
        if (memberRepository.existsByNickname(memberCheckNicknameRequestDto.getNickname())) {
            throw new BizException("이미 사용 중인 닉네임입니다.", ErrorCode.INVALID_INPUT, "닉네임: " + memberCheckNicknameRequestDto.getNickname());
        }
    }

    @Transactional(readOnly = true)
    public MemberCheckEmailResponseDto checkEmail(MemberCheckEmailRequestDto requestDto) {
        validateCheckEmail(requestDto);

        return MemberCheckEmailResponseDto.toDto(true);  // 사용 가능한 닉네임이면 true 반환
    }

    @Transactional
    public void updateProfile(CustomUserDetails customUserDetails, MemberUpdateRequestDto memberUpdateRequestDto) {
        Member member = customUserDetails.getUser();

        // 멤버 정보를 업데이트하기 전에 유효성 검사
        validateProfileUpdate(memberUpdateRequestDto);

        // 프로필 이미지가 포함되어 있으면 저장
        if (memberUpdateRequestDto.getProfileImg() != null) {
            String path = saveImage(memberUpdateRequestDto.getProfileImg(), "members", member.getId().toString(), false);
            member.setProfileImgUrl(path);
        } else if (memberUpdateRequestDto.getProfileImg() == null) {
            // 프로필 이미지가 없고 기존 이미지가 있다면 삭제
            deleteImage(member.getProfileImgUrl());
            member.setProfileImgUrl(null); // 프로필 이미지 URL을 null로 설정
        }

        // 다른 회원 정보 업데이트
        member.setNickname(memberUpdateRequestDto.getNickname());

        memberRepository.save(member); // 변경된 멤버 정보 저장
    }

    private void validateProfileUpdate(MemberUpdateRequestDto memberUpdateRequestDto) {
        // 닉네임 유효성 검사
        if (memberUpdateRequestDto.getNickname() == null || memberUpdateRequestDto.getNickname().isEmpty()) {
            throw new BizException("닉네임을 입력해주세요.", ErrorCode.INVALID_INPUT, "nickname: " + memberUpdateRequestDto.getNickname());
        }

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(memberUpdateRequestDto.getNickname())) {
            throw new BizException("이미 사용 중인 닉네임입니다.", ErrorCode.USERNAME_ALREADY_EXISTS, "nickname: " + memberUpdateRequestDto.getNickname());
        }

        // 이미지 유효성 검사 (프로필 이미지가 있을 때)
        MultipartFile profileImage = memberUpdateRequestDto.getProfileImg();
        if (profileImage != null && !isValidImageFormat(profileImage)) {
            throw new BizException("유효하지 않은 이미지 형식입니다.", ErrorCode.INVALID_INPUT, "profileImage");
        }
    }

    // 이미지 형식 유효성 검사
    private boolean isValidImageFormat(MultipartFile image) {
        String contentType = image.getContentType();
        //TODO 이미지 형식 더 추가해야할듯
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }

    private void validateCheckEmail(MemberCheckEmailRequestDto memberCheckEmailRequestDto) {
        if (memberRepository.existsByEmail(memberCheckEmailRequestDto.getEmail())) {
            throw new BizException("이미 사용 중인 사용자 이메일입니다.", ErrorCode.USERNAME_ALREADY_EXISTS, "email: " + memberCheckEmailRequestDto.getEmail());
        }
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        validateChangePassword(email, newPassword);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));

        String encodedPassword = passwordEncoder.encode(newPassword);

        member.setPassword(encodedPassword);

        memberRepository.save(member);
    }

    private void validateChangePassword(String email, String newPassword) {
        if (email == null || newPassword == null) {
            throw new BizException("사용자 이름 또는 새 비밀번호가 누락되었습니다.", ErrorCode.INVALID_INPUT, "email: " + email);
        }
    }

    @Transactional
    public MemberGetProfileResponseDto getProfile(Member requestMember) {
        String email = requestMember.getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));
        return MemberGetProfileResponseDto.toDto(member.getNickname(),member.getEmail(),member.getProfileImgUrl());
    }
}