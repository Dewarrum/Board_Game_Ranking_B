package com.board_game_back.Controller;

import com.board_game_back.Service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    /** 클라이언트가 방에 입장할 때 호출: /app/presence/join */
    @MessageMapping("/presence/join")
    public void join(SimpMessageHeaderAccessor accessor,
                     @Payload Map<String, Object> payload) {
        String sessionId = accessor.getSessionId();
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        String roomId = payload.get("roomId").toString();

        presenceService.join(sessionId, memberId, roomId);
        broadcastPresence(roomId);
    }

    public void broadcastPresence(String roomId) {
        Set<Long> online = presenceService.getOnline(roomId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/presence", online);
    }
}
