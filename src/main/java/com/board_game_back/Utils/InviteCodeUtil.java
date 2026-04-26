package com.board_game_back.Utils;

import java.util.UUID;

public class InviteCodeUtil {

    private InviteCodeUtil() {}

    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
