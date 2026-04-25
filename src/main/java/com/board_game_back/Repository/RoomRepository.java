package com.board_game_back.Repository;

import com.board_game_back.Entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByInviteCode(String inviteCode);
    List<Room> findByCommunityId(Long communityId);
}
