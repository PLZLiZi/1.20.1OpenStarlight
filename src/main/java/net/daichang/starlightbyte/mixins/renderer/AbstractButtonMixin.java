package net.daichang.starlightbyte.mixins.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.daichang.starlight.server.util.render.Render2DUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget {
    @Shadow public abstract void renderString(GuiGraphics p_283366_, Font p_283054_, int p_281656_);

    @Shadow protected abstract int getTextureY();

    public AbstractButtonMixin(int p_93629_, int p_93630_, int p_93631_, int p_93632_, Component p_93633_) {
        super(p_93629_, p_93630_, p_93631_, p_93632_, p_93633_);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_, CallbackInfo ci) {
        PoseStack poseStack = p_281670_.pose();
        Color startColor = new Color(100, 100, 100);
        Render2DUtil.drawRoundGradient(poseStack, getX(), getY(), getWidth(), getHeight(), 5, startColor.getRGB(), startColor.getRGB());
        Minecraft minecraft = Minecraft.getInstance();
        p_281670_.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
//        p_281670_.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
//        p_281670_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.getFGColor();
        this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        ci.cancel();
    }

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    protected void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
//        PoseStack poseStack = p_281670_.pose();
//        Color startColor = new Color(100, 100, 100);
//        Render2DUtil.drawRoundGradient(poseStack, getX(), getY(), getWidth(), getHeight(), 5, startColor.getRGB(), startColor.getRGB());
//        Minecraft minecraft = Minecraft.getInstance();
//        p_281670_.setColor(1.0F, 1.0F, 1.0F, this.alpha);
//        RenderSystem.enableBlend();
//        RenderSystem.enableDepthTest();
////        p_281670_.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
////        p_281670_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
//        int i = this.getFGColor();
//        this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
//    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
