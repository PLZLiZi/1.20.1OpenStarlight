package net.daichang.starlight.server.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class PlayerItemClass extends Item {
    public PlayerItemClass(Properties p_41383_) {
        super(p_41383_.food(new FoodProperties.Builder().alwaysEat().meat().build()));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag flag) {
        p_41423_.add(Component.literal("此物品已被 永雏塔菲 封印"));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, flag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
    }
}
