package net.daichang.starlight.server.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.gui.fonts.FuckFont3;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerEntityRender extends MobRenderer<PlayerEntity, EntityModelOfDC<PlayerEntity>> {
    private final ResourceLocation location = new ResourceLocation(StarlightMod.MOD_ID, "textures/entity/skin.png");

    public PlayerEntityRender(EntityRendererProvider.Context context) {
        super(context, new EntityModelOfDC<>(context.bakeLayer(EntityModelOfDC.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PlayerEntity entity) {
        return location;
    }

    @Override
    protected @Nullable RenderType getRenderType(PlayerEntity p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
        return super.getRenderType(p_115322_, p_115323_, p_115324_, p_115325_);
    }

    @Override
    public @NotNull Font getFont() {
        return FuckFont3.getFont();
    }

    @Override
    public boolean shouldRender(PlayerEntity p_115468_, Frustum p_115469_, double p_115470_, double p_115471_, double p_115472_) {
        return super.shouldRender(p_115468_, p_115469_, p_115470_, p_115471_, p_115472_);
    }

    @Override
    protected boolean shouldShowName(PlayerEntity p_115506_) {
        return true;
    }

    @Override
    protected boolean isShaking(PlayerEntity p_115304_) {
        return false;
    }

    @Override
    protected void renderNameTag(PlayerEntity p_114498_, Component p_114499_, PoseStack p_114500_, MultiBufferSource p_114501_, int p_114502_) {
        super.renderNameTag(p_114498_, p_114499_, p_114500_, p_114501_, p_114502_);
    }
}
