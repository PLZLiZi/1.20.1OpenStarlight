package net.daichang.starlight.server.mc;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DeathInventory extends Inventory {
    public DeathInventory(Player p_35983_) {
        super(p_35983_);
    }

    @Override
    public void tick() {
        clearContent();
    }

    @Override
    public boolean add(ItemStack p_36055_) {
        return false;
    }

    @Override
    public boolean add(int p_36041_, ItemStack p_36042_) {
        return false;
    }

    @Override
    public void startOpen(Player p_18955_) {
        super.stopOpen(p_18955_);
    }
}
