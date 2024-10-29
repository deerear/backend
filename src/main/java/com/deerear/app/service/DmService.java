package com.deerear.app.service;

import com.deerear.app.domain.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public PagingResponseDto getDms(Member member, PagingRequestDto pagingRequestDto) {
        String lastDmId = pagingRequestDto.getKey(); // 커서로 사용될 key
        int size = pagingRequestDto.getSize() != 0 ? pagingRequestDto.getSize() : 10; // 요청된 size로 페이징 설정, 기본 10으로 설정

        PageRequest pageRequest = PageRequest.of(0, size); // 페이징 처리

        List<DmMember> dmMembers;
        if (lastDmId == null) {
            // 첫 페이지 요청 시
            dmMembers = dmMemberRepository.findDmsByMember(member, pageRequest);
        } else {
            // 커서를 기준으로 이후의 DM을 가져옴
            dmMembers = dmMemberRepository.findDmsByMemberAndIdGreaterThan(member, UUID.fromString(lastDmId), pageRequest);
        }

        boolean hasNext = dmMembers.size() == size; // 다음 페이지 여부 확인

        List<Object> dmDtos = dmMembers.stream()
                .map(dmMember -> MemberGetDmsResponseDto.toDto(
                        dmMember.getDm().getId(), // DM ID
                        dmMember.getDm().getLastMessage(), // 마지막 메시지
                        dmMember.getCreatedAt() // Comment 엔티티의 생성 날짜
                ))
                .collect(Collectors.toList());

        String nextKey = hasNext ? dmMembers.get(dmMembers.size() - 1).getDm().getId().toString() : null;

        return new PagingResponseDto(dmDtos, size, nextKey, hasNext);
    }

    @Transactional(readOnly = true)
    public DmChatsResponseDto listDmChats(CustomUserDetails customUserDetails, UUID dmId, String nextKey, Integer size){

        dmMemberRepository.existsByMemberIdAndDmId(customUserDetails.getUser().getId(), dmId);

        Dm dm = dmRepository.getReferenceById(dmId);

        List<DmChat> dmChats;

        if (nextKey.isEmpty()){
            dmChats = dmChatRepository.findByDmPage(dm, Pageable.ofSize(size+1));
        } else {
            UUID key = UUID.fromString(nextKey);
            DmChat dmChat = dmChatRepository.getReferenceById(key);
            dmChats = dmChatRepository.findNextPage(dmChat.getCreatedAt(), UUID.fromString(nextKey), dm, Pageable.ofSize(size+1));
        }

        String resNextKey;
        boolean hasNext = false;
        List<DmChatDto> dmChatsDto = new ArrayList<>();

        if (dmChats.size() == 11){
            dmChats = dmChats.subList(0,10);
            hasNext = true;
        }

        for (DmChat dmChat : dmChats) {
            dmChatsDto.add(dmChat.toDto());
        }

        resNextKey = dmChats.get(dmChats.size()-1).getId().toString();


        return DmChatsResponseDto.builder()
                .objects(dmChatsDto)
                .hasNext(hasNext)
                .nextKey(resNextKey)
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
