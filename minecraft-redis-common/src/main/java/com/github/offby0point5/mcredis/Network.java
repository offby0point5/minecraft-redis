package com.github.offby0point5.mcredis;

import com.github.offby0point5.mcredis.objects.NetworkPlayerGroup;
import com.github.offby0point5.mcredis.objects.NetworkServerGroup;
import com.github.offby0point5.mcredis.objects.NetworkSinglePlayer;
import com.github.offby0point5.mcredis.objects.NetworkSingleServer;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Network {
    public static Set<String> getAllServerNames() {
        Set<String> entries;
        try (Jedis jedis = NetRedis.getJedis()) {
            entries = jedis.keys(String.format("%s:*", NetworkSingleServer.PREFIX));
        }
        return entries;
    }

    public static Set<String> getAllGroupNames() {
        Set<String> entries;
        try (Jedis jedis = NetRedis.getJedis()) {
            entries = jedis.keys(String.format("%s:*", NetworkServerGroup.PREFIX));
        }
        return entries;
    }

    public static Set<UUID> getAllPlayerIds() {
        Set<UUID> entries;
        try (Jedis jedis = NetRedis.getJedis()) {
            entries = jedis.keys(String.format("%s:*", NetworkSinglePlayer.PREFIX))
                    .stream().map(UUID::fromString).collect(Collectors.toSet());
        }
        return entries;
    }

    public static Set<UUID> getAllPartyIds() {
        Set<UUID> entries;
        try (Jedis jedis = NetRedis.getJedis()) {
            entries = jedis.keys(String.format("%s:*", NetworkPlayerGroup.PREFIX))
                    .stream().map(UUID::fromString).collect(Collectors.toSet());
        }
        return entries;
    }

}
