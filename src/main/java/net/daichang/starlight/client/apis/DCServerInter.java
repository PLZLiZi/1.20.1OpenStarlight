package net.daichang.starlight.client.apis;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public interface DCServerInter extends DCInter{
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

    ServerPlayer serverPlayer = server.getPlayerList().getPlayer(localPlayer.getUUID());

    ServerLevel serverLevel = serverPlayer.serverLevel();
}
