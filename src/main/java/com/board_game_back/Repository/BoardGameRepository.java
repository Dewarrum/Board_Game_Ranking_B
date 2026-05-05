package com.board_game_back.Repository;

import com.board_game_back.Entity.BoardGame;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {

    // 이름으로 게임 찾기 (필요시)
    Optional<BoardGame> findByName(String name);

    List<BoardGame> findByIdIn(Collection<Long> ids);
}
