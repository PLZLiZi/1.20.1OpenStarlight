package net.daichang.starlightbyte.mixins.item;

import net.daichang.starlight.server.mc.Items.FuckItem;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Unique
    private final Item starlight$item = (Item) (Object) this;

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    private void itick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_, CallbackInfo ci){
        if (Utils.checkClass(p_41404_)) ci.cancel();
        if (Utils.isKillItem) HelperLib.setClass(starlight$item, FuckItem.class);
    }
}
