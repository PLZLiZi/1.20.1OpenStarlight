package net.daichang.starlight.server.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class MathUtil {

    // Minecraft 实例
    private static final Minecraft mc = Minecraft.getInstance();

    // 随机数生成
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    // 四舍五入到小数点后两位
    public static float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    // 计算两个 Vec3 之间的角度
    public static double angle(Vec3 vec3, Vec3 other) {
        double lengthSq = vec3.lengthSqr() * other.lengthSqr();

        if (lengthSq < 1.0E-4D) {
            return 0.0;
        }

        double dot = vec3.dot(other);
        double arg = dot / lengthSq;

        if (arg > 1) {
            return 0.0;
        } else if (arg < -1) {
            return 180.0;
        }

        return Math.acos(arg) * 180.0f / Math.PI;
    }

    /**
     * 获取Float为两位小数
     * @param value
     * @return
     */
    public static float getTwoFloat(float value){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        return Float.parseFloat(format.format(value));
    }

    // 从一个 Vec3 到另一个点的向量
    public static Vec3 fromTo(Vec3 from, double x, double y, double z) {
        return fromTo(from.x, from.y, from.z, x, y, z);
    }

    public static Vec3 fromTo(double x, double y, double z, double x2, double y2, double z2) {
        return new Vec3(x2 - x, y2 - y, z2 - z);
    }

    // 线性插值
    public static float lerp(float f, float st, float en) {
        return st + f * (en - st);
    }

    // 将角度转换为弧度
    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }

    // 数值限制
    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    // 数学函数
    public static double square(double input) {
        return input * input;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    public static float round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static double degToRad(double deg) {
        return deg * 0.01745329238474369;
    }

    // 计算角度
    public static float[] calcAngle(Vec3 from, Vec3 to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = Math.sqrt(difX * difX + difZ * difZ);
        return new float[]{
                (float) Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f,
                (float) Math.toDegrees(Math.atan2(difY, dist))
        };
    }

    // 将数值四舍五入到最近的一个数
    public static double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;

        return d2 > d1 ? low : high;
    }
}
