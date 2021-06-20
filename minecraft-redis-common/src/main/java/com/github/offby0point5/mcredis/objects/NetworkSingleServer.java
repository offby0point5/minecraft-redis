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
    private ServerOnlineStatus status;
    private NetworkServerGroup main;
    private Set<NetworkServerGroup> groups;
    private Set<NetworkSinglePlayer> players;

    private NetworkSingleServer(String serverName) {
        servers.put(serverName, this);
        name = serverName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String[] inetAddr = jedis.get(String.format("%s:%s:address", PREFIX, name)).split("\\n");
            address = new InetSocketAddress(inetAddr[0], Integer.parseInt(inetAddr[1]));
        }
        update();
    }

    public void update() {
        try (Jedis jedis = NetRedis.getJedis()) {
            String onlineStatus = jedis.get(String.format("%s:%s:status", PREFIX, name));
            status = ServerOnlineStatus.valueOf(onlineStatus);

            String mainGroupName = jedis.get(String.format("%s:%s:main", PREFIX, name));
            main = NetworkServerGroup.getInstance(mainGroupName);

            Set<String> allGroupNames = jedis.smembers(String.format("%s:%s:groups", PREFIX, name));
            groups = new HashSet<>();
            for (String groupName : allGroupNames) {
                groups.add(NetworkServerGroup.getInstance(groupName));
            }

            Set<String> playerUUIDs = jedis.smembers(String.format("%s:%s:players", PREFIX, name));
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

    public ServerOnlineStatus getStatus() {
        return status;
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

    private enum ServerOnlineStatus {
        ONLINE,
        SOFT_FULL,
        HARD_FULL
    }
}
