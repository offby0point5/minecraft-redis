package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.objects.NetworkServerGroup;
import com.github.offby0point5.mcredis.objects.NetworkSinglePlayer;
import com.github.offby0point5.mcredis.objects.NetworkSingleServer;

public enum JoinRules {
    NONE(((player, groupJoined) -> null))
    ;

    private final ServerGroupJoinRule rule;

    JoinRules(ServerGroupJoinRule joinRule) {
        rule = joinRule;
    }

    public NetworkSingleServer getJoinServer(NetworkSinglePlayer player, NetworkServerGroup groupJoined) {
        return null;
    }

    private interface ServerGroupJoinRule {
        NetworkSingleServer getJoinServer(NetworkSinglePlayer player, NetworkServerGroup groupJoined);
    }
}
