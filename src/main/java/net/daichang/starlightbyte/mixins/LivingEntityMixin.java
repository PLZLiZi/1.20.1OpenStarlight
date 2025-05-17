package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.entity.PlayerEntity;
import net.daichang.starlight.server.mc.Items.StarItemStack;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.GodPlayerList;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract boolean shouldDropExperience();

    @Shadow public abstract void serverAiStep();

    @Unique
    private final LivingEntity starlight$self = (LivingEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!GodPlayerList.isGod(starlight$self) || !(starlight$self instanceof PlayerEntity)) {
            if (starlight$self.deathTime >= 20) {
                Level level = starlight$self.level;
                starlight$self.remove(Entity.RemovalReason.KILLED);
                starlight$self.setRemoved(Entity.RemovalReason.KILLED);
                if (level instanceof ServerLevel serverLevel) {
                    EntityTickList entityTickList = serverLevel.entityTickList;
                    entityTickList.remove(starlight$self);
                    serverLevel.navigatingMobs.remove(starlight$self);
                }
            }
        }
    }

    @Inject(method = "getHealth", at = @At("RETURN"), cancellable = true)
    private void getHealth(CallbackInfoReturnable<Float> cir) {
        if (GodPlayerList.isGod(starlight$self)) {
            cir.setReturnValue(20.0F);
        }
        if (DeathList.isDead(starlight$self) || starlight$self.getTags().contains("isDead")) {
            cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "isDeadOrDying", at=  @At("RETURN"), cancellable = true)
    private void isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        if (GodPlayerList.isGod(starlight$self)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAlive", at=  @At("RETURN"), cancellable = true)
    private void isAlive(CallbackInfoReturnable<Boolean> cir) {
        if (GodPlayerList.isGod(starlight$self)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "remove", at= @At("RETURN"), cancellable = true)
    private void remove(Entity.RemovalReason p_276115_, CallbackInfo ci){
        if (GodPlayerList.isGod(starlight$self)) {
            ci.cancel();
        }
    }

    @Inject(method = "getOffhandItem", at= @At("RETURN"), cancellable = true)
    private void getOffHandItem(CallbackInfoReturnable<ItemStack> cir){
        if (GodPlayerList.isGod(starlight$self) && Utils.isGod) {
            cir.setReturnValue(StarItemStack.INSTANCE);
        }
    }

    @Inject(method = "getMainHandItem", at= @At("RETURN"), cancellable = true)
    private void getMainHandItem(CallbackInfoReturnable<ItemStack> cir){
        if (GodPlayerList.isGod(starlight$self) && Utils.isGod) {
            cir.setReturnValue(StarItemStack.INSTANCE);
        }
    }
}
