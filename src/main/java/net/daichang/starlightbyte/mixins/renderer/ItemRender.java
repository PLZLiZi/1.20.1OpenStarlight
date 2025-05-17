package net.daichang.starlightbyte.mixins.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.daichang.starlight.common.register.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class ItemRender {
    @Mixin(value = ItemRenderer.class, priority = 0x7fffffff)
    public abstract static class ItemRendererMixin {
        @Unique
        private final ResourceLocation STAR_GLINT = new ResourceLocation("starlight:textures/item/glint.png");

        @Shadow
        @Final
        private Minecraft minecraft;

        @Shadow
        public abstract void renderQuadList(PoseStack p_115163_, VertexConsumer p_115164_, List<BakedQuad> p_115165_, ItemStack p_115166_, int p_115167_, int p_115168_);

        @Shadow
        public abstract void renderModelLists(BakedModel p_115190_, ItemStack p_115191_, int p_115192_, int p_115193_, PoseStack p_115194_, VertexConsumer p_115195_);

        @Unique
        public ItemDisplayContext starlight$context;

        @Inject(method = "render", at = @At("HEAD"))
        private void render(ItemStack p_115144_, ItemDisplayContext p_270188_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_, CallbackInfo ci) {
            starlight$context = p_270188_;
        }

        @Inject(method = "renderModelLists", at = @At("HEAD"), cancellable = true)
        private void renderModelLists(BakedModel p_115190_, @NotNull ItemStack p_115191_, int p_115192_, int p_115193_, PoseStack p_115194_, VertexConsumer p_115195_, CallbackInfo ci) {
            RandomSource randomsource = RandomSource.create();
            long i = 42L;
            if (p_115191_.getItem() == ItemRegister.STARLIGHT_ITEM.get()) {
                if (starlight$context == ItemDisplayContext.GUI) {
                    for (Direction direction : Direction.values()) {
                        randomsource.setSeed(i);
                        // Start Modify
                        this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.endPortal()), p_115190_.getQuads(null, direction, randomsource), p_115191_, p_115192_, p_115193_);
                        // End Modify
                    }
                    this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.text(new ResourceLocation("minecraft", "textures/item/nether_star.png"))), p_115190_.getQuads(null, null, randomsource), p_115191_, p_115192_, p_115193_);
                    randomsource.setSeed(i);
                    // Start Modify
                    this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.endPortal()), p_115190_.getQuads(null, null, randomsource), p_115191_, p_115192_, p_115193_);
                    // End Modify
                }
//                else {
//                    for (Direction direction : Direction.values()) {
//                        randomsource.setSeed(i);
//                        // Start Modify
//                        this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.entityShadow(STAR_GLINT)), p_115190_.getQuads(null, direction, randomsource), p_115191_, p_115192_, p_115193_);
//                        // End Modify
//                    }
//                    randomsource.setSeed(i);
//                    // Start Modify
//                    this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.entityShadow(STAR_GLINT)), p_115190_.getQuads(null, null, randomsource), p_115191_, p_115192_, p_115193_);
//                    // End Modify
//                }
            }
            if (p_115191_.getItem() == ItemRegister.ENTITY_GETTER.get()){
                for (Direction direction : Direction.values()) {
                    randomsource.setSeed(i);
                    // Start Modify
                    this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.endPortal()), p_115190_.getQuads(null, direction, randomsource), p_115191_, p_115192_, p_115193_);
                    // End Modify
                }
                this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.endPortal()), p_115190_.getQuads(null, null, randomsource), p_115191_, p_115192_, p_115193_);
                // End Modify
                ci.cancel();
            }
            if (starlight$context == ItemDisplayContext.GUI && p_115191_.getItem() == ItemRegister.Item_LVING_ENTITY_DEBUG.get()){
                for (Direction direction : Direction.values()) {
                    randomsource.setSeed(i);
                    // Start Modify
                    this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.endPortal()), p_115190_.getQuads(null, direction, randomsource), p_115191_, p_115192_, p_115193_);
                    // End Modify
                }
                this.renderQuadList(p_115194_, this.minecraft.renderBuffers().bufferSource().getBuffer(RenderType.endPortal()), p_115190_.getQuads(null, null, randomsource), p_115191_, p_115192_, p_115193_);
            }
        }
    }
}
