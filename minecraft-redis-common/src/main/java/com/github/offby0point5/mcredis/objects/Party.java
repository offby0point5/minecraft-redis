package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class Party {
    private static final String PREFIX = String.format("%S:player-group", NetRedis.NETWORK_PREFIX);

    private final UUID uuid;

    protected final String LEADER;
    protected final String MEMBERS;

    public Party(UUID groupID) {
        LEADER = String.format("%s:leader", PREFIX);
        MEMBERS = String.format("%s:members", PREFIX);
        uuid = groupID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getLeader() {
        try (Jedis jedis = NetRedis.getJedis()) {
            String leaderUUID = jedis.get(LEADER);
            if (leaderUUID == null) return null;
            return UUID.fromString(leaderUUID);
        }
    }

    public Set<UUID> getMembers() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return jedis.smembers(MEMBERS).stream().map(UUID::fromString).collect(Collectors.toSet());
        }
    }

    public void delete() {
        try (Jedis jedis = NetRedis.getJedis()) {
            Transaction transaction = jedis.multi();
            for (UUID playerId : getMembers()) {
                Player player = new Player(playerId);
                transaction.del(player.PARTY);
            }
            Player player = new Player(getLeader());
            transaction.del(player.PARTY);
            transaction.del(LEADER);
            transaction.del(MEMBERS);
            transaction.exec();
        }
    }
}
