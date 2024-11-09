package com.deerear.app.controller;

import com.deerear.app.dto.*;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.DmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
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
    public ResponseEntity<DmChatsResponseDto> listDmChats(@AuthenticationPrincipal CustomUserDetails member, @PathVariable UUID dmId, @RequestParam String key, @RequestParam Integer size) {
        return ResponseEntity.ok(dmService.listDmChats(member, dmId, key, size));
    }

    @MessageMapping("/{dmId}")
    @SendTo("/sub/{dmId}")
    public DmChatDto sendDm(@DestinationVariable UUID dmId, DmDto request){
        DmChatDto output = dmService.sendDm(dmId, request);
        System.out.println(output);
        return output;
    }

}
