package com.deerear.app.service;

import com.deerear.app.domain.Dm;
import com.deerear.app.domain.DmChat;
import com.deerear.app.domain.DmMember;
import com.deerear.app.domain.Member;
import com.deerear.app.dto.*;
import com.deerear.app.repository.DmChatRepository;
import com.deerear.app.repository.DmMemberRepository;
import com.deerear.app.repository.DmRepository;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public DmChatsResponseDto listDmChats(CustomUserDetails customUserDetails, UUID dmId, UUID nextKey, Integer size){

        dmMemberRepository.existsByMemberIdAndDmId(customUserDetails.getUser().getId(), dmId);

        Dm dm = dmRepository.getReferenceById(dmId);
        DmChat dmchat = dmChatRepository.findById(nextKey).orElseThrow(() ->new BizException("존재하지 않는 채팅입니다.", ErrorCode.NOT_FOUND, ""));

        // TODO 첫 페이지
        List<DmChat> dmChats = dmChatRepository.findNextPage(dmchat.getCreatedAt(), nextKey, dm, Pageable.ofSize(size+1));

        String tempNextKey;
        List<DmChatDto> dmChatsDto = new ArrayList<>();

        if (dmChats.size() == 11){
            for (int i=0;i<10;i++) {
                dmChatsDto.add(dmChats.get(i).toDto());
            }
            tempNextKey = dmChats.get(9).getId().toString();
        } else {
            for (DmChat dmChat : dmChats) {
                dmChatsDto.add(dmChat.toDto());
            }
            tempNextKey = dmChats.get(dmChats.size()-1).getId().toString();
        }

        return DmChatsResponseDto.builder()
                .objects(dmChatsDto)
                .hasNext(dmChats.size() > 10)
                .nextKey(tempNextKey)
                .size(size)
                .build();


    }

    @Transactional
    public DmChatDto sendDm(UUID dmId, DmDto request){

        Dm dm = dmRepository.getReferenceById(dmId);
        Member member = memberRepository.findByNickname(request.getNickname()).orElseThrow(() ->new BizException("존재하지 않는 유저입니다.", ErrorCode.NOT_FOUND, ""));
        DmChat chat = dmChatRepository.save(DmChat.builder().dm(dm).member(member).message(request.getMessage()).build());

        dm.setLastMessage(request.getMessage());

        return chat.toDto();
    }


    private DmMember buildDmMember(Dm dm, Member member, Member chatMember) {
        return DmMember.builder()
                .dm(dm)
                .member(member)
                .chatMember(chatMember)
                .build();
    }
}
