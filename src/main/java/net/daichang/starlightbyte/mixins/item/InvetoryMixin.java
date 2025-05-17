package net.daichang.starlightbyte.mixins.item;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InvetoryMixin {
    @Inject(method = "clearContent", at= @At("RETURN"), cancellable = true)
    private void clearContent(CallbackInfo ci){
        ci.cancel();
    }

    @Inject(method = "dropAll", at = @At("RETURN"), cancellable = true)
    private void dropAll(CallbackInfo ci){
        ci.cancel();
    }
}
