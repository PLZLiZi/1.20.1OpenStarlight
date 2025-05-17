package net.daichang.starlight.server.mc.players;

import com.mojang.authlib.GameProfile;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FuckDeathServerPlayer extends ServerPlayer {
    public FuckDeathServerPlayer(MinecraftServer p_254143_, ServerLevel p_254435_, GameProfile p_253651_) {
        super(p_254143_, p_254435_, p_253651_);
    }

    @Override
    public float getHealth() {
        return 0.0F;
    }

    @Override
    public void setHealth(float p_21154_) {
        super.setHealth(0.0F);
    }

    @Override
    public boolean isDeadOrDying() {
        return true;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (Minecraft.getInstance().getCameraEntity() instanceof ServerPlayer serverPlayer){
            HelperLib.setClass(serverPlayer, FuckDeathServerPlayer.class);
        }
        Utils.unSafePlayer(this);
    }
}
