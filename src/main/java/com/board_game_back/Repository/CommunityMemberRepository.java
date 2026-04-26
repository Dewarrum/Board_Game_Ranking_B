package com.board_game_back.Repository;

import com.board_game_back.Entity.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    boolean existsByCommunityIdAndMemberId(Long communityId, Long memberId);

    long countByCommunityId(Long communityId);

    Optional<CommunityMember> findByCommunityIdAndMemberId(Long communityId, Long memberId);

    @Query("SELECT cm.community.id FROM CommunityMember cm WHERE cm.member.id = :memberId")
    List<Long> findCommunityIdsByMemberId(@Param("memberId") Long memberId);

    void deleteByCommunityId(Long communityId);
}
