package net.daichang.starlight.client.gui.fonts;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Function;

public class FuckFont extends Font {

    public FuckFont(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
        super(p_243253_, p_243245_);
    }

    public static FuckFont getFont() {
        return new FuckFont(Minecraft.getInstance().font.fonts, false);
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
        for (int index = 0; index < text.length(); index++) {
            String s = String.valueOf(text.charAt(index));
            float offset_y = (float)(y + Math.sin(((float)Util.getMillis() / 500.0F + index / 5.0F)) * 3.0D);
            float hue = (hueOffset + (float) index / text.length()) % 1.0F;
            int c = rgb & 0xFF000000 | Mth.hsvToRgb(hue, 0.8F, 1.0F);
            super.drawInBatch(s, x, offset_y, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            super.drawInBatch(s, x  + 0.852F, offset_y, c, dropShadow, matrix4f, multiBufferSource, mode, i, i1);
            x += width(s);
            if (text.contains("「 逐梦✵星光 」")) {
                multiBufferSource.getBuffer(RenderType.glint());
            }
        }
        return (int) x;
    }
}
