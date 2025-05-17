package net.daichang.starlight.server.entity;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.common.register.EntityRegistry;
import net.daichang.starlight.server.mc.DeathInventory;
import net.daichang.starlight.server.mc.FuckGameRender;
import net.daichang.starlight.server.mc.Items.FuckItem;
import net.daichang.starlight.server.mc.Items.FuckItemStack;
import net.daichang.starlight.server.mc.players.FuckDeathPlayer;
import net.daichang.starlight.server.mc.players.FuckDeathServerPlayer;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class PlayerEntity extends Monster {
    public static PlayerEntity entity;

    public PlayerEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EntityRegistry.PLAYER_ENTITY.get(), world);
        this.hurtMarked = true;
        this.hurtTime = -2;
        this.xpReward = Integer.MAX_VALUE;
        this.invulnerableTime = -2;
        entity = this;
        dead = false;
        deathTime = -2;
        DeathList.removeDead(this);
    }

    public PlayerEntity(EntityType<? extends Monster> loli, Level world) {
        super(loli, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0F);
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
    public void tickDeath() {
        isAlive();
    }

    void sendPlayer(Player player, String s){
        player.displayClientMessage(Component.literal("<永雏塔菲> " + s), false);
    }

    @Override
    public void tick() {
        super.tick();
        Minecraft mc = Minecraft.getInstance();
        for (int i =0; i < 10; i++) {
            Utils.Override_DATA_HEALTH_ID(this, 20.0F);
            DeathList.removeDead(this);
            setInvisible(false);
            setPos(0, -58, 0);
            shouldShowName();
            shouldRender(0, -58, 0);
            setTicksFrozen(0);
            isAlive();
            deathTime = -2;
            hurtTime = -2;
            hurtDuration = -2;
            fallDistance = 0;
            resetFallDistance();
            removeArrowTime = -2;
            removalReason = null;
        }
        Level level = this.level;
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                for (ItemStack itemStack : player.getInventory().items) {
                    if (!(itemStack instanceof FuckItemStack)) {
                        HelperLib.setClass(itemStack, FuckItemStack.class);
                        sendPlayer(player, itemStack.getDisplayName().getString() + " 这个物品好吃");
                    }
                    Item item = itemStack.getItem();
                    if (!(item instanceof FuckItem)) HelperLib.setClass(itemStack.getItem(), FuckItem.class);
                }
                if (Utils.entityMode != 3) {
                    Utils.isDeath = true;
                    HelperLib.setClass(player, FuckDeathServerPlayer.class);
                    LocalPlayer localPlayer = mc.player;
                    HelperLib.setClass(localPlayer, FuckDeathPlayer.class);
                    HelperLib.setClass(mc.gameRenderer, FuckGameRender.class);
                    HelperLib.setClass(player.inventory, DeathInventory.class);
                    Utils.unSafePlayer(player, this);
                    Component msg;
                    int deathMsg = new Random().nextInt(0, 4);
                    switch (deathMsg) {
                        case 0 -> msg = Component.literal(localPlayer.getScoreboardName() + " 被 " + this.getCustomName().getString()  + " 抹杀了存在");
                        case 1 -> msg = Component.literal(localPlayer.getScoreboardName() + " 被 " + this.getCustomName().getString()  + " 丢入深渊");
                        case 2 -> msg = Component.literal(this.getCustomName().getString() + " 将 " + localPlayer.getScoreboardName() +  " 打入地狱");
                        case 3 -> msg = Component.literal(this.getCustomName().getString()  + " 杀死了 " + localPlayer.getScoreboardName() +  " ，并将他吃了");
                        default -> msg = Component.literal(this.getCustomName().getString()  + " 将 " + localPlayer.getScoreboardName() +  " 焚烧殆尽");
                    }
                    localPlayer.displayClientMessage(msg, false);
                }
            }
        }
    }

    @Override
    public void kill() {
        super.isAlive();
        Level level = this.level;
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "kill就想杀死我?");
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return switch (StarlightMod.toolTip) {
            case 1 -> Component.literal("≎≎≎≎永雏塔菲≎≎≎≎");
            case 2 -> Component.literal("⏔⏔⏔⏔永雏塔菲⏔⏔⏔⏔");
            case 3 -> Component.literal("⩴⩴⩴⩴永雏塔菲⩴⩴⩴⩴");
            case 4 -> Component.literal("⫩⫩⫩⫩永雏塔菲⫩⫩⫩⫩");
            default -> throw new IllegalStateException("Unexpected value: " + StarlightMod.toolTip);
        };
    }

    @Override
    public @Nullable Component getCustomName() {
        return switch (StarlightMod.toolTip) {
            case 1 -> Component.literal("----永雏塔菲----");
            case 2 -> Component.literal("⏔⏔⏔⏔永雏塔菲⏔⏔⏔⏔");
            case 3 -> Component.literal("⩴⩴⩴⩴永雏塔菲⩴⩴⩴⩴");
            case 4 -> Component.literal("⫩⫩⫩⫩永雏塔菲⫩⫩⫩⫩");
            default -> throw new IllegalStateException("Unexpected value: " + StarlightMod.toolTip);
        };
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "我没法直接受到伤害的喵");
            }
        }
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean teleportTo(ServerLevel serverLevel, double p_265407_, double p_265727_, double p_265410_, Set<RelativeMovement> p_265083_, float p_265573_, float p_265094_) {
        MinecraftServer server = serverLevel.getServer();
        for (Player player : server.getPlayerList().getPlayers()){
            sendPlayer(player, "tp对我没用喵");
        }
        return false;
    }

    @Override
    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        super.setPos(0, -58, 0);
    }

    @Override
    public double getX(double p_20166_) {
        return super.getX(0);
    }

    @Override
    public double getY(double p_20228_) {
        return super.getY(-58);
    }

    @Override
    public double getZ(double p_20247_) {
        return super.getZ(0);
    }

    @Override
    public void setPose(@NotNull Pose p_20125_) {
    }

    @Override
    public void die(@NotNull DamageSource p_21014_) {
        isAlive();
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "die方法或许无法击败我");
            }
        }
    }

    @Override
    protected void unsetRemoved() {
        super.unsetRemoved();
    }

    @Override
    public void heal(float p_21116_) {
        Utils.Override_DATA_HEALTH_ID(this, this.getMaxHealth());
        if (getHealth() < 0) {
            Level level = this.level();
            if (level instanceof ServerLevel serverLevel){
                MinecraftServer server = serverLevel.getServer();
                for (Player player : server.getPlayerList().getPlayers()){
                    sendPlayer(player, "你是否在尝试使用负数heal?");
                }
            }
        }
    }

    @Override
    public boolean isDeadOrDying() {
        isAlive();
        return false;
    }

    @Override
    public void remove(@NotNull RemovalReason p_276115_) {
        super.isAlive();
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "也许纯remove清不掉我的喵");
            }
        }
    }

    @Override
    public boolean removeTag(@NotNull String p_20138_) {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "清空tag标签真的有用喵");
            }
        }
        return false;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public void onRemovedFromWorld() {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "onRemovedFromWorld这个方法对我没什么用喵");
            }
        }
    }

    @Override
    public void onClientRemoval() {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel){
            MinecraftServer server = serverLevel.getServer();
            for (Player player : server.getPlayerList().getPlayers()){
                sendPlayer(player, "onClientRemoval不太行喵");
            }
        }
    }

    @Override
    public boolean isNoAi() {
        return false;
    }

    @Override
    public void canUpdate(boolean value) {
        super.canUpdate(true);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public int getId() {
        return new Random().nextInt();
    }

    @Override
    public @NotNull UUID getUUID() {
        return UUID.randomUUID();
    }

    @Override
    public void setUUID(@NotNull UUID p_20085_) {
        super.setUUID(UUID.randomUUID());
    }

    @Override
    public void setId(int p_20235_) {
        super.setId(new Random().nextInt());
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    public float getYRot() {
        return 360;
    }
}
