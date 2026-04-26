package com.board_game_back.Service;

import com.board_game_back.DTO.CommunityDto;
import com.board_game_back.Entity.Community;
import com.board_game_back.Entity.CommunityAdmin;
import com.board_game_back.Entity.CommunityMember;
import com.board_game_back.Entity.Member;
import com.board_game_back.Entity.Room;
import com.board_game_back.Repository.BoardGameRepository;
import com.board_game_back.Repository.CommunityAdminRepository;
import com.board_game_back.Repository.CommunityMemberRepository;
import com.board_game_back.Repository.CommunityRepository;
import com.board_game_back.Repository.MemberRepository;
import com.board_game_back.Repository.RoomMemberRepository;
import com.board_game_back.Repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityAdminRepository communityAdminRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final BoardGameRepository boardGameRepository;

    @Transactional
    public CommunityDto.Response createCommunity(CommunityDto.CreateRequest req) {
        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        Community community = new Community(req.name(), req.region(), req.imageUrl(), req.createdBy());
        community.setInviteCode(inviteCode);
        communityRepository.save(community);

        Member creator = memberRepository.findById(req.createdBy())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        communityAdminRepository.save(new CommunityAdmin(community, creator));
        communityMemberRepository.save(new CommunityMember(community, creator));

        if (req.adminMemberIds() != null) {
            for (Long memberId : req.adminMemberIds()) {
                if (memberId.equals(req.createdBy())) continue;
                if (communityAdminRepository.countByCommunityId(community.getId()) >= 5) break;
                memberRepository.findById(memberId).ifPresent(member -> {
                    communityAdminRepository.save(new CommunityAdmin(community, member));
                    if (!communityMemberRepository.existsByCommunityIdAndMemberId(community.getId(), member.getId())) {
                        communityMemberRepository.save(new CommunityMember(community, member));
                    }
                });
            }
        }

        return toResponse(community);
    }

    @Transactional
    public CommunityDto.Response joinCommunity(String inviteCode, Long memberId) {
        Community community = communityRepository.findByInviteCode(inviteCode.toUpperCase().trim())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (!communityMemberRepository.existsByCommunityIdAndMemberId(community.getId(), memberId)) {
            communityMemberRepository.save(new CommunityMember(community, member));
        }

        return toResponse(community);
    }

    @Transactional(readOnly = true)
    public CommunityDto.Response getMyCommunity(Long memberId) {
        return communityRepository.findByCreatedBy(memberId)
            .map(this::toResponse)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public CommunityDto.DetailResponse getCommunityDetail(Long communityId) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커뮤니티입니다."));

        List<Room> rooms = roomRepository.findByCommunityId(communityId);
        int groupCount = rooms.size();
        long memberCount = communityMemberRepository.countByCommunityId(communityId);

        List<CommunityDto.AdminInfo> admins = communityAdminRepository.findByCommunityId(communityId)
            .stream()
            .map(ca -> new CommunityDto.AdminInfo(ca.getMember().getId(), ca.getMember().getNickname()))
            .collect(Collectors.toList());

        return new CommunityDto.DetailResponse(
            community.getId(), community.getName(), community.getRegion(),
            community.getImageUrl(), community.getStatus(),
            groupCount, memberCount, admins, community.getInviteCode()
        );
    }

    @Transactional(readOnly = true)
    public List<CommunityDto.RoomResponse> getCommunityRooms(Long communityId, Long memberId) {
        return roomRepository.findByCommunityId(communityId).stream()
            .map(r -> {
                String imageUrl = r.getBoardGameId() != null
                    ? boardGameRepository.findById(r.getBoardGameId())
                        .map(g -> g.getImageUrl()).orElse(null)
                    : null;
                boolean isMember = memberId != null &&
                    roomMemberRepository.findByRoomIdAndMemberId(r.getId(), memberId).isPresent();
                return new CommunityDto.RoomResponse(
                    r.getId(), r.getName(), r.getInviteCode(), r.getBoardGameId(), imageUrl,
                    r.isSessionActive(), isMember);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public CommunityDto.Response updateCommunity(Long communityId, CommunityDto.UpdateRequest req) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커뮤니티입니다."));

        if (req.name() != null && !req.name().isBlank()) community.setName(req.name().trim());
        if (req.region() != null) community.setRegion(req.region());
        if (req.imageUrl() != null) community.setImageUrl(req.imageUrl());

        communityAdminRepository.deleteByCommunityId(communityId);

        Member creator = memberRepository.findById(community.getCreatedBy())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        communityAdminRepository.save(new CommunityAdmin(community, creator));

        if (req.adminMemberIds() != null) {
            for (Long memberId : req.adminMemberIds()) {
                if (memberId.equals(community.getCreatedBy())) continue;
                if (communityAdminRepository.countByCommunityId(communityId) >= 5) break;
                memberRepository.findById(memberId).ifPresent(member -> {
                    communityAdminRepository.save(new CommunityAdmin(community, member));
                    if (!communityMemberRepository.existsByCommunityIdAndMemberId(communityId, member.getId())) {
                        communityMemberRepository.save(new CommunityMember(community, member));
                    }
                });
            }
        }

        communityRepository.save(community);
        return toResponse(community);
    }

    @Transactional
    public void addRoomToCommunity(Long communityId, Long roomId) {
        communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커뮤니티입니다."));
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        room.setCommunityId(communityId);
        roomRepository.save(room);
    }

    @Transactional
    public void deleteCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커뮤니티입니다."));
        communityAdminRepository.deleteByCommunityId(communityId);
        communityMemberRepository.deleteByCommunityId(communityId);
        communityRepository.delete(community);
    }

    @Transactional(readOnly = true)
    public List<CommunityDto.Response> getJoinedCommunities(Long memberId) {
        return communityMemberRepository.findCommunityIdsByMemberId(memberId).stream()
            .distinct()
            .flatMap(id -> communityRepository.findById(id).stream())
            .filter(c -> !c.getCreatedBy().equals(memberId))
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityDto.Response> getMyCommunitiesList(Long memberId) {
        return communityRepository.findAllByCreatedBy(memberId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private CommunityDto.Response toResponse(Community c) {
        long memberCount = communityMemberRepository.countByCommunityId(c.getId());
        int groupCount = (int) roomRepository.countByCommunityId(c.getId());
        List<CommunityDto.AdminInfo> admins = communityAdminRepository.findByCommunityId(c.getId())
            .stream()
            .map(ca -> new CommunityDto.AdminInfo(ca.getMember().getId(), ca.getMember().getNickname()))
            .collect(Collectors.toList());
        return new CommunityDto.Response(
            c.getId(), c.getName(), c.getRegion(), c.getImageUrl(), c.getStatus(),
            memberCount, groupCount, admins, c.getInviteCode()
        );
    }
}
