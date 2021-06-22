package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;
import redis.clients.jedis.Jedis;

import java.util.*;

public class NetworkServerGroup {
    private static final String PREFIX = String.format("%S:server-group", NetRedis.NETWORK_PREFIX);

    private static final Map<String, NetworkServerGroup> groups = new HashMap<>();

    public static NetworkServerGroup getInstance(String groupName) {
        if (groups.containsKey(groupName)) return groups.get(groupName);
        else return new NetworkServerGroup(groupName);
    }

    public static NetworkServerGroup createAndGetInstance(String groupName,
                                                          JoinRules groupJoinRule,
                                                          KickRules groupKickRule) {
        Objects.requireNonNull(groupName);
        try (Jedis jedis = NetRedis.getJedis()) {
            if (!jedis.keys(String.format("%s:%s", PREFIX, groupName)).isEmpty()) {
                throw new IllegalStateException("This group already exists.");
            }
            Objects.requireNonNull(groupJoinRule);
            Objects.requireNonNull(groupKickRule);

            jedis.set(String.format("%s:%s:join-rule", PREFIX, groupName), groupJoinRule.name());
            jedis.set(String.format("%s:%s:kick-rule", PREFIX, groupName), groupKickRule.name());
        }
        return new NetworkServerGroup(groupName);
    }

    private final String name;
    private final JoinRules joinRule;
    private final KickRules kickRule;
    private Set<NetworkSingleServer> members;

    private final String MEMBERS;

    private NetworkServerGroup(String groupName) {
        Objects.requireNonNull(groupName);
        String JOIN_RULE = String.format("%s:%s:join-rule", PREFIX, groupName);
        String KICK_RULE = String.format("%s:%s:kick-rule", PREFIX, groupName);
        MEMBERS = String.format("%s:%s:members", PREFIX, groupName);

        groups.put(groupName, this);
        name = groupName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String joinRuleName = jedis.get(JOIN_RULE);
            Objects.requireNonNull(joinRuleName);
            joinRule = JoinRules.valueOf(joinRuleName);

            String kickRuleName = jedis.get(KICK_RULE);
            Objects.requireNonNull(kickRuleName);
            kickRule = KickRules.valueOf(kickRuleName);
        }
        update();
    }

    public void update() {
        try (Jedis jedis = NetRedis.getJedis()) {
            Set<String> memberNames = jedis.smembers(MEMBERS);
            members = new HashSet<>();
            for (String memberName : memberNames) {
                members.add(NetworkSingleServer.getInstance(memberName));
            }
        }
    }

    public String getName() {
        return name;
    }

    public JoinRules getJoinRule() {
        return joinRule;
    }

    public KickRules getKickRule() {
        return kickRule;
    }

    public Set<NetworkSingleServer> getMembers() {
        return members;
    }

    protected void addServer(NetworkSingleServer server) {
        try (Jedis jedis = NetRedis.getJedis()) {
            members.add(server);
            jedis.sadd(MEMBERS, server.getName());
        }
    }

    protected void removeServer(NetworkSingleServer server) {
        try (Jedis jedis = NetRedis.getJedis()) {
            members.remove(server);
            jedis.srem(MEMBERS, server.getName());
        }
    }
}
