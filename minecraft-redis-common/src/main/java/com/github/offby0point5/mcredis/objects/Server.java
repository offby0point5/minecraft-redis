package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    private static final String PREFIX = String.format("%S:server", NetRedis.NETWORK_PREFIX);

    private final String name;
    private final InetSocketAddress address;

    protected final String ADDRESS;
    protected final String STATUS;
    protected final String MAIN_GROUP;
    protected final String ALL_GROUPS;
    protected final String PLAYERS;

    public Server(String serverName) {
        ADDRESS = String.format("%s:%s:address", PREFIX, serverName);
        STATUS = String.format("%s:%s:status", PREFIX, serverName);
        MAIN_GROUP = String.format("%s:%s:main", PREFIX, serverName);
        ALL_GROUPS = String.format("%s:%s:groups", PREFIX, serverName);
        PLAYERS = String.format("%s:%s:players", PREFIX, serverName);
        name = serverName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String[] inetAddr = jedis.get(ADDRESS).split("\\n");
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
            return ServerOnlineStatus.valueOf(jedis.get(STATUS));
        }
    }

    public String getMain() {
        try (Jedis jedis = NetRedis.getJedis()){
            return jedis.get(MAIN_GROUP);
        }
    }

    public Set<String> getGroups() {
        try (Jedis jedis = NetRedis.getJedis()){
            return jedis.smembers(ALL_GROUPS);
        }
    }

    public Set<UUID> getPlayers() {
        try (Jedis jedis = NetRedis.getJedis()){
            return jedis.smembers(PLAYERS).stream().map(UUID::fromString).collect(Collectors.toSet());
        }
    }

    public void delete() {
        try (Jedis jedis = NetRedis.getJedis()) {
            Transaction transaction = jedis.multi();
            for (String groupName : getGroups()) {
                Group group = new Group(groupName);
                transaction.srem(group.MEMBERS, name);
            }
            Group group = new Group(getMain());
            transaction.srem(group.MEMBERS, name);
            transaction.del(ADDRESS);
            transaction.del(STATUS);
            transaction.del(MAIN_GROUP);
            transaction.del(ALL_GROUPS);
            for (UUID playerId : getPlayers()) {
                Player player = new Player(playerId);
                transaction.del(player.SERVER);
            }
            transaction.del(PLAYERS);
            transaction.exec();
        }
    }

    private enum ServerOnlineStatus {
        ONLINE,
        SOFT_FULL,
        HARD_FULL
    }
}
