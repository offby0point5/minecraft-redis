package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetworkServerGroup {
    private static final String PREFIX = String.format("%S:server-group", NetRedis.NETWORK_PREFIX);

    private static final Map<String, NetworkServerGroup> groups = new HashMap<>();

    public static NetworkServerGroup getInstance(String groupName) {
        if (groups.containsKey(groupName)) return groups.get(groupName);
        else return new NetworkServerGroup(groupName);
    }

    private final String name;
    private final JoinRules joinRule;
    private final KickRules kickRule;

    private NetworkServerGroup(String groupName) {
        groups.put(groupName, this);
        name = groupName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String joinRuleName = jedis.get(String.format("%s:%s:join-rule", PREFIX, name));
            joinRule = JoinRules.valueOf(joinRuleName);

            String kickRuleName = jedis.get(String.format("%s:%s:kick-rule", PREFIX, name));
            kickRule = KickRules.valueOf(kickRuleName);
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

    public Set<String> getMembers() {
        try (Jedis jedis = NetRedis.getJedis()) {
            return jedis.smembers(String.format("%s:%s:members", PREFIX, name));
        }
    }
}
