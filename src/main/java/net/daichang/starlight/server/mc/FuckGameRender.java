package net.daichang.starlight.server.mc;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.server.util.TextUtil;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.render.Render2DUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Random;

import java.util.Locale;

public class FuckGameRender extends GameRenderer {
    public FuckGameRender(Minecraft p_234219_, ItemInHandRenderer p_234220_, ResourceManager p_234221_, RenderBuffers p_234222_) {
        super(p_234219_, p_234220_, p_234221_, p_234222_);
    }

    @Override
    public @NotNull Camera getMainCamera() {
        return super.getMainCamera();
    }

    @Override
    public @Nullable ShaderInstance getShader(@Nullable String p_172735_) {
        return super.getShader(p_172735_);
    }

    @Override
    public void render(float p_109094_, long p_109095_, boolean p_109096_) {
        //这里用的唯爱的render
        if (this.minecraft.isWindowActive() || !this.minecraft.options.pauseOnLostFocus || ((Boolean)this.minecraft.options.touchscreen().get()).booleanValue() && this.minecraft.mouseHandler.isRightPressed()) {
            this.lastActiveTime = Util.getMillis();
        } else if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
        }
        if (!this.minecraft.noRender) {
            int i = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
            int j = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
            RenderSystem.viewport((int)0, (int)0, (int)this.minecraft.getWindow().getWidth(), (int)this.minecraft.getWindow().getHeight());
            if (p_109096_ && this.minecraft.level != null) {
                this.minecraft.getProfiler().push("level");
                this.renderLevel(p_109094_, p_109095_, new PoseStack());
                this.minecraft.levelRenderer.doEntityOutline();
                if (this.postEffect != null && this.effectActive) {
                    RenderSystem.disableBlend();
                    RenderSystem.disableDepthTest();
                    RenderSystem.resetTextureMatrix();
                    this.postEffect.process(p_109094_);
                }
                this.minecraft.getMainRenderTarget().bindWrite(true);
            }
            Window window = this.minecraft.getWindow();
            RenderSystem.clear((int)256, (boolean)Minecraft.ON_OSX);
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float)((double)window.getWidth() / window.getGuiScale()), (float)((double)window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, ForgeHooksClient.getGuiFarPlane());
            RenderSystem.setProjectionMatrix((Matrix4f)matrix4f, (VertexSorting)VertexSorting.ORTHOGRAPHIC_Z);
            PoseStack posestack = RenderSystem.getModelViewStack();
            Render2DUtil.drawDeathScreen(posestack, window.getWidth(), window.getHeight());
            posestack.pushPose();
            posestack.setIdentity();
            posestack.translate(0.0, 0.0, (double)(1000.0f - ForgeHooksClient.getGuiFarPlane()));
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
            GuiGraphics guigraphics = new GuiGraphics(this.getMinecraft(), this.renderBuffers.bufferSource());
            this.getMinecraft().screen = null;
            int cX = (int)(this.getMinecraft().mouseHandler.xpos() * (double)this.getMinecraft().getWindow().getGuiScaledWidth() / (double)this.getMinecraft().getWindow().getScreenWidth());
            int cY = (int)(this.getMinecraft().mouseHandler.ypos() * (double)this.getMinecraft().getWindow().getGuiScaledHeight() / (double)this.getMinecraft().getWindow().getScreenHeight());
            Random random1 = new Random(StarlightMod.milliTime());
            guigraphics.fillGradient(RenderType.gui(), 0, 0, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), random1.nextInt(1), random1.nextInt(1), 0);
            guigraphics.pose().pushPose();
            guigraphics.pose().scale(2.0f, 2.0f, 2.0f);
            guigraphics.drawCenteredString((Font) FuckFont.getFont(), TextUtil.GetColor((String)"你死了"), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 / 2, 30, 0xFFFFFF);
            guigraphics.pose().popPose();
            int x = this.getMinecraft().getWindow().getGuiScaledWidth() / 2 - 100;
            int y = this.getMinecraft().getWindow().getGuiScaledHeight() / 4 + 72;
            boolean isHovered = cX >= x && cY >= y && cX < x + 200 && cY < y + 20;
            guigraphics.setColor(random1.nextFloat(), random1.nextFloat(), random1.nextFloat(), random1.nextFloat());
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            guigraphics.blitNineSliced(AbstractWidget.WIDGETS_LOCATION, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 72, 200, 20, 20, 4, 200, 20, 0, 46 + (isHovered ? 2 : 1) * 20);
            guigraphics.setColor(random1.nextFloat(), random1.nextFloat(), random1.nextFloat(), random1.nextFloat());
            int i1 = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100 + 2;
            int j1 = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100 + 200 - 2;
            Utils.renderScrollingString((GuiGraphics)guigraphics, (Font)FuckFont.getFont(), (Component)(this.getMinecraft().level.getLevelData().isHardcore() ? Component.translatable((String)"deathScreen.spectate") : Component.translatable((String)"deathScreen.respawn")), (int)i1, (int)(Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 72), (int)j1, (int)(Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 72 + 20), (int)(0xFFFFFF | Mth.ceil((float)255.0f) << 24));
            x = this.getMinecraft().getWindow().getGuiScaledWidth() / 2 - 100;
            y = this.getMinecraft().getWindow().getGuiScaledHeight() / 4 + 96;
            isHovered = cX >= x && cY >= y && cX < x + 200 && cY < y + 20;
            guigraphics.setColor(random1.nextFloat(), random1.nextFloat(), random1.nextFloat(), random1.nextFloat());
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            guigraphics.blitNineSliced(AbstractWidget.WIDGETS_LOCATION, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 96, 200, 20, 20, 4, 200, 20, 0, 46 + (isHovered ? 2 : 1) * 20);
            guigraphics.setColor(random1.nextFloat(), random1.nextFloat(), random1.nextFloat(), random1.nextFloat());
            i1 = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100 + 2;
            j1 = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100 + 200 - 2;
            Utils.renderScrollingString((GuiGraphics)guigraphics, (Font)FuckFont.getFont(), (Component)Component.translatable((String)"deathScreen.titleScreen"), (int)i1, (int)(Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 96), (int)j1, (int)(Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 96 + 20), (int)(0xFFFFFF | Mth.ceil((float)255.0f) << 24));
            if (this.getMinecraft().getReportingContext().hasDraftReport()) {
                guigraphics.blit(AbstractWidget.WIDGETS_LOCATION, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 100 + 200 - 17, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4 + 96 + 3, 182, 24, 15, 15);
            }
            this.getMinecraft().mouseHandler.mouseGrabbed = false;
            if (p_109096_ && this.minecraft.level != null) {
                this.minecraft.getProfiler().popPush("gui");
                if (this.minecraft.player != null) {
                    float f = Mth.lerp((float)p_109094_, (float) this.getMinecraft().player.oSpinningEffectIntensity, (float) this.getMinecraft().player.spinningEffectIntensity);
                    float f1 = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
                    if (f > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONFUSION) && f1 < 1.0f) {
                        this.renderConfusionOverlay(guigraphics, f * (1.0f - f1));
                    }
                }
                if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
                    this.renderItemActivationAnimation(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), p_109094_);
                    this.minecraft.gui.render(guigraphics, p_109094_);
                    RenderSystem.clear((int)256, (boolean)Minecraft.ON_OSX);
                }
                this.minecraft.getProfiler().pop();
            }
            if (this.minecraft.getOverlay() != null) {
                try {
                    this.minecraft.getOverlay().render(guigraphics, i, j, this.minecraft.getDeltaFrameTime());
                }
                catch (Throwable var16) {
                    CrashReport crashreport2 = CrashReport.forThrowable((Throwable)var16, (String)"Rendering overlay");
                    CrashReportCategory crashreportcategory2 = crashreport2.addCategory("Overlay render details");
                    crashreportcategory2.setDetail("Overlay name", () -> this.minecraft.getOverlay().getClass().getCanonicalName());
                    throw new ReportedException(crashreport2);
                }
            }
            if (this.minecraft.screen != null) {
                try {
                    ForgeHooksClient.drawScreen(this.getMinecraft().screen, guigraphics, (int)i, (int)j, (float)this.minecraft.getDeltaFrameTime());
                }
                catch (Throwable var15) {
                    CrashReport crashreport2 = CrashReport.forThrowable((Throwable)var15, (String)"Rendering screen");
                    CrashReportCategory crashreportcategory2 = crashreport2.addCategory("Screen render details");
                    crashreportcategory2.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                    crashreportcategory2.setDetail("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos()));
                    crashreportcategory2.setDetail("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getWindow().getGuiScale()));
                    throw new ReportedException(crashreport2);
                }
                try {
                    if (this.getMinecraft().screen != null) {
                        this.getMinecraft().screen.handleDelayedNarration();
                    }
                }
                catch (Throwable var14) {
                    CrashReport crashreport2 = CrashReport.forThrowable((Throwable)var14, (String)"Narrating screen");
                    CrashReportCategory crashreportcategory2 = crashreport2.addCategory("Screen details");
                    crashreportcategory2.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                    throw new ReportedException(crashreport2);
                }
            }
            this.getMinecraft().getProfiler().push("toasts");
            this.getMinecraft().getToasts().render(guigraphics);
            this.getMinecraft().getProfiler().pop();
            guigraphics.flush();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }
}
