package com.board_game_back.Config;

import com.board_game_back.Controller.PresenceController;
import com.board_game_back.Service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final PresenceService presenceService;
    private final PresenceController presenceController;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Set<String> affectedRooms = presenceService.disconnect(sessionId);
        affectedRooms.forEach(presenceController::broadcastPresence);
    }
}
