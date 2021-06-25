package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Player;
import com.github.offby0point5.mcredis.Server;

public enum JoinRules {
    NONE(((player, groupJoined) -> null))
    ;

    private final ServerGroupJoinRule rule;

    JoinRules(ServerGroupJoinRule joinRule) {
        rule = joinRule;
    }

    public Server getJoinServer(Player player, Group groupJoined) {
        return rule.getJoinServer(player, groupJoined);
    }

    private interface ServerGroupJoinRule {
        Server getJoinServer(Player player, Group groupJoined);
    }
}
