package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.objects.NetworkServerGroup;
import com.github.offby0point5.mcredis.objects.NetworkSinglePlayer;
import com.github.offby0point5.mcredis.objects.NetworkSingleServer;

public enum KickRules implements ServerGroupKickRule {
    NONE((player, kickedFrom) -> null),
    ;

    private final ServerGroupKickRule rule;

    KickRules(ServerGroupKickRule kickRule) {
        rule = kickRule;
    }

    @Override
    public NetworkServerGroup getNewGroup(NetworkSinglePlayer player, NetworkSingleServer kickedFrom) {
        return rule.getNewGroup(player, kickedFrom);
    }
}
