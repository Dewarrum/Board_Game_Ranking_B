package com.board_game_back.Service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    // sessionId → memberId
    private final Map<String, Long> sessionToMember = new ConcurrentHashMap<>();
    // sessionId → roomIds (한 세션이 여러 방 구독 가능)
    private final Map<String, Set<String>> sessionToRooms = new ConcurrentHashMap<>();
    // roomId → online memberIds
    private final Map<String, Set<Long>> roomOnline = new ConcurrentHashMap<>();

    public void join(String sessionId, Long memberId, String roomId) {
        sessionToMember.put(sessionId, memberId);
        sessionToRooms.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(roomId);
        roomOnline.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(memberId);
    }

    public Set<Long> getOnline(String roomId) {
        return Collections.unmodifiableSet(
                roomOnline.getOrDefault(roomId, Collections.emptySet())
        );
    }

    /** 연결 해제 시 해당 세션이 참여했던 roomId 목록 반환 (브로드캐스트 대상) */
    public Set<String> disconnect(String sessionId) {
        Long memberId = sessionToMember.remove(sessionId);
        Set<String> rooms = sessionToRooms.remove(sessionId);
        if (memberId == null || rooms == null) return Collections.emptySet();

        Set<String> affected = new HashSet<>(rooms);
        for (String roomId : rooms) {
            Set<Long> online = roomOnline.get(roomId);
            if (online != null) {
                online.remove(memberId);
                if (online.isEmpty()) roomOnline.remove(roomId);
            }
        }
        return affected;
    }
}
