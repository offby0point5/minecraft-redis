package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

public class NetworkPlayerGroup {
    private static final String PREFIX = String.format("%S:player-group", NetRedis.NETWORK_PREFIX);

    private final UUID uuid;

    public NetworkPlayerGroup(UUID groupID) {
        uuid = groupID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getLeader() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return UUID.fromString(jedis.get(String.format("%s:leader", PREFIX)));
        }
    }

    public Set<UUID> getMembers() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return jedis.smembers(String.format("%s:members", PREFIX)).stream().map(UUID::fromString).collect(Collectors.toSet());
        }
    }
}
