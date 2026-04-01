package com.board_game_back.Repository;

import com.board_game_back.Entity.MatchParticipant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {

    // 특정 유저의 최근 전적(참가 기록) 가져오기
    @Query("SELECT mp FROM MatchParticipant mp JOIN FETCH mp.matchRecord mr JOIN FETCH mr.boardGame WHERE mp.member.id = :memberId ORDER BY mr.playedAt DESC")
    List<MatchParticipant> findRecentMatchesByMemberId(@Param("memberId") Long memberId);

    // 특정 방에서 특정 멤버의 참가 기록 삭제 (방 나가기 시)
    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM MatchParticipant mp WHERE mp.member.id = :memberId AND mp.matchRecord.room.id = :roomId")
    void deleteByMemberIdAndRoomId(@Param("memberId") Long memberId, @Param("roomId") Long roomId);

    // 특정 멤버의 모든 참가 기록 삭제 (회원 탈퇴 시)
    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM MatchParticipant mp WHERE mp.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

}
