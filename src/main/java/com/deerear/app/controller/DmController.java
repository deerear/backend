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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DmController {

    private final DmService dmService;

    @PostMapping("/api/dms")
    public ResponseEntity<DmResponseDto> createDm(@AuthenticationPrincipal CustomUserDetails member, @RequestBody DmRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dmService.createDm(member, request));
    }

    @GetMapping("/api/dm-chats/{dmId}")
    public void getDmChats(@AuthenticationPrincipal CustomUserDetails member, @PathVariable UUID dmId) {
        dmService.listDmChats(member, dmId);
    }


    @MessageMapping("/pub/{dmId}")
    @SendTo("/sub/{dmId}")
    public DmChatDto sendDm(@AuthenticationPrincipal CustomUserDetails member, @DestinationVariable UUID dmId, String message){
        return dmService.sendDm(member, dmId, message);
    }

}
