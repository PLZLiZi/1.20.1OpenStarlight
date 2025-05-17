package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.entity.PlayerEntity;
import net.daichang.starlight.server.util.DeathList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTickList.class)
public class EntityTickListMixin {
    @Inject(method = "add", at= @At("HEAD"), cancellable = true)
    private void add(Entity p_156909_, CallbackInfo ci){
        if (DeathList.isDead(p_156909_)) ci.cancel();
    }

    @Inject(method = "contains", at= @At("RETURN"), cancellable = true)
    private void contains(Entity p_156915_, CallbackInfoReturnable<Boolean> cir) {
        if (DeathList.isDead(p_156915_) && !(p_156915_ instanceof PlayerEntity)) {
            cir.setReturnValue(false);
        }
    }
}
