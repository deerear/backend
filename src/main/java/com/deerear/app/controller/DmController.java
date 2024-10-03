package com.deerear.app.controller;

import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.DmService;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DmController {

    private final DmService dmService;

    @PostMapping("/api/dm")
    public void createDm(@AuthenticationPrincipal CustomUserDetails userDetails, String otherUserNickname) {

    }

    @MessageMapping("/sub/{dmId}")
    public void sendDm(@AuthenticationPrincipal CustomUserDetails member, UUID dmId, String text){
        dmService.sendDm(member, dmId, text);
    }

}
