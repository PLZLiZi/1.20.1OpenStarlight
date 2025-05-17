package net.daichang.starlight.server.notification;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    private static final List<Notification> notificationsnew = new CopyOnWriteArrayList<>();
    private static final int maxNotifications = 8;
    private static final float positionY = 1.0F;
    private static final float spacingY = 3.0F;

    private final int backgroundColor = new Color(20, 20, 20, 220).getRGB();
    private final int textColor = Color.WHITE.getRGB();
    private static final float backgroundRadius = 0.0F;
    private final float blurRadius = 12.0f;
    private final boolean enableBlur = true;
    private final boolean enableGradient = true;

    private final float animationSpeed = 0.12f;

    private final int successColor = new Color(46, 204, 113).getRGB();
    private final int errorColor = new Color(231, 76, 60).getRGB();
    private final int enabledColor = new Color(52, 152, 219).getRGB();
    private final int disabledColor = new Color(142, 68, 173).getRGB();

    private static NotificationManager getInstance() {
        return new NotificationManager();
    }

    public static void publicity(String content, int second, Notification.Type type) {
        int displayTime = second;
        if (type == Notification.Type.ENABLED || type == Notification.Type.DISABLED) {
            displayTime = 2;
        }

        Notification notification = new Notification(content, type, displayTime * 1000);
        // 传递设置
        notification.updateSettings(
                backgroundRadius,
                getInstance().blurRadius,
                getInstance().enableBlur,
                getInstance().enableGradient,
                getInstance().animationSpeed,
                new Color(getInstance().backgroundColor),
                new Color(getInstance().textColor),
                new Color(getInstance().getColorForType(type))
        );
        notificationsnew.add(notification);
    }

    private int getColorForType(Notification.Type type) {
        return switch (type) {
            case SUCCESS -> successColor;
            case ERROR -> errorColor;
            case ENABLED -> enabledColor;
            case DISABLED -> disabledColor;
        };
    }

    public static void renderNotification(){
        PoseStack poseStack = new PoseStack();
        if (notificationsnew.size() > maxNotifications)
            notificationsnew.remove(0);

        float startY = Minecraft.getInstance().getWindow().getScreenHeight() * positionY - 36f;
        for (int i = 0; i < notificationsnew.size(); i++) {
            Notification notification = notificationsnew.get(i);
            notificationsnew.removeIf(Notification::shouldDelete);
            notification.render(poseStack, startY);
            startY -= (float) (notification.getHeight() + spacingY);
        }
    }
}
