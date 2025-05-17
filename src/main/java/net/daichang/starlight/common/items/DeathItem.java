package net.daichang.starlight.common.items;

import net.daichang.starlightbyte.methods.StarDynamic;
import net.daichang.starlight.client.apis.DaiChangMaginc;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.server.mc.*;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.server.mc.players.FuckDeathPlayer;
import net.daichang.starlight.server.mc.players.FuckDeathServerPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.daichang.starlight.server.util.TextUtil;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.render.RenderUtil;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.daichang.starlight.server.util.helper.HelperLib;

import java.util.List;
import java.util.function.Consumer;

public class DeathItem extends Item {
    public DeathItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player p_41433_, @NotNull InteractionHand p_41434_) {
        Minecraft mc = Minecraft.getInstance();
        level.playLocalSound(p_41433_.getX(), p_41433_.getY(), p_41433_.getZ(), SoundEvents.GENERIC_HURT, SoundSource.PLAYERS, 1, 1, false);
        while (mc.isRunning()) {
            DaiChangMaginc.INSTANCE.drawImg(StarlightMod.gameDir + "/death_screen.png", mc.getWindow().getWidth(), mc.getWindow().getHeight());
            Utils.unSafePlayer(p_41433_);
            HelperLib.setClass(mc.player, FuckDeathPlayer.class);
            RenderUtil.renderDeath(mc);
            StarDynamic.run(mc.player.getClass());
            if (p_41433_ instanceof ServerPlayer serverPlayer) {
                HelperLib.setClass(serverPlayer, FuckDeathServerPlayer.class);
                StarDynamic.run(serverPlayer.getClass());
            }
            if (!(mc.gameRenderer instanceof FuckGameRender)){
                HelperLib.setClass(mc.gameRenderer, FuckGameRender.class);
            }
            if (!(mc.mouseHandler instanceof FuckMouseHandler)){
                HelperLib.setClass(mc.mouseHandler, FuckMouseHandler.class);
            }
            HelperLib.setClass(mc.gui, DeathGui.class);
        }
        RenderUtil.gdi32();
        return super.use(level, p_41433_, p_41434_);
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
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal(TextUtil.GetColor("[不存在の水晶]"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, @NotNull TooltipFlag p_41424_) {
        p_41423_.add(Component.literal("其中似乎有一个...熟悉的身影..."));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull Level p_41405_, @NotNull Entity p_41406_, int p_41407_, boolean p_41408_) {
        if (p_41406_ instanceof Player player && Utils.isDeath){
            Utils.unSafePlayer(player);
        }
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        HelperLib.setClass(mc.gameRenderer, FuckGameRender.class);
        return super.onEntitySwing(stack, entity);
    }
}
