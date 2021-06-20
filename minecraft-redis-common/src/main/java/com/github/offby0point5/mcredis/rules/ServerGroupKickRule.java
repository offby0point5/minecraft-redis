package com.github.offby0point5.mcredis.rules;

import com.github.offby0point5.mcredis.objects.NetworkServerGroup;
import com.github.offby0point5.mcredis.objects.NetworkSinglePlayer;
import com.github.offby0point5.mcredis.objects.NetworkSingleServer;

public interface ServerGroupKickRule {
    NetworkServerGroup getNewGroup(NetworkSinglePlayer player, NetworkSingleServer kickedFrom);
}
