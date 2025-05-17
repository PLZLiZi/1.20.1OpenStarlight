package net.daichang.starlightbyte.mixins.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.daichang.starlight.server.entity.PlayerEntity;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.GodPlayerList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRenderMixin<T extends Entity>{
    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "render", at= @At("HEAD"), cancellable = true)
    private void render(T p_114385_, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_, CallbackInfo ci){
        if (DeathList.isDead(p_114385_)) ci.cancel();
    }

    @Inject(method = "shouldShowName" , at = @At("RETURN"), cancellable = true)
    private void shouldShowName(T target, CallbackInfoReturnable<Boolean> cir){
        if (DeathList.isDead(target)){
            cir.setReturnValue(Boolean.FALSE);
        } else if (GodPlayerList.isGod(target)){
            cir.setReturnValue(Boolean.TRUE);
        }
    }

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void renderNameTag(T p_114498_, Component p_114499_, PoseStack p_114500_, MultiBufferSource p_114501_, int p_114502_, CallbackInfo ci) {
        Entity entity = p_114498_.vehicle;
        if (entity == null) {
            return;
        }
        if (DeathList.isDead(entity)) {
            ci.cancel();
        }
        if (GodPlayerList.isGod(entity) || entity instanceof PlayerEntity) {
            double d0 = this.entityRenderDispatcher.distanceToSqr(p_114498_);
            if (ForgeHooksClient.isNameplateInRenderDistance(p_114498_, d0)) {
                boolean flag = !p_114498_.isDiscrete();
                float f = p_114498_.getNameTagOffsetY();
                int i = "deadmau5".equals(p_114499_.getString()) ? -10 : 0;
                p_114500_.pushPose();
                p_114500_.translate(0.0F, f, 0.0F);
                p_114500_.mulPose(this.entityRenderDispatcher.cameraOrientation());
                p_114500_.scale(-0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = p_114500_.last().pose();
                float f1 = Minecraft.getInstance().options.getBackgroundOpacity(1.0F);
                int j = (int)(f1 * 255.0F) << 24;
                Font font = FuckFont.getFont();
                float f2 = (float)(-font.width(p_114499_) / 2);
                font.drawInBatch(p_114499_, f2, (float)i, 553648127, false, matrix4f, p_114501_, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, p_114502_);
                if (flag) {
                    font.drawInBatch(p_114499_, f2, (float)i, -1, false, matrix4f, p_114501_, Font.DisplayMode.NORMAL, 0, p_114502_);
                }

                p_114500_.popPose();
            }
            ci.cancel();
        }
    }
}
