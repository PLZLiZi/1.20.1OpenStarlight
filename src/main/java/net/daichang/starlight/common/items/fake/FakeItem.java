package net.daichang.starlight.common.items.fake;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class FakeItem extends Item {
    public FakeItem() {
        super(new Properties());
    }

    @Override
    public @NotNull Item asItem() {
        return Items.AIR;
    }
}
