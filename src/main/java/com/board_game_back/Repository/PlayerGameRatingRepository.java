package com.board_game_back.Repository;

import com.board_game_back.Entity.BoardGame;
import com.board_game_back.Entity.Member;
import com.board_game_back.Entity.PlayerGameRating;
import com.board_game_back.Entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerGameRatingRepository extends JpaRepository<PlayerGameRating, Long> {

    List<PlayerGameRating> findByBoardGameIdOrderByGameStatsRatingDesc(Long boardGameId);

    Optional<PlayerGameRating> findByMemberAndBoardGameAndRoom(Member member, BoardGame boardGame, Room room);

List<PlayerGameRating> findByRoomIdAndBoardGameIdOrderByGameStatsRatingDesc(Long roomId, Long boardGameId);

    List<PlayerGameRating> findByRoomIdAndBoardGameIdAndPlayCountGreaterThanEqualOrderByGameStatsRatingDesc(
        Long roomId, Long boardGameId, int minPlayCount);

    // 멤버ID + 방ID로 조회
    Optional<PlayerGameRating> findByMember_IdAndRoom_Id(Long memberId, Long roomId);

    // 방별 랭킹 - JOIN FETCH로 N+1 방지
    @org.springframework.data.jpa.repository.Query("SELECT p FROM PlayerGameRating p JOIN FETCH p.member WHERE p.room.id = :roomId AND p.boardGame.id = :boardGameId ORDER BY CASE WHEN p.playCount > 0 THEN 0 ELSE 1 END ASC, p.gameStats.rating DESC")
    List<PlayerGameRating> findByRoomIdAndBoardGameIdOrderByPlayedThenRating(@org.springframework.data.repository.query.Param("roomId") Long roomId, @org.springframework.data.repository.query.Param("boardGameId") Long boardGameId);

    // 글로벌 랭킹용 - JOIN FETCH로 N+1 방지
    @org.springframework.data.jpa.repository.Query("SELECT p FROM PlayerGameRating p JOIN FETCH p.member WHERE p.boardGame.id = :boardGameId ORDER BY p.gameStats.rating DESC")
    List<PlayerGameRating> findByBoardGameIdWithMember(@org.springframework.data.repository.query.Param("boardGameId") Long boardGameId);

    // 멤버별 플레이한 게임 통계용 (playCount > 0)
    @org.springframework.data.jpa.repository.Query("SELECT p FROM PlayerGameRating p JOIN FETCH p.boardGame WHERE p.member.id = :memberId AND p.playCount > 0")
    List<PlayerGameRating> findPlayedByMemberId(@org.springframework.data.repository.query.Param("memberId") Long memberId);

    // 방 삭제 시 FK 제거용
    void deleteByRoomId(Long roomId);

    // 방 나가기 시 해당 멤버의 방별 점수 삭제
    void deleteByMember_IdAndRoom_Id(Long memberId, Long roomId);

    // 회원 탈퇴 시 해당 멤버의 모든 점수 삭제
    void deleteByMember_Id(Long memberId);
}
