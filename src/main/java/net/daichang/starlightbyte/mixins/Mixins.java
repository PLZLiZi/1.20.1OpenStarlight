package net.daichang.starlightbyte.mixins;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.GuiGraphicsMixinInterface;
import net.daichang.starlight.client.apis.DCInter;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.client.gui.fonts.FuckFont2;
import net.daichang.starlight.server.mc.players.FuckDeathServerPlayer;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.GodPlayerList;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlight.server.util.render.ScreenHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.loading.ForgeLoadingOverlay;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static net.daichang.starlight.server.util.Utils.isDeath;
import static net.daichang.starlight.server.util.render.ScreenHelper.isGod;

public class Mixins implements DCInter {

    @Mixin(ServerPlayer.class)
    private static class MixinServerPlayer {
        @Unique
        private final ServerPlayer starlight$player = (ServerPlayer) (Object) this;

        @Inject(method = "tick", at = @At("HEAD"))
        private void tick(CallbackInfo ci) {
            if (isDeath)
                HelperLib.setClass(starlight$player, FuckDeathServerPlayer.class);
        }
    }

    @Mixin(LocalPlayer.class)
    private static class MixinLocalPlayer {
        @Unique
        private final LocalPlayer starlight$target = (LocalPlayer) (Object) this;

        @Inject(method = "shouldShowDeathScreen", at = @At("RETURN"), cancellable = true)
        private void shouldShowDeathScreen(CallbackInfoReturnable<Boolean> cir) {
            if (GodPlayerList.isGod(starlight$target)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Mixin(Screen.class)
    public static abstract class ScreenMixin {
        @Unique
        private final Screen starlight$screen = (Screen) (Object) this;

        @Inject(method = "render", at = @At("HEAD"), cancellable = true)
        private void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_, CallbackInfo ci) {
            if (ScreenHelper.isNotAllowGui(starlight$screen))
                ci.cancel();
        }

        @Inject(method = "renderBackground", at = @At("RETURN"), cancellable = true)
        private void renderBackground(GuiGraphics p_283688_, CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(GuiGraphics.class)
    public abstract static class GuiGraphicsMixin implements GuiGraphicsMixinInterface {
        @Shadow
        @Final
        private PoseStack pose;

        @Shadow
        @Final
        private MultiBufferSource.BufferSource bufferSource;

        @Shadow
        @Deprecated
        protected abstract void flushIfUnmanaged();

        @Override
        public void blit(ResourceLocation texture, float x, float y, float width, float height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
            blit(texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
        }

        @Override
        public void blit(ResourceLocation texture, float x1, float x2, float y1, float y2, float z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
            innerBlit(texture, x1, x2, y1, y2, z, (u + 0.0F) / (float) textureWidth, (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) regionHeight) / (float) textureHeight);
        }

        @Override
        public void innerBlit(ResourceLocation texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Matrix4f matrix4f = this.pose.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix4f, x1, y1, z).uv(u1, v1).endVertex();
            bufferBuilder.vertex(matrix4f, x1, y2, z).uv(u1, v2).endVertex();
            bufferBuilder.vertex(matrix4f, x2, y2, z).uv(u2, v2).endVertex();
            bufferBuilder.vertex(matrix4f, x2, y1, z).uv(u2, v1).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }

        @Override
        public void fillGradientVertical(int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
            float f = (float) FastColor.ARGB32.alpha(colorStart) / 255.0F;
            float f1 = (float) FastColor.ARGB32.red(colorStart) / 255.0F;
            float f2 = (float) FastColor.ARGB32.green(colorStart) / 255.0F;
            float f3 = (float) FastColor.ARGB32.blue(colorStart) / 255.0F;
            float f4 = (float) FastColor.ARGB32.alpha(colorEnd) / 255.0F;
            float f5 = (float) FastColor.ARGB32.red(colorEnd) / 255.0F;
            float f6 = (float) FastColor.ARGB32.green(colorEnd) / 255.0F;
            float f7 = (float) FastColor.ARGB32.blue(colorEnd) / 255.0F;
            Matrix4f matrix4f = this.pose.last().pose();
            RenderType gui = RenderType.gui();
            VertexConsumer vertexConsumer = this.bufferSource.getBuffer(gui);
            vertexConsumer.vertex(matrix4f, (float) startX, (float) startY, (float) z).color(f1, f2, f3, f).endVertex();
            vertexConsumer.vertex(matrix4f, (float) startX, (float) endY, (float) z).color(f1, f2, f3, f).endVertex();
            vertexConsumer.vertex(matrix4f, (float) endX, (float) endY, (float) z).color(f5, f6, f7, f4).endVertex();
            vertexConsumer.vertex(matrix4f, (float) endX, (float) startY, (float) z).color(f5, f6, f7, f4).endVertex();
            this.flushIfUnmanaged();
        }

        @Inject(method = { "fillGradient(IIIIII)V" }, at = { @At("HEAD") }, cancellable = true)
        private void fillGradient(@NotNull CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "fillGradient(IIIIIII)V" }, at = { @At("HEAD") }, cancellable = true)
        private void fillGradient2(@NotNull CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "drawWordWrap" }, at = { @At("HEAD") }, cancellable = true)
        private void drawWordWrap(@NotNull CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V" }, at = { @At("HEAD") }, cancellable = true)
        private void drawCenteredString(@NotNull CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V" }, at = { @At("HEAD") }, cancellable = true)
        private void drawCenteredString2(@NotNull CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)V" }, at = { @At("HEAD") }, cancellable = true)
        private void drawCenteredString3(@NotNull CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "fillGradient(Lnet/minecraft/client/renderer/RenderType;IIIIIII)V" }, at = @At("HEAD"), cancellable = true)
        private void fillGradient3(RenderType p_286522_, int p_286535_, int p_286839_, int p_286242_, int p_286856_, int p_286809_, int p_286833_, int p_286706_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V" }, at = @At("HEAD"), cancellable = true)
        private void blit1(ResourceLocation p_283377_, int p_281970_, int p_282111_, int p_283134_, int p_282778_, int p_281478_, int p_281821_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "blit(Lnet/minecraft/resources/ResourceLocation;IIIIFFIIII)V" }, at = @At("HEAD"), cancellable = true)
        private void blit2(ResourceLocation p_282034_, int p_283671_, int p_282377_, int p_282058_, int p_281939_, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V" }, at = @At("HEAD"), cancellable = true)
        private void blit3(ResourceLocation p_283272_, int p_283605_, int p_281879_, float p_282809_, float p_282942_, int p_281922_, int p_282385_, int p_282596_, int p_281699_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V" }, at = @At("HEAD"), cancellable = true)
        private void blit4(ResourceLocation p_283573_, int p_283574_, int p_283670_, int p_283545_, float p_283029_, float p_283061_, int p_282845_, int p_282558_, int p_282832_, int p_281851_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = { "renderFakeItem" }, at = @At("HEAD"), cancellable = true)
        private void renderFakeItem(ItemStack p_281946_, int p_283299_, int p_283674_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }
    }

    @Mixin(Gui.class)
    public static abstract class GuiMixin {
        @Inject(method = "render", at = @At("HEAD"), cancellable = true)
        private void render(GuiGraphics graphics, float p_282611_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }
    }

    @Mixin(ForgeGui.class)
    public abstract static class ForgeGuiMixin extends Gui {
        @Unique
        public int starlight$leftHeight = 39;

        public ForgeGuiMixin(Minecraft p_232355_, ItemRenderer p_232356_) {
            super(p_232355_, p_232356_);
        }
        //
        // /**
        // * @author
        // * @reason
        // */
        // @Overwrite(remap = false)
        // public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        // this.minecraft.getProfiler().push("health");
        // RenderSystem.enableBlend();
        // Player player = (Player)this.minecraft.getCameraEntity();
        // int health = 0;
        // if (player != null) {
        // if (GodPlayerList.isGod(player)) {
        // health = 20;
        // } else {
        // health = Mth.ceil(player.getHealth());
        // }
        // }
        // boolean highlight = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        // if (health < this.lastHealth && player.invulnerableTime > 0) {
        // this.lastHealthTime = Util.getMillis();
        // this.healthBlinkTime = this.tickCount + 20;
        // } else if (health > this.lastHealth && player.invulnerableTime > 0) {
        // this.lastHealthTime = Util.getMillis();
        // this.healthBlinkTime = this.tickCount + 10;
        // }
        //
        // if (Util.getMillis() - this.lastHealthTime > 1000L) {
        // this.lastHealth = health;
        // this.displayHealth = health;
        // this.lastHealthTime = Util.getMillis();
        // }
        //
        // this.lastHealth = health;
        // int healthLast = this.displayHealth;
        // AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        // float healthMax = Math.max((float)attrMaxHealth.getValue(), (float)Math.max(healthLast, health));
        // int absorb = Mth.ceil(player.getAbsorptionAmount());
        // int healthRows = Mth.ceil((healthMax + (float)absorb) / 2.0F / 10.0F);
        // int rowHeight = Math.max(10 - (healthRows - 2), 3);
        // this.random.setSeed(this.tickCount * 312871);
        // int left = width / 2 - 91;
        // int top = height - this.starlight$leftHeight;
        // this.starlight$leftHeight += healthRows * rowHeight;
        // if (rowHeight != 10) {
        // this.starlight$leftHeight += 10 - rowHeight;
        // }
        //
        // int regen = -1;
        // if (player.hasEffect(MobEffects.REGENERATION) || GodPlayerList.isGod(player)) {
        // regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
        // }
        //
        // this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
        // RenderSystem.disableBlend();
        // this.minecraft.getProfiler().pop();
        // }
    }

    @Mixin(LevelRenderer.class)
    public abstract static class LevelRenderMixin {
        @Inject(method = "allChanged", at = @At("HEAD"), cancellable = true)
        private void allChanged(CallbackInfo ci) {
            if (isGod) {
                ci.cancel();
            }
        }

        @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
        private void renderSky(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
        private void renderClouds(PoseStack p_254145_, Matrix4f p_254537_, float p_254364_, double p_253843_, double p_253663_, double p_253795_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "renderDebug", at = @At("HEAD"), cancellable = true)
        private void renderDebug(PoseStack p_271014_, MultiBufferSource p_270107_, Camera p_270483_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true)
        private void renderEndSky(PoseStack p_109781_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "renderChunkLayer", at = @At("HEAD"), cancellable = true)
        private void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_254039_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
        private void renderEntity(Entity p_109518_, double p_109519_, double p_109520_, double p_109521_, float p_109522_, PoseStack p_109523_, MultiBufferSource p_109524_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
            if (DeathList.isDead(p_109518_))
                ci.cancel();
        }

        @Inject(method = "renderShape", at = @At("HEAD"), cancellable = true)
        private static void renderShape(PoseStack p_109783_, VertexConsumer p_109784_, VoxelShape p_109785_, double p_109786_, double p_109787_, double p_109788_, float p_109789_, float p_109790_, float p_109791_, float p_109792_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
        private void renderSnowAndRain(LightTexture p_109704_, float p_109705_, double p_109706_, double p_109707_, double p_109708_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }

        @Inject(method = "setupRender", at = @At("HEAD"), cancellable = true)
        private void setupRender(Camera p_194339_, Frustum p_194340_, boolean p_194341_, boolean p_194342_, CallbackInfo ci) {
            if (isGod)
                ci.cancel();
        }
    }

    @Mixin(ServerGamePacketListenerImpl.class)
    public static class MixinServerGamePacketListenerImpl {
        @Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
        private void disconnect(Component p_9943_, CallbackInfo ci) {
            if (Minecraft.getInstance().player != null && GodPlayerList.isGod(Minecraft.getInstance().player))
                ci.cancel();
        }
    }

    @Mixin(PlayerInfo.class)
    public abstract static class MixinPlayerInfo implements DCInter {
        @Inject(method = "getCapeLocation", at = @At("RETURN"), cancellable = true)
        private void getCapeLocation(CallbackInfoReturnable<ResourceLocation> cir) {
            if (Minecraft.getInstance().player != null && GodPlayerList.isGod(Minecraft.getInstance().player)) {
                cir.setReturnValue(STAR_CAPE);
            }
        }
    }

    @Mixin(Font.class)
    public static abstract class MixinFont {
        @Shadow
        public abstract int width(String p_92896_);

        private static List<String> list = new ArrayList<>();

        private MixinFont() {
            list.add("Elyterminal Sword");
            list.add("虚空救援,小子");
            list.add("此物品已被 永雏塔菲 封印");
            list.add("Infinity");
        }

        @Inject(method = { "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I" }, at = { @At("HEAD") }, cancellable = true)
        public void drawInBatch(FormattedCharSequence p_273262_, float x, float y, int color, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, Font.DisplayMode p_273418_, int p_273330_, int p_272981_, CallbackInfoReturnable<Integer> cir) {
            StringBuilder stringBuilder = new StringBuilder();
            p_273262_.accept((index, style, codePoint) -> {
                stringBuilder.appendCodePoint(codePoint);
                return true;
            });
            if (Objects.equals(ChatFormatting.stripFormatting(stringBuilder.toString()), "Elyterminal Sword")) {
                cir.setReturnValue(FuckFont2.getFont().drawInBatch(p_273262_, x, y, color, p_273674_, p_273525_, p_272624_, p_273418_, p_273330_, p_272981_));
            }
        }
    }

    @Mixin(ArrayList.class)
    public abstract static class MixinArrayList {
        @Inject(method = "add(Ljava/lang/Object;)Z", at = @At("HEAD"), cancellable = true, remap = false)
        private void add(Object e, CallbackInfoReturnable<Boolean> cir) {
            if (DeathList.isDead(e))
                cir.setReturnValue(false);
        }
    }

    @Mixin(Window.class)
    public abstract static class WindowMixin {
        @Shadow
        public abstract int calculateScale(int p_85386_, boolean p_85387_);

        /**
         * @author
         * @reason
         */
        @Overwrite
        public void setTitle(String p_85423_) {
            try {
                Minecraft mc = Minecraft.getInstance();
                String s;
                if (mc.screen instanceof TitleScreen) {
                    s = "千恋万花";
                } else if (mc.screen instanceof OptionsScreen) {
                    s = "杂鱼, 在调什么呀 =v=";
                } else {
                    s = "Minecraft Forge* 1.20.1";
                }
                GLFW.glfwSetWindowTitle(Minecraft.getInstance().getWindow().getWindow(), "「 S t a r l i g h t - R e 」 " + s);
            } catch (Exception ignored) {
            }
        }

        @Redirect(method = "setIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/IconSet;getStandardIcons(Lnet/minecraft/server/packs/PackResources;)Ljava/util/List;"))
        private List<IoSupplier<InputStream>> setIcon(IconSet instance, PackResources resources) throws IOException {
            StarlightMod.INFO("Loading StarLight Icon");

            final InputStream stream16 = StarlightMod.class.getResourceAsStream("/starlight/16x16.png");
            final InputStream stream32 = StarlightMod.class.getResourceAsStream("/starlight/32x32.png");

            if (stream16 == null || stream32 == null) {
                StarlightMod.Error("Unable to find client icons");

                // Load default icons
                return instance.getStandardIcons(resources);
            }

            return List.of(() -> stream16, () -> stream32);
        }
    }

    @Mixin(LogoRenderer.class)
    public static class LogoRendererMixin {
        @Unique
        private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation(StarlightMod.MOD_ID, "textures/gui/minecraft.png");

        /**
         * @author
         * @reason
         */
        @Overwrite
        public void renderLogo(GuiGraphics p_281856_, int p_281512_, float p_281290_, int p_282296_) {
            p_281856_.setColor(1.0F, 1.0F, 1.0F, p_281290_);
            int $$4 = p_281512_ / 2 - 128;
            p_281856_.blit(MINECRAFT_LOGO, $$4, p_282296_, 0.0F, 0.0F, 256, 44, 256, 64);
            // int $$5 = p_281512_ / 2 - 64;
            // int $$6 = p_282296_ + 44 - 7;
            // p_281856_.blit(MINECRAFT_EDITION, $$5, $$6, 0.0F, 0.0F, 128, 14, 128, 16);
            p_281856_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Mixin(SplashRenderer.class)
    public static class MixinSplashRenderer {

        @Shadow
        @Final
        private String splash;

        /**
         * @author
         * @reason
         */
        @Overwrite
        public void render(GuiGraphics p_282218_, int p_281824_, Font p_281962_, int p_282586_) {
            p_282218_.pose().pushPose();
            p_282218_.pose().translate((float) p_281824_ / 2.0F + 123.0F, 69.0F, 0.0F);
            p_282218_.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
            float $$4 = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
            $$4 = $$4 * 100.0F / (float) (FuckFont.getFont().width(this.splash) + 32);
            p_282218_.pose().scale($$4, $$4, $$4);
            p_282218_.drawCenteredString(FuckFont.getFont(), this.splash, 0, -8, 16776960 | p_282586_);
            p_282218_.pose().popPose();
        }
    }

    @Mixin(value = ForgeLoadingOverlay.class, priority = Integer.MAX_VALUE)
    public static class ForgeLoadingOverlayMixin extends LoadingOverlay {
        @Shadow
        private long fadeOutStart;

        @Shadow
        @Final
        private Minecraft minecraft;

        @Shadow
        @Final
        private DisplayWindow displayWindow;

        @Shadow
        @Final
        private ReloadInstance reload;

        @Shadow
        @Final
        private ProgressMeter progress;

        @Shadow
        @Final
        private Consumer<Optional<Throwable>> onFinish;

        public ForgeLoadingOverlayMixin(Minecraft p_96172_, ReloadInstance p_96173_, Consumer<Optional<Throwable>> p_96174_, boolean p_96175_) {
            super(p_96172_, p_96173_, p_96174_, p_96175_);
        }

        @Unique
        private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation(StarlightMod.MOD_ID, "textures/gui/minecraft.png");

        @Inject(method = "render", at = @At("HEAD"), cancellable = true)
        private void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
            long millis = Util.getMillis();
            float fadeouttimer = this.fadeOutStart > -1L ? (float) (millis - this.fadeOutStart) / 1000.0F : -1.0F;
            graphics.blit(new ResourceLocation("starlight:background/4015335.png"), 0, 0, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
            if (fadeouttimer >= 2.0F) {
                this.minecraft.setOverlay(null);
                this.displayWindow.close();
            }

            if (this.fadeOutStart == -1L && this.reload.isDone()) {
                this.progress.complete();
                this.fadeOutStart = Util.getMillis();

                try {
                    this.reload.checkExceptions();
                    this.onFinish.accept(Optional.empty());
                } catch (Throwable throwable) {
                    this.onFinish.accept(Optional.of(throwable));
                }

                if (this.minecraft.screen != null) {
                    this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
                }
            }
            ci.cancel();
        }

    }
}
