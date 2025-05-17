package net.daichang.starlight.common.items;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.gui.fonts.FuckFont2;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlight.server.util.render.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EntityDebugStick extends Item {
    public EntityDebugStick() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity living){
            if (player.isShiftKeyDown()) {
                Utils.killEntityEx(living);
            } else {
                Utils.Override_DATA_HEALTH_ID(living, 0.0F);
                StarlightMod.INFO(living.getStringUUID() + " 血量已被清零");
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public boolean hurtEnemy(ItemStack p_41395_, LivingEntity p_41396_, LivingEntity player) {
        if (player.isShiftKeyDown()){
            Utils.killEntityEx(p_41396_);
        }else {
            Utils.Override_DATA_HEALTH_ID(p_41396_, 0.0F);
            StarlightMod.INFO(p_41396_.getStringUUID() + " 血量已被清零");
        }
        return super.hurtEnemy(p_41395_, p_41396_, player);
    }


    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal("Entity Debug");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player p_41433_, @NotNull InteractionHand p_41434_) {
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.dragonParts.clear();
            Iterables.unmodifiableIterable(serverLevel.getAllEntities()).forEach(target -> {
                if (!(target instanceof Player)) {
                    Utils.Override_DATA_HEALTH_ID(target, 0.0F);
                }
            });
        }else if (world instanceof ClientLevel clientLevel) {
            clientLevel.partEntities.clear();
            clientLevel.entitiesForRendering().forEach(target -> {
                if (!(target instanceof Player)) {
                    Utils.Override_DATA_HEALTH_ID(target, 0.0F);
                }
            });
        }
        return super.use(world, p_41433_, p_41434_);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, List<Component> list, @NotNull TooltipFlag p_41424_) {
        list.add(Component.literal("右键将全图实体血量设置为0"));
        list.add(Component.literal("左击到目标实体后,将目标实体血量设置为0"));
        list.add(Component.literal("按下shift后攻击为 高级击杀 模式"));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull Font getFont(ItemStack stack, FontContext context) {
                return FuckFont2.getFont();
            }
        });
        super.initializeClient(consumer);
    }
}
