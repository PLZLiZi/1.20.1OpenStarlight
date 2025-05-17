package net.daichang.starlight.server.mc.Items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FuckItemStack extends ItemStack {
    public FuckItemStack(ItemLike p_41599_) {
        super(p_41599_);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level p_41683_, @NotNull Player p_41684_, @NotNull InteractionHand p_41685_) {
        return super.use(p_41683_, p_41684_, p_41685_);
    }
}
