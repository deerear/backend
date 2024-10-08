package com.deerear.app.service;

import com.deerear.app.domain.Dm;
import com.deerear.app.domain.DmChat;
import com.deerear.app.domain.DmMember;
import com.deerear.app.domain.Member;
import com.deerear.app.dto.DmChatDto;
import com.deerear.app.dto.DmChatsResponseDto;
import com.deerear.app.dto.DmRequestDto;
import com.deerear.app.dto.DmResponseDto;
import com.deerear.app.repository.DmChatRepository;
import com.deerear.app.repository.DmMemberRepository;
import com.deerear.app.repository.DmRepository;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DmRepository dmRepository;
    private final DmMemberRepository dmMemberRepository;
    private final DmChatRepository dmChatRepository;
    private final MemberRepository memberRepository;

    public DmResponseDto createDm(CustomUserDetails customUserDetails, DmRequestDto request){
        Dm dm = dmRepository.save(request.toEntity());

        Member requestMember = customUserDetails.getUser();
        Member dmMember = memberRepository.findByNickname(request.getDmMemberNickname()).orElseThrow(() ->new BizException("존재하지 않는 유저입니다.", ErrorCode.NOT_FOUND, ""));

        dmMemberRepository.save(buildDmMember(dm, requestMember));
        dmMemberRepository.save(buildDmMember(dm, dmMember));

        return DmResponseDto.builder()
                .dmId(dm.getId())
                .build();
    }

    public void listDmChats(CustomUserDetails customUserDetails, UUID dmId){

        dmMemberRepository.existsByMemberIdAndDmId(customUserDetails.getUser().getId(), dmId);

        List<DmChat> dmChats = dmChatRepository.findAllByDmId(dmId);


    }

    @Transactional
    public DmChatDto sendDm(CustomUserDetails customUserDetails, UUID dmId, String message){
        Dm dm = dmRepository.getReferenceById(dmId);

        DmChat chat = buildDmChat(dm, customUserDetails.getUser(), message);

        return chat.toDto();
    }

    private DmChat buildDmChat(Dm dm, Member member, String message){
        return DmChat.builder()
                .dm(dm)
                .member(member)
                .message(message)
                .build();
    }

    private DmMember buildDmMember(Dm dm, Member member) {
        return DmMember.builder()
                .dm(dm)
                .member(member)
                .build();
    }
}
