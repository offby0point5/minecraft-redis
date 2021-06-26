package com.github.offby0point5.minecraftredis;

import com.github.offby0point5.mcredis.backend.Manager;
import com.github.offby0point5.mcredis.datatype.ItemStack;
import com.github.offby0point5.mcredis.rules.JoinRules;
import com.github.offby0point5.mcredis.rules.KickRules;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import java.util.List;

public class MinestomRedis extends Extension {

    @Override
    public void initialize() {
        // TODO: 27.06.21 Read from a configuration file
        String serverName = "server1";
        String mainGroup = "lobby";
        String[] allGroups = {};

        Manager.setup(serverName,
                MinecraftServer.getNettyServer().getServerChannel().localAddress(),
                mainGroup,
                new ItemStack.Builder("BIRCH_SAPLING", serverName)
                        .lore(List.of("The lobby. Just the lobby."))
                        .build(),
                JoinRules.NONE,
                KickRules.NONE,
                allGroups);
        MinecraftServer.LOGGER.info("minecraft-redis started.");
    }

    @Override
    public void terminate() {
        Manager.shutdown();
        MinecraftServer.LOGGER.info("minecraft-redis shut down.");
    }
}
