package net.daichang.starlight.common.items;

import net.daichang.starlight.client.gui.fonts.FuckFont2;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IForgeSpawnEgg extends ForgeSpawnEggItem implements IForgeItem {
    public IForgeSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type) {
        super(type, 0, 0, new Properties().stacksTo(5));
    }

    @Override
    public int getBarColor(@NotNull ItemStack p_150901_) {
        return new Random().nextInt();
    }

    @Override
    public int getBarWidth(@NotNull ItemStack p_150900_) {
        return 13;
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

    @Override
    public InteractionResult useOn(UseOnContext p_43223_) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        localPlayer.displayClientMessage(Component.literal(ChatFormatting.YELLOW + "永雏塔菲 加入了游戏"), false);
        return super.useOn(p_43223_);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal("解封 永雏塔菲");
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, @NotNull TooltipFlag p_41424_) {
        p_41423_.add(Component.literal("世界,倒影水中"));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }
}
