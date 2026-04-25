package com.board_game_back.Repository;

import com.board_game_back.Entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Optional<Community> findByCreatedBy(Long memberId);
}
