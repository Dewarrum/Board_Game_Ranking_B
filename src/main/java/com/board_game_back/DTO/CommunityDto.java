package com.board_game_back.DTO;

import java.util.List;

public class CommunityDto {

    public record CreateRequest(
        String name,
        String region,
        String imageUrl,
        Long createdBy,
        List<Long> adminMemberIds
    ) {}

    public record Response(
        Long communityId,
        String name,
        String region,
        String imageUrl,
        String status
    ) {}

    public record AdminInfo(
        Long memberId,
        String nickname
    ) {}

    public record DetailResponse(
        Long communityId,
        String name,
        String region,
        String imageUrl,
        String status,
        int groupCount,
        long memberCount,
        List<AdminInfo> admins
    ) {}

    public record RoomResponse(
        Long roomId,
        String roomName,
        String inviteCode,
        Long boardGameId,
        String imageUrl,
        boolean sessionActive
    ) {}
}
