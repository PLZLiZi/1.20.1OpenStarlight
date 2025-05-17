package net.daichang.starlight.server.util.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static net.daichang.starlight.server.util.render.RenderUtil.endRender;
import static net.daichang.starlight.server.util.render.RenderUtil.setupRender;

public class Render2DUtil {
    public static HashMap<Integer, Integer> shadowCache = new HashMap<>();

    public static void drawDeathScreen(PoseStack poseStack, float width, float height) {
        setupRender();
        Color color = Color.GRAY;
        drawRect(poseStack, 0, 0, width, height, Color.RED);
        drawRoundGradient(poseStack, width / 2 - 100, height / 4 + 72, 200, 20, 15, color.getRGB(), color.getRGB());
        drawRoundGradient(poseStack, width / 2 - 100, height / 4 + 96, 200, 20, 15, color.getRGB(), color.getRGB());
        endRender();
    }

    //绘制阴影
    public static void drawBlurredShadow(PoseStack matrices, float x, float y, float width, float height, int blurRadius, Color color) {
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;
        //System.out.println("Info:" + x + "|" + y + "|" + width + "|" + height);
        int identifier = (int) (width * height + width + color.hashCode() * blurRadius + blurRadius);

        // 开启渲染状态
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);
        } else {
            if (width <= 0) width = 1;
            if (height <= 0) height = 1;

            // 创建 BufferedImage
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);

            Graphics2D g = original.createGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();

            // 应用高斯模糊
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);

            // 将 BufferedImage 转换为 NativeImage
            NativeImage nativeImage = convertBufferedImageToNativeImage(blurred, color);

            // 创建 OpenGL 纹理 ID
            texId = TextureUtil.generateTextureId();
            TextureUtil.prepareImage(texId, blurred.getWidth(), blurred.getHeight());

            // 上传纹理到显存
            nativeImage.upload(0, 0, 0, false); // 使用 NativeImage 的上传方法
            nativeImage.close(); // 用完后释放资源

            shadowCache.put(identifier, texId);
        }

        // 绑定纹理并进行渲染
        RenderSystem.setShaderTexture(0, texId);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        Matrix4f matrix = matrices.last().pose();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, _X, _Y, 0).uv(0, 0).endVertex(); // Top-left
        buffer.vertex(matrix, _X, _Y + height, 0).uv(0, 1).endVertex(); // Bottom-left
        buffer.vertex(matrix, _X + width, _Y + height, 0).uv(1, 1).endVertex(); // Bottom-right
        buffer.vertex(matrix, _X + width, _Y, 0).uv(1, 0).endVertex(); // Top-right
        tesselator.end();

        // 恢复渲染状态
        RenderSystem.disableBlend();
    }

    //绘制渐变矩形
    public static void drawGradientRound(PoseStack ms, float v, float v1, float i, float i1, float v2, Color darker, Color darker1, Color darker2, Color darker3) {
        renderRoundedQuad2(ms, darker, darker1, darker2, darker3, v, v1, v + i, v1 + i1, v2);
    }

    //绘制2d圆角矩形
    public static void renderRoundedQuad2(PoseStack matrices, Color c, Color c2, Color c3, Color c4, double fromX, double fromY, double toX, double toY, double radius) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        renderRoundedQuadInternal2(
                matrices.last().pose(),
                c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f,
                c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, c2.getAlpha() / 255f,
                c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f, c3.getAlpha() / 255f,
                c4.getRed() / 255f, c4.getGreen() / 255f, c4.getBlue() / 255f, c4.getAlpha() / 255f,
                fromX, fromY, toX, toY, radius
        );

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void renderRoundedQuadInternal2(
            Matrix4f matrix,
            float cr, float cg, float cb, float ca, // 左上角颜色 (RGBA)
            float cr1, float cg1, float cb1, float ca1, // 右上角颜色 (RGBA)
            float cr2, float cg2, float cb2, float ca2, // 左下角颜色 (RGBA)
            float cr3, float cg3, float cb3, float ca3, // 右下角颜色 (RGBA)
            double fromX, double fromY, double toX, double toY, double radius
    ) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        // 启用混合模式和默认混合函数
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        // 开始绘制
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        // 圆角的四个角 (右下, 右上, 左上, 左下)
        double[][] map = new double[][]{
                {toX - radius, toY - radius, radius}, // 右下角
                {toX - radius, fromY + radius, radius}, // 右上角
                {fromX + radius, fromY + radius, radius}, // 左上角
                {fromX + radius, toY - radius, radius} // 左下角
        };

        // 循环绘制每个圆角的顶点
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];

            // 以 10 度为步长，绘制圆角
            for (double r = i * 90; r <= 90 + i * 90; r += 10) {
                float radian = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(radian) * rad);
                float cos = (float) (Math.cos(radian) * rad);

                switch (i) {
                    case 0 -> bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F)
                            .color(cr3, cg3, cb3, ca3).endVertex(); // 右下角颜色
                    case 1 -> bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F)
                            .color(cr1, cg1, cb1, ca1).endVertex(); // 右上角颜色
                    case 2 -> bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F)
                            .color(cr, cg, cb, ca).endVertex(); // 左上角颜色
                    default -> bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F)
                            .color(cr2, cg2, cb2, ca2).endVertex(); // 左下角颜色
                }
            }
        }

        // 提交渲染
        tesselator.end();

        // 恢复渲染状态
        RenderSystem.enableDepthTest();
    }

    private static NativeImage convertBufferedImageToNativeImage(BufferedImage image, Color customColor) {
        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, image.getWidth(), image.getHeight(), false);

        // 自定义颜色的 RGBA
        int customA = customColor.getAlpha();
        int customR = customColor.getRed();
        int customG = customColor.getGreen();
        int customB = customColor.getBlue();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);

                // 提取原图的 Alpha 值（保持原始透明度）
                int originalA = (argb >> 24) & 0xFF;

                // 混合透明度
                int finalA = (originalA * customA) / 255;

                // 手动组合 ARGB 值
                int combinedColor = (finalA << 24) | (customR << 16) | (customG << 8) | customB;

                nativeImage.setPixelRGBA(x, y, combinedColor);
            }
        }

        return nativeImage;

    }

    public static void drawRect(PoseStack matrices, float x, float y, float width, float height, Color c) {
        Matrix4f matrix = matrices.last().pose();;
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.vertex(matrix, x, y + height, 0.0F).color(c.getRGB());
        buffer.vertex(matrix, x + width, y + height, 0.0F).color(c.getRGB());
        buffer.vertex(matrix, x + width, y, 0.0F).color(c.getRGB());
        buffer.vertex(matrix, x, y, 0.0F).color(c.getRGB());
        tesselator.end();
        endRender();
    }

    public static void drawRound(PoseStack matrices, float x, float y, float width, float height, float radius, Color color) {
        renderRoundedQuad(matrices, color, x, y, width + x, height + y, radius, 4);
    }

    public static void renderRoundedQuad(PoseStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        renderRoundedQuadInternal(matrices.last().pose(), c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f, fromX, fromY, toX, toY, radius, samples);
        endRender();
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        RenderSystem.enableBlend();  // 开启混合
        RenderSystem.defaultBlendFunc();  // 使用默认混合函数
        RenderSystem.disableDepthTest();  // 禁用深度测试，确保透明物体正确显示

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        // 绘制圆角矩形的四个角
        double[][] map = new double[][]{
                {toX - radius, toY - radius, radius},
                {toX - radius, fromY + radius, radius},
                {fromX + radius, fromY + radius, radius},
                {fromX + radius, toY - radius, radius}
        };

        // 遍历四个角，分别绘制圆弧
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F)
                        .color(cr, cg, cb, ca)  // 使用传入的透明度
                        .endVertex();
            }
            float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F)
                    .color(cr, cg, cb, ca)  // 使用透明颜色
                    .endVertex();
        }

        RenderSystem.enableDepthTest();  // 恢复深度测试

        tesselator.end();  // 结束渲染
    }

    public static void draw2DGradientRect(PoseStack matrices, float left, float top, float right, float bottom, Color leftBottomColor, Color leftTopColor, Color rightBottomColor, Color rightTopColor) {
        Matrix4f matrix = matrices.last().pose();
        setupRender();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, right, top, 0.0F).color(rightTopColor.getRGB());
        bufferBuilder.vertex(matrix, left, top, 0.0F).color(leftTopColor.getRGB());
        bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(leftBottomColor.getRGB());
        bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(rightBottomColor.getRGB());

        tesselator.end();
        endRender();
    }

    //绘制带有渐变的圆角矩形
    public static void drawRoundGradient(PoseStack poseStack, float x, float y, float width, float height, float radius, int startColor, int endColor) {
        // 首先绘制圆角矩形背景
        drawRound(poseStack, x, y, width, height, radius, new Color(startColor));

        // 然后在上面绘制渐变效果
        Color start = new Color(startColor);
        Color end = new Color(endColor);

        draw2DGradientRect(
                poseStack,
                x,
                y,
                x + width,
                y + height,
                start,
                end,
                start,
                end
        );
    }
}
