package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class Player {
    private static final String PREFIX = String.format("%S:player", NetRedis.NETWORK_PREFIX);

    private final UUID uuid;

    private Player(UUID playerUuid) {
        this.uuid = playerUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getServer() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return jedis.get(String.format("%s:%s:server", PREFIX, uuid));
        }
    }

    public UUID getPlayerGroup() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return UUID.fromString(jedis.get(String.format("%s:%s:group", PREFIX, uuid)));
        }
    }
}
