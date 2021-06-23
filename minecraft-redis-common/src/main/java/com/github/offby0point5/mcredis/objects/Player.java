package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.UUID;

public class Player {
    private static final String PREFIX = String.format("%S:player", NetRedis.NETWORK_PREFIX);

    private final UUID uuid;

    protected final String SERVER;
    protected final String PARTY;

    public Player(UUID playerUuid) {
        SERVER = String.format("%s:%s:server", PREFIX, playerUuid);
        PARTY = String.format("%s:%s:party", PREFIX, playerUuid);
        this.uuid = playerUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getServer() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return jedis.get(SERVER);
        }
    }

    public UUID getParty() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return UUID.fromString(jedis.get(PARTY));
        }
    }

    public void delete() {
        String serverName = getServer();
        UUID partyId = getParty();
        try (Jedis jedis = NetRedis.getJedis()) {
            Transaction transaction = jedis.multi();
            if (serverName != null) {
                Server server = new Server(getServer());
                transaction.srem(server.PLAYERS, partyId.toString());
            }

            if (partyId != null) {
                Party party = new Party(partyId);
                transaction.srem(party.MEMBERS, partyId.toString());
                if (party.getLeader().equals(uuid))
                    transaction.del(party.LEADER);
            }
            transaction.del(SERVER);
            transaction.del(PARTY);

            transaction.exec();
        }
    }
}
