package com.github.offby0point5.mcredis.proxy;

import com.github.offby0point5.mcredis.Group;
import com.github.offby0point5.mcredis.Network;
import com.github.offby0point5.mcredis.Player;
import com.github.offby0point5.mcredis.Server;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the network as seen by proxies.
 */
public class Manager {
    private static final Set<ServerGroup> groups = new HashSet<>();

    public static void setup() {
        // TODO: 25.06.21 implement proxy network manager setup
    }

    public static String getJoinServer(UUID playerID, String groupName) {
        Objects.requireNonNull(playerID);
        Objects.requireNonNull(groupName);
        Group group = new Group(groupName);
        JoinRules joinRule = group.getJoinRule();
        if (joinRule == null) return null;
        return joinRule.getJoinServer(new Player(playerID), group).getName();
    }

    public static String getKickGroup(UUID playerID, String serverName) {
        Objects.requireNonNull(playerID);
        Objects.requireNonNull(serverName);
        Server server = new Server(serverName);
        String groupName = server.getMain();
        if (groupName == null) return null;
        Group mainGroup = new Group(groupName);
        KickRules kickRule = mainGroup.getKickRule();
        if (kickRule == null) return null;
        return kickRule.getNewGroup(new Player(playerID), new Server(serverName)).getName();
    }

    public static void shutdown() {
        // TODO: 25.06.21 implement proxy network manager shutdown
    }
}
