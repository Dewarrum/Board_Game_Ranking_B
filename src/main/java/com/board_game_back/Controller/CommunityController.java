package com.board_game_back.Controller;

import com.board_game_back.DTO.CommunityDto;
import com.board_game_back.Service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<CommunityDto.Response> createCommunity(@RequestBody CommunityDto.CreateRequest request) {
        return ResponseEntity.ok(communityService.createCommunity(request));
    }

    @GetMapping("/my/{memberId}")
    public ResponseEntity<CommunityDto.Response> getMyCommunity(@PathVariable Long memberId) {
        CommunityDto.Response response = communityService.getMyCommunity(memberId);
        if (response == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityDto.DetailResponse> getCommunityDetail(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityService.getCommunityDetail(communityId));
    }

    @GetMapping("/{communityId}/rooms")
    public ResponseEntity<List<CommunityDto.RoomResponse>> getCommunityRooms(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityService.getCommunityRooms(communityId));
    }

    @PatchMapping("/{communityId}")
    public ResponseEntity<CommunityDto.Response> updateCommunity(
            @PathVariable Long communityId,
            @RequestBody CommunityDto.UpdateRequest request) {
        return ResponseEntity.ok(communityService.updateCommunity(communityId, request));
    }

    @PostMapping("/{communityId}/rooms/{roomId}")
    public ResponseEntity<String> addRoomToCommunity(
            @PathVariable Long communityId,
            @PathVariable Long roomId) {
        communityService.addRoomToCommunity(communityId, roomId);
        return ResponseEntity.ok("그룹이 커뮤니티에 연결되었습니다.");
    }
}
