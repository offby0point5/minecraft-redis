package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    private static final String PREFIX = String.format("%S:server", NetRedis.NETWORK_PREFIX);

    private final String name;
    private final InetSocketAddress address;

    private Server(String serverName) {
        name = serverName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String[] inetAddr = jedis.get(String.format("%s:%s:address", PREFIX, name)).split("\\n");
            address = new InetSocketAddress(inetAddr[0], Integer.parseInt(inetAddr[1]));
        }
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public ServerOnlineStatus getStatus() {
        try (Jedis jedis = NetRedis.getJedis()){
            return ServerOnlineStatus.valueOf(jedis.get(String.format("%s:%s:status", PREFIX, name)));
        }
    }

    public String getMain() {
        try (Jedis jedis = NetRedis.getJedis()){
            return jedis.get(String.format("%s:%s:main", PREFIX, name));
        }
    }

    public Set<String> getGroups() {
        try (Jedis jedis = NetRedis.getJedis()){
            return jedis.smembers(String.format("%s:%s:groups", PREFIX, name));
        }
    }

    public Set<UUID> getPlayers() {
        try (Jedis jedis = NetRedis.getJedis()){
            return jedis.smembers(String.format("%s:%s:players", PREFIX, name)).stream().map(UUID::fromString).collect(Collectors.toSet());
        }
    }

    private enum ServerOnlineStatus {
        ONLINE,
        SOFT_FULL,
        HARD_FULL
    }
}
