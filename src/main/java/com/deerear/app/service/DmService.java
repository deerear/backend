package com.deerear.app.service;

import com.deerear.app.domain.Dm;
import com.deerear.app.domain.DmChat;
import com.deerear.app.domain.DmMember;
import com.deerear.app.domain.Member;
import com.deerear.app.dto.DmChatDto;
import com.deerear.app.dto.DmRequestDto;
import com.deerear.app.dto.DmResponseDto;
import com.deerear.app.repository.DmChatRepository;
import com.deerear.app.repository.DmMemberRepository;
import com.deerear.app.repository.DmRepository;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmService {

    private final JwtTokenProvider jwtTokenProvider;
    private final DmRepository dmRepository;
    private final DmMemberRepository dmMemberRepository;
    private final DmChatRepository dmChatRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public DmResponseDto createDm(CustomUserDetails customUserDetails, DmRequestDto request){

        Member member = customUserDetails.getUser();
        Member chatMember = memberRepository.findByNickname(request.getDmMemberNickname()).orElseThrow(() ->new BizException("존재하지 않는 유저입니다.", ErrorCode.NOT_FOUND, ""));

        // 상대방과 기존 DM 여부 검증
        Optional<DmMember> ExistedDm = dmMemberRepository.findByMemberIdAndChatMemberId(member.getId(), chatMember.getId());
        if (ExistedDm.isPresent()){
            return DmResponseDto.builder()
                    .dmId(ExistedDm.get().getDm().getId())
                    .build();
        }

        Dm dm = dmRepository.save(request.toEntity());
        dmMemberRepository.save(buildDmMember(dm, member, chatMember));
        dmMemberRepository.save(buildDmMember(dm, chatMember, member));

        return dm.toDto();
    }

    @Transactional(readOnly = true)
    public void listDmChats(CustomUserDetails customUserDetails, UUID dmId, UUID nextKey, Long size){

        dmMemberRepository.existsByMemberIdAndDmId(customUserDetails.getUser().getId(), dmId);

        List<DmChat> dmChats = dmChatRepository.findAllByDmId(dmId);


    }

    @Transactional
    public DmChatDto sendDm(CustomUserDetails customUserDetails, UUID dmId, String message){

//        String email = jwtTokenProvider.getUsernameFromToken(auth.substring(7));

        Dm dm = dmRepository.getReferenceById(dmId);
//        Member member = memberRepository.findByEmail(email).orElseThrow(() ->new BizException("존재하지 않는 유저입니다.", ErrorCode.NOT_FOUND, ""));
        Member member = customUserDetails.getUser();

        DmChat chat = buildDmChat(dm, member, message);
        dmChatRepository.save(chat);

        dm.setLastMessage(message);
        return chat.toDto();
    }

    private DmChat buildDmChat(Dm dm, Member member, String message){
        return DmChat.builder()
                .dm(dm)
                .member(member)
                .message(message)
                .build();
    }

    private DmMember buildDmMember(Dm dm, Member member, Member chatMember) {
        return DmMember.builder()
                .dm(dm)
                .member(member)
                .chatMember(chatMember)
                .build();
    }
}
