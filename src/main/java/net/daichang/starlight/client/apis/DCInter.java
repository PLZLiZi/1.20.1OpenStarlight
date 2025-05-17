package net.daichang.starlight.client.apis;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.daichang.starlight.server.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.UUID;

public interface DCInter {
    IEventBus eventBus = MinecraftForge.EVENT_BUS;

    Minecraft mc = Minecraft.getInstance();

    Window window = mc.getWindow();

    LocalPlayer localPlayer = mc.player;

    GameRenderer gameRenderer = mc.gameRenderer;

    User user = mc.getUser();

    GameProfile gameProfile = user.getGameProfile();

    Font font = mc.font;

    FontManager fontManger = mc.fontManager;

    DebugRenderer debugRenderer = mc.debugRenderer;

    Screen screen = mc.screen;

    boolean noRender = mc.noRender;

    boolean isDemo = true;

    ClientLevel clientLevel = mc.level;

    LevelRenderer levelRender = mc.levelRenderer;

    RenderBuffers renderBuffers = mc.renderBuffers;

    Entity cameraEntity = mc.getCameraEntity();

    String id = "starlight";

    UUID uuid = user.getProfileId();

    ResourceLocation STAR_SKIN = new ResourceLocation("starlight:textures/entity/skin.png");

    ResourceLocation STAR_CAPE = new ResourceLocation("starlight:textures/entity/cape.png");

    default void defPlayer(Player player) {
        Utils.starLightPlayer(player);
        player.removeAllEffects();
    }

    default void kill(Object o) {
        if (o instanceof Entity e) {
            Utils.killEntity(e);
        } else if (o instanceof BlockEntity block) {
            block.setRemoved();
            block.isRemoved();
            block.setChanged();
        } else if (o instanceof Level level){

        }
    }
}