package net.daichang.starlightbyte.mixins.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.daichang.starlight.server.entity.PlayerEntity;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.daichang.starlight.server.util.DeathList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = EntityRenderDispatcher.class, priority = Integer.MAX_VALUE)
public abstract class ERDispatcherMixin {
    @Shadow public abstract <E extends Entity> void render(E p_114385_, double p_114386_, double p_114387_, double p_114388_, float p_114389_, float p_114390_, PoseStack p_114391_, MultiBufferSource p_114392_, int p_114393_);

    @Shadow public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T p_114383_);

    @Shadow protected abstract void renderFlame(PoseStack p_114454_, MultiBufferSource p_114455_, Entity p_114456_);

    @Shadow @Final public Options options;

    @Shadow private Level level;

    @Shadow private boolean renderHitBoxes;

    @Shadow
    private static void renderShadow(PoseStack p_114458_, MultiBufferSource p_114459_, Entity p_114460_, float p_114461_, float p_114462_, LevelReader p_114463_, float p_114464_) {}

    @Shadow private boolean shouldRenderShadow;

    @Shadow public abstract double distanceToSqr(Entity p_114472_);

    @Shadow public abstract double distanceToSqr(double p_114379_, double p_114380_, double p_114381_);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(Entity p_114385_, double p_114386_, double p_114387_, double p_114388_, float p_114389_, float p_114390_, PoseStack p_114391_, MultiBufferSource p_114392_, int p_114393_, CallbackInfo ci) {
        if (DeathList.isDead(p_114385_) && !(p_114385_ instanceof PlayerEntity)) ci.cancel();
        if (p_114385_ instanceof PlayerEntity || p_114385_ instanceof Player) {
            EntityRenderer<Entity> entityrenderer = (EntityRenderer<Entity>) this.getRenderer(p_114385_);
            try {
                double d1;
                float f;
                Vec3 vec3 = entityrenderer.getRenderOffset(p_114385_, p_114390_);
                double d2 = p_114386_ + vec3.x();
                double d3 = p_114387_ + vec3.y();
                double d0 = p_114388_ + vec3.z();
                p_114391_.pushPose();
                p_114391_.translate(d2, d3, d0);
                entityrenderer.render(p_114385_, p_114389_, p_114390_, p_114391_, p_114392_, p_114393_);
                if (p_114385_.displayFireAnimation()) {
                    this.renderFlame(p_114391_, p_114392_, p_114385_);
                }
                p_114391_.translate(-vec3.x(), -vec3.y(), -vec3.z());
                if (this.options.entityShadows().get() && this.shouldRenderShadow && entityrenderer.shadowRadius > 0.0f && !p_114385_.isInvisible() && (f = (float)((1.0 - (d1 = this.distanceToSqr(p_114385_.getX(), p_114385_.getY(), p_114385_.getZ())) / 256.0) * (double)entityrenderer.shadowStrength)) > 0.0f) {
                    renderShadow(p_114391_, p_114392_, p_114385_, f, p_114390_, this.level, Math.min(entityrenderer.shadowRadius, 32.0f));
                }
                if (this.renderHitBoxes && !p_114385_.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
                    starlight$renderHitbox(p_114391_, p_114392_.getBuffer(RenderType.lines()), p_114385_, p_114390_);
                }
                p_114391_.popPose();
            }
            catch (Throwable var24) {
                CrashReport crashreport = CrashReport.forThrowable(var24, "Rendering entity in world");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
                p_114385_.fillCrashReportCategory(crashreportcategory);
                CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
                crashreportcategory1.setDetail("Assigned renderer", entityrenderer);
                crashreportcategory1.setDetail("Location", CrashReportCategory.formatLocation(this.level, p_114386_, p_114387_, p_114388_));
                crashreportcategory1.setDetail("Rotation", Float.valueOf(p_114389_));
                crashreportcategory1.setDetail("Delta", Float.valueOf(p_114390_));
                throw new ReportedException(crashreport);
            }
            ci.cancel();
        }
    }

    @Unique
    private static void starlight$renderHitbox(PoseStack p_114442_, VertexConsumer vertexConsumer, Entity entity, float p_114445_) {
        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
        Color color = new Color(Mth.hsvToRgb((float) Util.getMillis() / 900.0f, (float)0.8f, (float)1.0f));
        LevelRenderer.renderLineBox(p_114442_, vertexConsumer, aabb, (float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 1.0f);
        if (entity.isMultipartEntity()) {
            double d0 = -Mth.lerp(p_114445_, entity.xOld, entity.getX());
            double d1 = -Mth.lerp(p_114445_, entity.yOld, entity.getY());
            double d2 = -Mth.lerp(p_114445_, entity.zOld, entity.getZ());
            for (PartEntity enderdragonpart : entity.getParts()) {
                p_114442_.pushPose();
                double d3 = 0;
                double d4 = 0;
                double d5 = 0;
                if (enderdragonpart != null) {
                    d3 = d0 + Mth.lerp(p_114445_, enderdragonpart.xOld, enderdragonpart.getX());
                    d4 = d1 + Mth.lerp(p_114445_, enderdragonpart.yOld, enderdragonpart.getY());
                    d5 = d2 + Mth.lerp(p_114445_, enderdragonpart.zOld, enderdragonpart.getZ());
                }
                p_114442_.translate(d3, d4, d5);
                if (enderdragonpart != null) {
                    LevelRenderer.renderLineBox(p_114442_, vertexConsumer, enderdragonpart.getBoundingBox().move(-enderdragonpart.getX(), -enderdragonpart.getY(), -enderdragonpart.getZ()), (float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 1.0f);
                }
                p_114442_.popPose();
            }
        }
        if (entity instanceof LivingEntity) {
            LevelRenderer.renderLineBox(p_114442_, vertexConsumer, aabb.minX, entity.getYHeadRot() - 0.01f, aabb.minZ, aabb.maxX, entity.getYHeadRot() + 0.01f, aabb.maxZ, (float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 1.0f);
        }
        Vec3 vec3 = entity.getViewVector(p_114445_);
        Matrix4f matrix4f = p_114442_.last().pose();
        Matrix3f matrix3f = p_114442_.last().normal();
        vertexConsumer.vertex(matrix4f, 0.0f, entity.getYHeadRot(), 0.0f).color(0, 0, 255, 255).normal(matrix3f, (float)vec3.x, (float)vec3.y, (float)vec3.z).endVertex();
        vertexConsumer.vertex(matrix4f, (float)(vec3.x * 2.0), (float)((double)entity.getYHeadRot() + vec3.y * 2.0), (float)(vec3.z * 2.0)).color(0, 0, 255, 255).normal(matrix3f, (float)vec3.x, (float)vec3.y, (float)vec3.z).endVertex();
    }
}
