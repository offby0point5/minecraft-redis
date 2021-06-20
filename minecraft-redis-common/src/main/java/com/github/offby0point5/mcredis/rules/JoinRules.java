package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.objects.NetworkServerGroup;
import com.github.offby0point5.mcredis.objects.NetworkSinglePlayer;
import com.github.offby0point5.mcredis.objects.NetworkSingleServer;

public enum JoinRules implements ServerGroupJoinRule {
    NONE(((player, groupJoined) -> null))
    ;

    private final ServerGroupJoinRule rule;

    JoinRules(ServerGroupJoinRule joinRule) {
        rule = joinRule;
    }

    @Override
    public NetworkSingleServer getJoinServer(NetworkSinglePlayer player, NetworkServerGroup groupJoined) {
        return null;
    }
}
