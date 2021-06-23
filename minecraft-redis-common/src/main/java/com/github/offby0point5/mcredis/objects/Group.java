package com.github.offby0point5.mcredis.objects;

import com.github.offby0point5.mcredis.NetRedis;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Set;

public class Group {
    private static final String PREFIX = String.format("%S:server-group", NetRedis.NETWORK_PREFIX);

    private final String name;
    private final JoinRules joinRule;
    private final KickRules kickRule;

    protected final String JOIN;
    protected final String KICK;
    protected final String MEMBERS;

    public Group(String groupName) {
        JOIN = String.format("%s:%s:join-rule", PREFIX, groupName);
        KICK = String.format("%s:%s:kick-rule", PREFIX, groupName);
        MEMBERS = String.format("%s:%s:members", PREFIX, groupName);

        name = groupName;
        try (Jedis jedis = NetRedis.getJedis()) {
            String joinRuleName = jedis.get(JOIN);
            joinRule = JoinRules.valueOf(joinRuleName);

            String kickRuleName = jedis.get(KICK);
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
            return jedis.smembers(MEMBERS);
        }
    }

    public void delete() {
        try (Jedis jedis = NetRedis.getJedis()) {
            Transaction transaction = jedis.multi();
            for (String serverName : getMembers()) {
                Server server = new Server(serverName);
                transaction.srem(server.ALL_GROUPS, name);
                if (server.getMain().equals(name)) transaction.set(server.MAIN_GROUP, "none");
            }
            transaction.del(JOIN);
            transaction.del(KICK);
            transaction.del(MEMBERS);
            transaction.exec();
        }
    }
}
