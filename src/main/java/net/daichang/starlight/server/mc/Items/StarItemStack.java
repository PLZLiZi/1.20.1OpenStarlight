package net.daichang.starlight.server.mc.Items;

import net.daichang.starlight.common.register.ItemRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StarItemStack extends ItemStack {
    public static final StarItemStack INSTANCE = new StarItemStack();

    public StarItemStack() {
        super(ItemRegister.STARLIGHT_ITEM.get());
    }

    public Item item() {
        return ItemRegister.STARLIGHT_ITEM.get();
    }

    @Override
    public int getCount() {
        return new Random().nextInt(100, 200);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ItemRegister.STARLIGHT_ITEM.get().getName(this);
    }

    public boolean isEmpty() {
        return false;
    }

    public @NotNull ItemStack copy() {
        return INSTANCE;
    }

    @Override
    public @NotNull ItemStack copyWithCount(int p_256354_) {
        return super.copyWithCount(1);
    }

    @Override
    public int getUseDuration() {
        return 72000;
    }

    @Override
    public @NotNull ItemStack copyAndClear() {
        return INSTANCE;
    }

    @Override
    public boolean onDroppedByPlayer(Player player) {
        return false;
    }

    @Override
    public boolean canDisableShield(ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    @Override
    public void setCount(int p_41765_) {
        super.setCount(new Random().nextInt(100, 200));
    }
}