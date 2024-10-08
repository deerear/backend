package com.deerear.app.controller;

import com.deerear.app.dto.DmChatDto;
import com.deerear.app.dto.DmRequestDto;
import com.deerear.app.dto.DmResponseDto;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.DmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DmController {

    private final DmService dmService;

    @PostMapping("/api/dms")
    public ResponseEntity<DmResponseDto> createOrGetDm(@AuthenticationPrincipal CustomUserDetails member, @RequestBody DmRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dmService.createDm(member, request));
    }

    @GetMapping("/api/dm-chats/{dmId}")
    public void getDmChats(@AuthenticationPrincipal CustomUserDetails member, @PathVariable UUID dmId, @RequestParam UUID nextKey, @RequestParam Long size) {
        dmService.listDmChats(member, dmId, nextKey, size);
    }

    @MessageMapping("/{dmId}")
    @SendTo("/{dmId}")
    public DmChatDto sendDm(@AuthenticationPrincipal CustomUserDetails customUserDetails, @DestinationVariable UUID dmId, String message){
        return dmService.sendDm(customUserDetails, dmId, message);
    }

}
