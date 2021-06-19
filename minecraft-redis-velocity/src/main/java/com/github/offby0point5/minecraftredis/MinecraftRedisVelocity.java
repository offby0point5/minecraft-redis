package com.github.offby0point5.minecraftredis;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "minecraft-redis-velocity",
        name = "Minecraft Redis",
        version = "1.0-SNAPSHOT",
        authors = {"offbyone"}
)
public class MinecraftRedisVelocity {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
