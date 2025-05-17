package net.daichang.starlight.server.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class BetterAnimation {
    private final int maxTick;
    private int prevTick;
    private int tick;

    public BetterAnimation(int maxTick) {
        this.maxTick = maxTick;
        this.tick = 0;  // 初始化 tick 为 0
        this.prevTick = 0;
    }

    public BetterAnimation() {
        this(10);
    }

    public static double dropAnimation(double value) {
        double c1 = 1.70158;
        double c3 = 2.70158;
        return 1 + c3 * Math.pow(value - 1, 3) + c1 * Math.pow(value - 1, 2);
    }

    public void update(boolean update) {
        prevTick = tick;
        tick = Mth.clamp(tick + (update ? 1 : -1), 0, maxTick);
    }

    public double getAnimationd() {
        return dropAnimation((this.prevTick + (this.tick - this.prevTick) * Minecraft.getInstance().getFrameTime()) / maxTick);
    }

    // 添加 reset 方法，用于重置动画状态
    public void reset() {
        this.tick = 0;
        this.prevTick = 0;
    }
}
