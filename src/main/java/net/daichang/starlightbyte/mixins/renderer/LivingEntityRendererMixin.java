package net.daichang.starlightbyte.mixins.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.GodPlayerList;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = Integer.MAX_VALUE)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void render(LivingEntity p_115308_, float p_115309_, float p_115310_, PoseStack p_115311_, MultiBufferSource p_115312_, int p_115313_, CallbackInfo ci) {
        if (DeathList.isDead(p_115308_) && Utils.checkClass(p_115308_)) ci.cancel();
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void render(Entity entity, float par2, float par3, PoseStack par4, MultiBufferSource par5, int par6, CallbackInfo ci) {
        if (DeathList.isDead(entity) && Utils.checkClass(entity)) ci.cancel();
    }

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    private void shouldShowName1(Entity par1, CallbackInfoReturnable<Boolean> cir) {
        if (DeathList.isDead(par1)) {
            cir.setReturnValue(Boolean.FALSE);
        } else if (GodPlayerList.isGod(par1)) {
            cir.setReturnValue(Boolean.TRUE);
        }
    }
}