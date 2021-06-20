package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetworkSinglePlayer {
    private static final String PREFIX = String.format("%S:player", NetRedis.NETWORK_PREFIX);

    private static final Map<UUID, NetworkSinglePlayer> players = new HashMap<>();

    public static NetworkSinglePlayer getInstance(UUID playerUUID) {
        if (players.containsKey(playerUUID)) return players.get(playerUUID);
        else return new NetworkSinglePlayer(playerUUID);
    }

    private final UUID uuid;
    private NetworkSingleServer server;
    private NetworkPlayerGroup playerGroup;

    private NetworkSinglePlayer(UUID playerUuid) {
        players.put(playerUuid, this);
        this.uuid = playerUuid;
        update();
    }

    public void update() {
        try (Jedis jedis = NetRedis.getJedis()) {
            String serverName = jedis.get(String.format("%s:%s:server", PREFIX, uuid));
            server = NetworkSingleServer.getInstance(serverName);

            String groupID = jedis.get(String.format("%s:%s:group", PREFIX, uuid));
            playerGroup = NetworkPlayerGroup.getInstance(UUID.fromString(groupID));
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public NetworkSingleServer getServer() {
        return server;
    }

    public NetworkPlayerGroup getPlayerGroup() {
        return playerGroup;
    }
}
