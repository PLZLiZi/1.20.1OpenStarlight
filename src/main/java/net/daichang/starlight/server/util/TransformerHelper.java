package net.daichang.starlight.server.util;

import sun.misc.Unsafe;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TransformerHelper {
    private static Unsafe unsafe = StarLightUnsafeAccess.UNSAFE;

    public static Instrumentation instrumentation;

    protected TransformerHelper(Instrumentation ins){
        instrumentation = ins;
    }

    static {
        try {
            if (unsafe == null) {
                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                unsafe = (Unsafe)theUnsafe.get((Object)null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }

    public static Object getTransformerManager(Instrumentation inst) {
        try {
            long mTransformerManagerOffset = unsafe.objectFieldOffset(
                    Class.forName("sun.instrument.InstrumentationImpl").getDeclaredField("mTransformerManager"));
            return unsafe.getObject(inst, mTransformerManagerOffset);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get TransformerManager", e);
        }
    }

    public static Object[] getTransformerList(Object manager) {
        try {
            long transformerListOffset = unsafe.objectFieldOffset(
                    Class.forName("sun.instrument.TransformerManager").getDeclaredField("mTransformerList"));
            return (Object[])unsafe.getObject(manager, transformerListOffset);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get transformerList", e);
        }
    }

    public static ClassFileTransformer getTransformerFromInfo(Object info) {
        try {
            long transformerOffset = unsafe.objectFieldOffset(
                    Class.forName("sun.instrument.TransformerManager$TransformerInfo").getDeclaredField("mTransformer"));
            return (ClassFileTransformer)unsafe.getObject(info, transformerOffset);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get transformer from TransformerInfo", e);
        }
    }

    public static List<ClassFileTransformer> getAllTransformers(Instrumentation inst) {
        try {
            Object manager = getTransformerManager(inst);
            Object[] transformerList = getTransformerList(manager);
            List<ClassFileTransformer> transformers = new ArrayList<>();
            for (Object info : transformerList)
                transformers.add(getTransformerFromInfo(info));
            return transformers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve transformers", e);
        }
    }
}
