package net.daichang.starlight.server.mc.players;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.client.apis.DCInter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatsCounter;
import org.jetbrains.annotations.NotNull;

public class FuckLoalPlayer extends LocalPlayer implements DCInter {
    public FuckLoalPlayer(Minecraft p_108621_, ClientLevel p_108622_, ClientPacketListener p_108623_, StatsCounter p_108624_, ClientRecipeBook p_108625_, boolean p_108626_, boolean p_108627_) {
        super(p_108621_, p_108622_, p_108623_, p_108624_, p_108625_, p_108626_, p_108627_);
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
    public @NotNull ResourceLocation getSkinTextureLocation() {
        return STAR_SKIN;
    }

    @Override
    public void tick() {
        super.tick();
        Utils.starLightPlayer(this);
    }
}
