package net.daichang.starlight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class StarLightItemRender extends BlockEntityWithoutLevelRenderer {
    public StarLightItemRender(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public void renderByItem(ItemStack p_108830_, ItemDisplayContext p_270899_, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_108834_, int p_108835_) {
        super.renderByItem(p_108830_, p_270899_, poseStack, multiBufferSource, p_108834_, p_108835_);
    }
}
