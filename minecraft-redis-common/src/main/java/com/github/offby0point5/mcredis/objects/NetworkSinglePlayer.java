package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    private final String SERVER;
    private final String GROUP;


    private NetworkSinglePlayer(UUID playerUuid) {
        Objects.requireNonNull(playerUuid);
        SERVER = String.format("%s:%s:server", PREFIX, playerUuid);
        GROUP = String.format("%s:%s:group", PREFIX, playerUuid);

        players.put(playerUuid, this);
        this.uuid = playerUuid;
        update();
    }

    public void update() {
        try (Jedis jedis = NetRedis.getJedis()) {
            String serverName = jedis.get(SERVER);
            server = NetworkSingleServer.getInstance(serverName);

            String groupID = jedis.get(GROUP);
            playerGroup = NetworkPlayerGroup.getInstance(UUID.fromString(groupID));
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public NetworkSingleServer getServer() {
        return server;
    }

    protected void setServer(NetworkSingleServer server) {
        Objects.requireNonNull(server);
        try (Jedis jedis = NetRedis.getJedis()) {
            this.server = server;
            jedis.set(SERVER, server.getName());
        }

    }

    public NetworkPlayerGroup getPlayerGroup() {
        return playerGroup;
    }
}
