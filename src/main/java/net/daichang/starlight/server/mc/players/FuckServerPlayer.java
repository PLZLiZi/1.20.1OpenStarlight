package net.daichang.starlight.server.mc.players;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.daichang.starlight.server.util.Utils;

public class FuckServerPlayer extends ServerPlayer {
    public FuckServerPlayer(MinecraftServer p_254143_, ServerLevel p_254435_, GameProfile p_253651_) {
        super(p_254143_, p_254435_, p_253651_);
    }

    @Override
    public float getHealth() {
        return 20.0F;
    }

    @Override
    public void setHealth(float p_21154_) {
        super.setHealth(20.0F);
    }

    @Override
    public boolean hurt(DamageSource p_108662_, float p_108663_) {
        return false;
    }

    @Override
    public void die(DamageSource p_9035_) {}

    @Override
    public void tick() {
        super.tick();
        Utils.starLightPlayer(this);
    }
}
