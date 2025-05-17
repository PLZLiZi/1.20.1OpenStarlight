package net.daichang.starlight.common.items;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.apis.DCInter;
import net.daichang.starlight.client.apis.StarLightAPI;
import net.daichang.starlight.common.register.ItemRegister;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.server.mc.Items.StarItemStack;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.TextUtil;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ElyterminalSword extends Item implements StarLightAPI, IClientItemExtensions, DCInter {
    static int barWidth = 0;

    public ElyterminalSword() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_41452_) {
        if (localPlayer != null && localPlayer.isShiftKeyDown()) {
            return UseAnim.BOW;
        } else  {
            return Utils.getUseAnim();
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide){
            mc.gameRenderer.resetData();
            mc.getMainRenderTarget().clear(true);
            mc.getMainRenderTarget().checkStatus();
            mc.levelRenderer.allChanged();
            mc.levelRenderer.needsUpdate();
        }
        player.startUsingItem(hand);
        Utils.killLevelEntity(level);
        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack p_41454_) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        switch (StarlightMod.toolTip){
            case 1 :{
                list.add(Component.literal(""));
                list.add(Component.literal("见星海无岸，证宇宙无边，我们一路同行，去往梦的彼方                       "));
                break;
            }
            case 2: {
                list.add(Component.literal(""));
                list.add(Component.literal("我们，在那属于传奇的时间线上，经历春夏秋冬...                          "));
                break;
            }
            case 3:{
                list.add(Component.literal(""));
                list.add(Component.literal("她，                                                            "));
                list.add(Component.literal("诞生于时间之初，"));
                list.add(Component.literal("穿梭于维度之外，"));
                list.add(Component.literal("泛着神秘的辉光....."));
                break;
            }
            case 4 :{
                list.add(Component.literal(""));
                list.add(Component.literal("那飘零于时间的故事                                                 "));
                list.add(Component.literal("如是初源，如是终焉"));
                list.add(Component.literal("分叉的起点，终将在结局交汇"));
                list.add(Component.literal("那从往世荫蔽中破土的未来"));
                list.add(Component.literal("如炬如光"));
                list.add(Component.literal("繁花谢世之时，万物自此新生..."));
                break;
            }
        }
        list.add(Component.literal(""));
        list.add(Component.literal("在手中时:"));
        list.add(Component.literal(" +宇宙无边 力量"));
        list.add(Component.literal(" +Terminal 攻击速度"));
        list.add(Component.literal(""));
        list.add(Component.literal("在心中时:"));
        list.add(Component.literal(" +逐梦星光 守护"));
        list.add(Component.literal(" +传奇 所向披靡"));
        list.add(Component.literal(""));
        list.add(Component.literal("Starlight 「Re」"));
        list.add(Component.literal("  「Re」 在最暴躁的代码中「重铸」"));
        list.add(Component.literal("  「Re」 在最闪亮的群星中「重生」"));
        list.add(Component.literal("  「Re」之时，踏破黑暗，星辰之光，再度闪耀！"));
        list.add(Component.literal("  「REBUILD BY PLZLiZi」"));
        list.add(Component.literal(""));
        if (StarlightMod.isDemo) {
            list.add(Component.literal("测试版"));
        }
        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal(TextUtil.GetColor("    「 S t a r l i g h t 」                "));
    }

    @Override
    public @NotNull Item asItem() {
        return ItemRegister.STARLIGHT_ITEM.get();
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        Utils.starLightPlayer(player);
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull Level p_41405_, @NotNull Entity p_41406_, int p_41407_, boolean p_41408_) {
        if (p_41406_ instanceof Player player) Utils.starLightPlayer(player);
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull Font getFont(ItemStack stack, FontContext context) {
                return FuckFont.getFont();
            }
        });
        super.initializeClient(consumer);
    }

    @Override
    public @Nullable Font getFont(ItemStack stack, FontContext context) {
        return FuckFont.getFont();
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        Utils.killEntity(entity);
        entityKill(entity);
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack p_41395_, @NotNull LivingEntity p_41396_, @NotNull LivingEntity p_41397_) {
        Utils.killEntity(p_41396_);
        return super.hurtEnemy(p_41395_, p_41396_, p_41397_);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack p_41412_, @NotNull Level p_41413_, @NotNull LivingEntity p_41414_, int p_41415_) {
        Utils.killLevelEntity(p_41413_);
        Utils.spawnRainbowLighting(p_41413_, p_41414_);
        if (p_41414_.isShiftKeyDown()) {
            DeathList.clearAll();
        }
        super.releaseUsing(p_41412_, p_41413_, p_41414_, p_41415_);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack p_41453_) {
        return false;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack p_150899_) {
        return true;
    }

    @Override
    public int getBarColor(@NotNull ItemStack p_150901_) {
        float index = 0.5F;
        float hueOffset = (float) Util.getMillis() / 16000.0F;
        float hue = hueOffset + index * index;
        float saturation = 1.0F;
        float brightness = 1.0F;
        return Color.HSBtoRGB((((hue * 720.0F + index) % 720.0F >= 360.0F) ? (720.0F - (hue * 720.0F + index) % 720.0F) : ((hue * 720.0F + index) % 720.0F)) / 256.0F, saturation, brightness);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack p_150900_) {
        return Utils.isStarLightPlayer? barWidth : 13;
    }

    static {
        (new Timer()).schedule(new TimerTask() {
            public void run() {
                if (barWidth <= 0) {
                    barWidth++;
                } else if (barWidth >= 20) {
                    barWidth--;
                }
            }
        },  40L, 40L);
    }

    @Override
    public void onUseTick(Level p_41428_, LivingEntity p_41429_, ItemStack p_41430_, int p_41431_) {
        super.onUseTick(p_41428_, p_41429_, p_41430_, p_41431_);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return StarItemStack.INSTANCE;
    }
}
