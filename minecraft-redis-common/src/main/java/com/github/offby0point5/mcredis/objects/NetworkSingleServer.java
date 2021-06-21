package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;
import java.util.*;

public class NetworkSingleServer {
    private static final String PREFIX = String.format("%S:server", NetRedis.NETWORK_PREFIX);

    private static final Map<String, NetworkSingleServer> servers = new HashMap<>();

    public static NetworkSingleServer getInstance(String serverName) {
        if (servers.containsKey(serverName)) return servers.get(serverName);
        else return new NetworkSingleServer(serverName);
    }

    private final String name;
    private final InetSocketAddress address;
    private final NetworkServerGroup main;
    private ServerStatus status;
    private Set<NetworkServerGroup> groups;
    private Set<NetworkSinglePlayer> players;

    private final String PLAYERS;
    private final String STATUS;
    private final String GROUPS;

    private NetworkSingleServer(String serverName) {
         PLAYERS = String.format("%s:%s:players", PREFIX, serverName);
        String ADDRESS = String.format("%s:%s:address", PREFIX, serverName);
        String MAIN_GROUP = String.format("%s:%s:main", PREFIX, serverName);
         STATUS = String.format("%s:%s:status", PREFIX, serverName);
         GROUPS = String.format("%s:%s:groups", PREFIX, serverName);

        servers.put(serverName, this);
        name = serverName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String[] inetAddr = jedis.get(ADDRESS).split("\\n");
            address = new InetSocketAddress(inetAddr[0], Integer.parseInt(inetAddr[1]));

            String mainGroupName = jedis.get(MAIN_GROUP);
            main = NetworkServerGroup.getInstance(mainGroupName);
        }
        update();
    }

    public void update() {
        try (Jedis jedis = NetRedis.getJedis()) {
            String onlineStatus = jedis.get(STATUS);
            status = ServerStatus.valueOf(onlineStatus);

            Set<String> allGroupNames = jedis.smembers(GROUPS);
            groups = new HashSet<>();
            for (String groupName : allGroupNames) {
                groups.add(NetworkServerGroup.getInstance(groupName));
            }

            Set<String> playerUUIDs = jedis.smembers(PLAYERS);
            players = new HashSet<>();
            for (String playerUUID : playerUUIDs) {
                players.add(NetworkSinglePlayer.getInstance(UUID.fromString(playerUUID)));
            }
        }
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    public NetworkServerGroup getMain() {
        return main;
    }

    public Set<NetworkServerGroup> getGroups() {
        return groups;
    }

    public Set<NetworkSinglePlayer> getPlayers() {
        return players;
    }

    public void join(NetworkServerGroup group) {

    }

    public void playerJoin(NetworkSinglePlayer player) {
        try (Jedis jedis = NetRedis.getJedis()) {
            jedis.sadd(PLAYERS, player.getUuid().toString());
            this.players.add(player);
            player.setServer(this);
        }
    }

    public void playerLeave(NetworkSinglePlayer player) {
        try (Jedis jedis = NetRedis.getJedis()) {
            jedis.srem(PLAYERS, player.getUuid().toString());
            this.players.remove(player);
        }
    }

    public void joinGroup(NetworkServerGroup group) {
        try (Jedis jedis = NetRedis.getJedis()) {
            group.addServer(this);
            jedis.sadd(GROUPS, group.getName());
        }
    }

    public void leaveGroup(NetworkServerGroup group) {
        try (Jedis jedis = NetRedis.getJedis()) {
            group.removeServer(this);
            jedis.srem(GROUPS, group.getName());
        }
    }

    public enum ServerStatus {
        ONLINE,
        SOFT_FULL,
        HARD_FULL
    }
}
