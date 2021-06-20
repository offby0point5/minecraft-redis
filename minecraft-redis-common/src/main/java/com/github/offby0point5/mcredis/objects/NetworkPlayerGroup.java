package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.util.*;

public class NetworkPlayerGroup {
    private static final String PREFIX = String.format("%S:player-group", NetRedis.NETWORK_PREFIX);

    private static final Map<UUID, NetworkPlayerGroup> groups = new HashMap<>();

    public static NetworkPlayerGroup getInstance(UUID groupID) {
        if (groups.containsKey(groupID)) return groups.get(groupID);
        else return new NetworkPlayerGroup(groupID);
    }

    private final UUID uuid;
    private NetworkSinglePlayer leader;
    private Set<NetworkSinglePlayer> members;

    private NetworkPlayerGroup(UUID groupID) {
        groups.put(groupID, this);
        uuid = groupID;
        update();
    }

    public void update() {
        try (Jedis jedis = NetRedis.getJedis()) {
            String leaderID = jedis.get(String.format("%s:leader", PREFIX));
            leader = NetworkSinglePlayer.getInstance(UUID.fromString(leaderID));

            Set<String> memberPlayerIDs = jedis.smembers(String.format("%s:members", PREFIX));
            members = new HashSet<>();
            for (String playerID :
                    memberPlayerIDs) {
                members.add(NetworkSinglePlayer.getInstance(UUID.fromString(playerID)));
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public NetworkSinglePlayer getLeader() {
        return leader;
    }

    public Set<NetworkSinglePlayer> getMembers() {
        return members;
    }
}
