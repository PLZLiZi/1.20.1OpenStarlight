package net.daichang.starlight.server.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.server.mc.players.FuckDeathPlayer;
import net.daichang.starlight.server.util.fonts.FontManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

public class RenderUtil {
    public static ResourceLocation customTexture = null;

    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final User32 user32 = User32.INSTANCE;

    private static final GDI32 gdi32 = GDI32.INSTANCE;

    public static int width = 0;

    public static int height = 0;

    public static FuckFont font;

    public RenderUtil(){
        font = FuckFont.getFont();
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void renderDeath(Minecraft mc){
        try {
            try {
                try {
                    if (mc.getWindow().shouldClose()) mc.stop();
                    mc.resizeDisplay();
                    mc.getWindow().updateDisplay();
                    Thread.yield();
                } catch (Exception ignored) {
                }
                try {
                    LocalPlayer old = Objects.requireNonNull(mc.player);
                    mc.player = new FuckDeathPlayer(old.minecraft, old.clientLevel, old.connection, old.getStats(), old.getRecipeBook(), false, false) {
                        @Override
                        public float getHealth() {
                            return super.getHealth();
                        }

                        @Override
                        public @NotNull FoodData getFoodData() {
                            return new FoodData() {
                                @Override
                                public int getFoodLevel() {
                                    return 0;
                                }

                                @Override
                                public float getExhaustionLevel() {
                                    return super.getExhaustionLevel();
                                }
                            };
                        }
                    };
                    mc.cameraEntity = mc.player;

                    //RenderSystem.bindTexture(texture.getId());

                    int width = mc.getWindow().getGuiScaledWidth();
                    int height = mc.getWindow().getGuiScaledHeight();

                    float random = new Random().nextFloat() * 0.5F + 0.5F;
                    Window window = mc.getWindow();
                    RenderSystem.clear(256, Minecraft.ON_OSX);
                    Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0F, 0.0F, ForgeHooksClient.getGuiFarPlane());
                    RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
                    PoseStack posestack = RenderSystem.getModelViewStack();
                    posestack.pushPose();
                    posestack.setIdentity();
                    RenderSystem.applyModelViewMatrix();
                    Lighting.setupFor3DItems();

                    GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers.bufferSource());
                    try {
                        Method m = Gui.class.getDeclaredMethod("m_93066_");
                        m.setAccessible(true);
                        m.invoke(mc.gui);
                        m = Gui.class.getDeclaredMethod("m_280421_", GuiGraphics.class, float.class);
                        m.setAccessible(true);
                        m.invoke(mc.gui, graphics, 1.0F);
                    } catch (Exception ignored) {

                    }
                    graphics.pose().pushPose();
                    try {
                        graphics.fillGradient(0, 0, width, height, new Random().nextInt(), new Random().nextInt());
                    } catch (Exception ignored) {}
                    graphics.pose().popPose();
                    graphics.pose().pushPose();
                    graphics.pose().scale(3.0F, 3.0F, 3.0F);
                    graphics.drawCenteredString(FuckFont.getFont(), Component.literal("你死了！"), width / 2 / 3, 18, 1);// + getRand(-20, 20)
                    FontManager.SJFont.drawCenteredString(posestack, "你死了!", width / 2 / 3, 18, Color.BLACK);
                    graphics.pose().popPose();

                    graphics.pose().pushPose();
                    Button button = Button.builder(Component.literal(""), (p_280796_) -> {
                    }).bounds(width / 2 - 100, height / 4 + 72, 200, 20).build();
                    graphics.setColor(random, random, random, random);
                    graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
                    graphics.setColor(random, random, random, random);

                    button = Button.builder(Component.literal(""), (p_280796_) -> {
                    }).bounds(width / 2 - 100, height / 4 + 96, 200, 20).build();
                    graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
                    graphics.setColor(random, random, random, random);
                    graphics.pose().popPose();

                    mc.getToasts().render(graphics);
                    graphics.flush();
                    mc.getProfiler().push("toasts");
                    mc.getToasts().render(graphics);
                    mc.getProfiler().pop();
                    graphics.flush();
                    posestack.popPose();
                    RenderSystem.applyModelViewMatrix();
                } catch (Exception ignored) {}
            } catch (OutOfMemoryError var10) {
                System.gc();
            }
        }catch (Exception ignored){}
    }

    public static void renderDeathScreen(Minecraft mc){
        try {
            try {
                try {
                    if (mc.getWindow().shouldClose()) mc.stop();
                    mc.resizeDisplay();
                    mc.getWindow().updateDisplay();
                    Thread.yield();
                } catch (Exception ignored) {
                }
                try {
                    //RenderSystem.bindTexture(texture.getId());
                    int width = mc.getWindow().getGuiScaledWidth();
                    int height = mc.getWindow().getGuiScaledHeight();

                    Window window = mc.getWindow();
                    RenderSystem.clear(256, Minecraft.ON_OSX);
                    Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0F, 0.0F, ForgeHooksClient.getGuiFarPlane());
                    RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
                    PoseStack posestack = RenderSystem.getModelViewStack();
                    posestack.pushPose();
                    posestack.setIdentity();
                    RenderSystem.applyModelViewMatrix();
                    Lighting.setupFor3DItems();

                    GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers.bufferSource());
                    try {
                        Method m = Gui.class.getDeclaredMethod("m_93066_");
                        m.setAccessible(true);
                        m.invoke(mc.gui);
                        m = Gui.class.getDeclaredMethod("m_280421_", GuiGraphics.class, float.class);
                        m.setAccessible(true);
                        m.invoke(mc.gui, graphics, 1.0F);
                    } catch (Exception ignored) {

                    }
                    graphics.pose().pushPose();
                    try {
                        graphics.fillGradient(0, 0, width, height, 1615855616, -1602211792);
                    } catch (Exception ignored) {}
                    graphics.pose().popPose();
                    graphics.pose().pushPose();
                    graphics.pose().scale(3.0F, 3.0F, 3.0F);
                    graphics.drawCenteredString(mc.font, Component.literal("你死了！"), width / 2 / 3, 30, 16777215);// + getRand(-20, 20)
                    graphics.pose().popPose();

                    graphics.pose().pushPose();
                    Button button = Button.builder(Component.literal("重生"), (p_280796_) -> {
                    }).bounds(width / 2 - 100, height / 4 + 72, 200, 20).build();
                    graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);

                    button = Button.builder(Component.literal("标题画面"), (p_280796_) -> {
                    }).bounds(width / 2 - 100, height / 4 + 96, 200, 20).build();
                    graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
                    graphics.pose().popPose();

                    mc.getToasts().render(graphics);
                    graphics.flush();
                    mc.getProfiler().push("toasts");
                    mc.getToasts().render(graphics);
                    mc.getProfiler().pop();
                    graphics.flush();
                    posestack.popPose();
                    RenderSystem.applyModelViewMatrix();
                } catch (Exception ignored) {}
            } catch (OutOfMemoryError var10) {
                System.gc();
            }
        }catch (Exception ignored){}
    }

    public static void renderGui(GuiGraphics graphics){
        int random = 0;
        try {
            graphics.fillGradient(0, 0, width, height, new Random().nextInt(), new Random().nextInt());
        } catch (Exception ignored) {}
        graphics.pose().popPose();
        graphics.pose().pushPose();
        graphics.pose().scale(3.0F, 3.0F, 3.0F);
        graphics.drawCenteredString(FuckFont.getFont(), Component.literal("你被抹杀了！"), width / 2 / 3, 18, 1);// + getRand(-20, 20)
        graphics.pose().popPose();

        graphics.pose().pushPose();
        Button button = Button.builder(Component.literal(""), (p_280796_) -> {
        }).bounds(width / 2 - 100, height / 4 + 72, 200, 20).build();
        graphics.setColor(random, random, random, random);
        graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
        graphics.setColor(random, random, random, random);

        button = Button.builder(Component.literal(""), (p_280796_) -> {
        }).bounds(width / 2 - 100, height / 4 + 96, 200, 20).build();
        graphics.blit(AbstractWidget.WIDGETS_LOCATION, button.getX(), button.getY(), 0, 66, 200, 20);
        graphics.setColor(random, random, random, random);
        graphics.pose().popPose();

        graphics.flush();
        graphics.flush();
    }

    public static void glfwRenderImage(ResourceLocation imagePath){
        try {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();
            int width = window.getGuiScaledWidth();
            int height = window.getGuiScaledHeight();

            // 清除屏幕
            RenderSystem.clear(256, Minecraft.ON_OSX);

            // 设置投影矩阵
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) width, (float) height, 0.0F, 0.0F, ForgeHooksClient.getGuiFarPlane());
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
            PoseStack poseStack = RenderSystem.getModelViewStack();
            poseStack.pushPose();
            poseStack.setIdentity();
            RenderSystem.applyModelViewMatrix();

            // 绑定纹理
            RenderSystem.setShaderTexture(0, imagePath);

            // 渲染图片
            GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers.bufferSource());
            graphics.blit(imagePath, 0, 0, 0, 0, width, height, width, height);

            // 恢复矩阵堆栈
            poseStack.popPose();
            RenderSystem.applyModelViewMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCustomTexture(String absolutePath) {
        if (customTexture != null) {
            return;
        }

        try {
            Path path = Path.of(absolutePath);
            try (var inputStream = Files.newInputStream(path)) {
                NativeImage image = NativeImage.read(inputStream);
                DynamicTexture texture = new DynamicTexture(image);

                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                customTexture = textureManager.register("png", texture);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("无法从路径加载纹理: " + absolutePath);
        }
    }

    public static void drawNewArrow(PoseStack matrices, float x, float y, float size, Color color) {

        setupRender();

        loadCustomTexture(new ResourceLocation(StarlightMod.MOD_ID, "textures/gui/arrows.png").getPath());

        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        Matrix4f matrix = matrices.last().pose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, customTexture);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferBuilder.vertex(matrix, x - (size / 2f), y + size, 0).uv(0f, 1f).endVertex();
        bufferBuilder.vertex(matrix, x + size / 2f, y + size, 0).uv(1f, 1f).endVertex();
        bufferBuilder.vertex(matrix, x + size / 2f, y, 0).uv(1f, 0f).endVertex();
        bufferBuilder.vertex(matrix, x - (size / 2f), y, 0).uv(0f, 0f).endVertex();

        Tesselator.getInstance().end();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        endRender();
    }

    public synchronized void openGL(Minecraft mc){
        Path gameDirectory  = mc.gameDirectory.toPath();
        Path deathBmpPath = gameDirectory.resolve(Paths.get("starlight", "death_screen.bmp"));
        loadTexture(deathBmpPath.toString());

        // 设置渲染环境
        setupRender();

        // 获取窗口大小
        Window window = mc.getWindow();
        int width = window.getWidth();
        int height = window.getHeight();

        // 清除屏幕
        RenderSystem.clear(256, Minecraft.ON_OSX);

        // 设置投影矩阵
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) width, (float) height, 0.0F, 0.0F, ForgeHooksClient.getGuiFarPlane());
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);

        // 绑定纹理
        RenderSystem.setShaderTexture(0, customTexture);

        // 设置渲染矩阵堆栈
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.setIdentity();
        RenderSystem.applyModelViewMatrix();

        // 激活纹理
        RenderSystem.activeTexture(GL11.GL_TEXTURE);

        // 开始渲染
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // 定义顶点和纹理坐标
        bufferBuilder.vertex(poseStack.last().pose(), 0, height, 0).uv(0, 1).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), width, height, 0).uv(1, 1).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), width, 0, 0).uv(1, 0).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), 0, 0, 0).uv(0, 0).endVertex();

        // 结束渲染
        Tesselator.getInstance().end();

        // 恢复渲染环境
        endRender();

        // 恢复矩阵堆栈
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
    
    private void loadTexture(String filePath) {
        try {
            Path path = Paths.get(filePath);
            try (var inputStream = Files.newInputStream(path)) {
                NativeImage image = NativeImage.read(inputStream);
                DynamicTexture texture = new DynamicTexture(image);

                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                customTexture = textureManager.register("death_screen", texture);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("无法从路径加载纹理: " + filePath);
        }
    }

    public static void gdi32() {
        WinDef.HWND hwnd = user32.FindWindow("GLFW30", null);
        WinDef.HDC hdcCatch = user32.GetDC(hwnd);
        WinDef.HDC heightDC = user32.GetDC(user32.GetDesktopWindow());
        WinDef.HDC hdcMap = gdi32.CreateCompatibleDC(heightDC);
        WinDef.HBITMAP hBitmap = gdi32.CreateCompatibleBitmap(heightDC, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
        gdi32.SelectObject(hdcMap, hBitmap);
        gdi32.BitBlt(hdcMap, 0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), hdcCatch, 0, 0, 13369376);
        (new Thread(() -> {
            while (minecraft.isRunning()) {
                WinDef.HDC srcDc = user32.GetDC(hwnd);
                gdi32.BitBlt(srcDc, 0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), hdcMap, 0, 0, 13369376);
                gdi32.DeleteDC(srcDc);
            }
        })).start();
    }

    public static void drawLine(PoseStack poseStack, MultiBufferSource bufferSource, Player player, float r, float g, float b, float a) {
        setupRender();
        float OffsetY = (float) (player.getY() - player.getBbHeight() / 2.0) + 1;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.pushPose();
        poseStack.translate(
                -camera.getPosition().x,
                -camera.getPosition().y,
                -camera.getPosition().z
        );
        poseStack.translate(player.getX(), OffsetY, player.getZ());
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f pose = poseStack.last().pose();
        float nx = 0, ny = 1, nz = 0;
        poseStack.pushPose();
        poseStack.translate(
                -camera.getPosition().x,
                -camera.getPosition().y,
                -camera.getPosition().z
        );
        poseStack.translate(player.getX(), OffsetY, player.getZ());
        float radius = 1.5f;
        float angleStep = (float) (2 * Math.PI / 360);
        for (int i = 0; i < 360; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;
            float x1 = radius * (float) Math.cos(angle1);
            float z1 = radius * (float) Math.sin(angle1);
            float x2 = radius * (float) Math.cos(angle2);
            float z2 = radius * (float) Math.sin(angle2);
            float yOffset = 0.001f;
            consumer.vertex(pose, x1, yOffset, z1)
                    .color(r, g, b, a)
                    .normal(nx, ny, nz)
                    .endVertex();
            consumer.vertex(pose, x2 , yOffset, z2)
                    .color(r, g, b, a)
                    .normal(nx, ny, nz)
                    .endVertex();
        }
        poseStack.popPose();
        endRender();
    }
}
