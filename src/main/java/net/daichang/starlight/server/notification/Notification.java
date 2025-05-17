package net.daichang.starlight.server.notification;

import com.mojang.blaze3d.vertex.PoseStack;
import net.daichang.starlight.server.util.render.BetterAnimation;
import net.daichang.starlight.server.util.MathUtil;
import net.daichang.starlight.server.util.render.Render2DUtil;
import net.daichang.starlight.server.util.fonts.FontManager;
import net.daichang.starlight.server.util.time.Timer;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Notification {
    private final String message;
    private final Timer timer;
    private final Type type;
    private final float height = 30;
    private final long stayTime;
    public BetterAnimation animation = new BetterAnimation();
    private float posY;
    private final float width;
    private float animationX;
    private boolean direction = false;
    private final Timer animationTimer = new Timer();

    // 自定义设置变量
    private float backgroundRadius = 4f;
    private float blurRadius = 12f;
    private boolean enableBlur = true;
    private boolean enableGradient = true;
    private float animationSpeed = 0.12f;
    private Color backgroundColor = new Color(20, 20, 20, 220);
    private Color textColor = Color.WHITE;
    private Color typeColor;

    public Notification(String message, Type type, int time) {
        this.stayTime = time;
        this.message = message;
        this.type = type;
        this.timer = new Timer();
        this.timer.reset();

        Minecraft mc = Minecraft.getInstance();
        this.width = Math.max(200, message.length() + 50);
        this.animationX = this.width;
        this.posY = mc.getWindow().getGuiScaledHeight() - this.height;
    }

    public void updateSettings(float backgroundRadius, float blurRadius, boolean enableBlur,
                               boolean enableGradient, float animationSpeed,
                               Color backgroundColor, Color textColor, Color typeColor) {
        this.backgroundRadius = backgroundRadius;
        this.blurRadius = blurRadius;
        this.enableBlur = enableBlur;
        this.enableGradient = enableGradient;
        this.animationSpeed = animationSpeed;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.typeColor = typeColor;
    }

    public void render(PoseStack poseStack, float getY) {
        float alpha = (float) MathUtil.clamp(1 - animation.getAnimationd(), 0, 1);
        Color textColorWithAlpha = new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), (int)(alpha * 255));
        Color shadowColor = new Color(0, 0, 0, (int)(alpha * 160));

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();

        this.direction = isFinished();
        this.animationX = (float) (this.width * animation.getAnimationd());
        this.posY = animate(this.posY, getY);

        int x1 = (int) ((screenWidth - 10) - this.width + this.animationX);
        int y1 = (int) this.posY;

        // 绘制背景
        if (enableBlur) {
            Render2DUtil.drawBlurredShadow(poseStack, x1, y1, (int) this.width, (int) this.height,
                    (int)blurRadius, shadowColor);
        }
        Render2DUtil.drawRound(poseStack, x1, y1, (int) this.width, (int) this.height,
                backgroundRadius, backgroundColor);

        // 绘制类型指示条
        float indicatorWidth = 4f;
        Render2DUtil.drawRound(poseStack, x1, y1, indicatorWidth, this.height, backgroundRadius,
                new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), (int)(180 * alpha)));

        if (enableBlur) {
            Render2DUtil.drawBlurredShadow(poseStack, x1, y1, indicatorWidth * 2, this.height, 8,
                    new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), (int)(80 * alpha)));
        }

        if (enableGradient) {
            float gradientWidth = 15f;
            Color gradientStart = new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), (int)(60 * alpha));
            Color gradientEnd = new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), 0);
            Render2DUtil.drawGradientRound(poseStack, x1 + indicatorWidth, y1, gradientWidth, this.height, 0f,
                    gradientStart, gradientEnd, gradientStart, gradientEnd);
        }

        // 绘制文本
        float textX = x1 + (enableGradient ? 21f : 8f);
        float typeTextY = y1 + 4;
        float messageTextY = y1 + this.height - FontManager.Genshin16.getHeight() - 4;

        String icon = getTypeIcon();
        float iconWidth = FontManager.Genshin16.getStringWidth(icon);
        FontManager.Genshin16.drawString(poseStack, icon, textX, typeTextY, typeColor.getRGB());
        FontManager.Genshin16.drawString(poseStack, type.getName(), textX + iconWidth + 4, typeTextY, textColorWithAlpha.getRGB());
        FontManager.Genshin16.drawString(poseStack, message, textX, messageTextY, textColorWithAlpha.getRGB());

        if (this.animationTimer.passedMs(50)) {
            this.animation.update(this.direction);
            this.animationTimer.reset();
        }
    }

    private String getTypeIcon() {
        switch (type) {
            case SUCCESS: return "✓";
            case ERROR: return "✕";
            case ENABLED: return "●";
            case DISABLED: return "○";
            default: return "";
        }
    }

    private boolean isFinished() {
        return this.timer.passedMs(this.stayTime);
    }

    public double getHeight() {
        return this.height;
    }

    public boolean shouldDelete() {
        return (isFinished()) && this.animationX >= this.width - 5;
    }


    public enum Type {
        SUCCESS("Success"),
        ERROR("Error"),
        ENABLED("Enabled"),
        DISABLED("Disabled");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public float animate(float value, float target) {
        return value + (target - value) * animationSpeed;
    }
}