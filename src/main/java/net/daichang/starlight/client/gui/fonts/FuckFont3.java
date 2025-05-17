package net.daichang.starlight.client.gui.fonts;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Random;
import java.util.function.Function;

public class FuckFont3 extends Font {
    public FuckFont3(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
        super(p_243253_, p_243245_);
    }

    public static FuckFont3 getFont() {
        return new FuckFont3(Minecraft.getInstance().font.fonts, false);
    }

    long getSystemTime() {
        return System.currentTimeMillis() * 1000L / System.nanoTime();
    }

    public int drawInBatch(@NotNull FormattedCharSequence formattedCharSequence, float x, float y, int rgb, boolean b1, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource multiBufferSource, @NotNull DisplayMode mode, int i, int i1) {
        StringBuilder builder = new StringBuilder();
        formattedCharSequence.accept((p_13746_, p_13747_, p_13748_) -> {
            builder.appendCodePoint(p_13748_);
            return true;
        });
        return renderFont(builder.toString(), x, y, rgb, b1, matrix4f, multiBufferSource, mode, i, i1, this.isBidirectional());
    }

    public int drawInBatch(@NotNull String text, float x, float y, int rgb, boolean b, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource source, @NotNull DisplayMode mode, int i, int i1) {
        return renderFont(text, x, y, rgb, b, matrix4f, source, mode, i, i1, this.isBidirectional());
    }

    public int drawInBatch(@NotNull Component component, float x, float y, int rgb, boolean b, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource source, @NotNull DisplayMode mode, int i, int i1) {
        return renderFont(component.getString(), x, y, rgb, b, matrix4f, source, mode, i, i1, this.isBidirectional());
    }

    public int renderFont(@NotNull String text, float x, float y, int rgb, boolean dropShadow, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource multiBufferSource, @NotNull DisplayMode mode, int i, int i1, boolean isText){
        float hueOffset = (float) Util.getMillis() / 800.0F;
        float time = (float) Util.getMillis() / 1000.0F; // 获取当前时间（秒）
        float amplitude = 20.0F; // 水平偏移的幅度
        float frequency = 1.5F; // 水平偏移的频率

        for (int index = 0; index < text.length(); index++) {
            String s = String.valueOf(text.charAt(index));
            int a = new Random().nextInt();
            float hue = (hueOffset + (float) index / text.length()) % 1.0F;
            int c = rgb & 0xFF000000 | Mth.hsvToRgb(hue, 0.8F, 1.0F);
            float offset_y = y + ((float) Math.sin(((index + 1) + (float) System.nanoTime() / 1000000L / 300.0F)));
            // 计算缩放因子
            float scaleFactor = (float) index / (text.length() - 1); // 从0到1
            // 计算水平偏移量
            float offset_x = amplitude * scaleFactor * (float) Math.sin(frequency * time + index * 0.1F);
            // 渲染字符
            super.drawInBatch(s, x + offset_x, offset_y, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, x + offset_x + 0.75F, offset_y, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, a, a, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, a, a, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, a, a, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, a, a, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, a, a, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, a, a, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            x += width(s);
        }
        return (int) x;
    }
}
