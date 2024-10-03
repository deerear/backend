package com.deerear.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendDm(CustomUserDetails member, UUID dmId, String text){

//        simpMessagingTemplate.convertAndSend("/sub/" + dmId, dm);
        return;
    }
}
