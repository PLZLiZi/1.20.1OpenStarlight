package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Unique
    private final ServerLevel starlight$serverLevel = (ServerLevel) (Object) this;

    @Inject(method = "addEntity", at=  @At("RETURN"), cancellable = true)
    private void addEntity(Entity p_8873_, CallbackInfoReturnable<Boolean> cir){
        if(DeathList.isDead(p_8873_)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "addFreshEntity",at = @At("RETURN"), cancellable = true)
    private void addFreshEntity(Entity p_8837_, CallbackInfoReturnable<Boolean> cir){
        if(DeathList.isDead(p_8837_)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at= @At("HEAD"))
    private  void tick(BooleanSupplier p_8794_, CallbackInfo ci){
        if(Utils.isAutoAttack){
            Utils.killLevelEntity(starlight$serverLevel);
        }
    }
}
