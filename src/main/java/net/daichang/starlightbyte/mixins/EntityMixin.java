package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.entity.PlayerEntity;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.GodPlayerList;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private final Entity starlight$entity = (Entity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci){
        if (DeathList.isDead(starlight$entity) && !(starlight$entity instanceof Player)) {
            Utils.killEntity(starlight$entity);
        }
    }

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void kill(CallbackInfo ci){
        if (GodPlayerList.isGod(starlight$entity) && starlight$entity instanceof Player) {
            ci.cancel();
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void remove(Entity.RemovalReason p_146834_, CallbackInfo ci){
        if (GodPlayerList.isGod(starlight$entity) && starlight$entity instanceof Player || starlight$entity instanceof PlayerEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at= @At("HEAD"), cancellable = true)
    private void setRemoved(Entity.RemovalReason p_146876_, CallbackInfo ci){
        if (GodPlayerList.isGod(starlight$entity)|| starlight$entity instanceof PlayerEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "onClientRemoval", at =@At("HEAD"), cancellable = true)
    private void onClientRemoval(CallbackInfo ci){
        if (GodPlayerList.isGod(starlight$entity)|| starlight$entity instanceof PlayerEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "getRemovalReason", at= @At("RETURN"), cancellable = true)
    private void getRemovalReason(CallbackInfoReturnable<Entity.RemovalReason> cir){
        if (GodPlayerList.isGod(starlight$entity)|| starlight$entity instanceof PlayerEntity) {
            cir.setReturnValue(null);
        }
        if (DeathList.isDead(starlight$entity) && !(starlight$entity instanceof Player)) {
            cir.setReturnValue(Entity.RemovalReason.KILLED);
        }
    }
}
