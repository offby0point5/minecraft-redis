package com.github.offby0point5.mcredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class NetRedis {
    private static final JedisPool JEDIS_POOL = new JedisPool(new JedisPoolConfig(), "localhost");
    public static final String NETWORK_PREFIX = "mcn";

    // TODO: 20.06.21 add actions between network elements
    // todo tests
    // todo readme.md

    public static Jedis getJedis() {
        Jedis jedis = JEDIS_POOL.getResource();
        // TODO: 20.06.21 authentication
        return jedis;
    }
}
