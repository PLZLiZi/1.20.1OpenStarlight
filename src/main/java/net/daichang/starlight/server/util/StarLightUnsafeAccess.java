package net.daichang.starlight.server.util;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class StarLightUnsafeAccess {
    public static final boolean SUPPORTS_GET_AND_SET_REF = hasGetAndSetSupport();

    public static final boolean SUPPORTS_GET_AND_ADD_LONG = hasGetAndAddLongSupport();

    public static final Unsafe UNSAFE = getUnsafe();

    static {
        if (UNSAFE == null)
            try {
                Field field = StarLightUnsafeAccess.class.getDeclaredField("UNSAFE");
                field.setAccessible(true);
                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Unsafe unsafe = (Unsafe)theUnsafe.get((Object)null);
                long offset = unsafe.staticFieldOffset(field);
                unsafe.putObject(StarLightUnsafeAccess.class, offset, getUnsafe());
                field.setAccessible(false);
            } catch (NoSuchFieldException|IllegalAccessException e) {
                throw new RuntimeException(e);
            }
    }

    private static Unsafe getUnsafe() {
        Unsafe instance;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            instance = (Unsafe)field.get((Object)null);
        } catch (Exception var4) {
            try {
                Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor(new Class[0]);
                c.setAccessible(true);
                instance = c.newInstance(new Object[0]);
            } catch (Exception var3) {
                throw new RuntimeException(var3);
            }
        }
        return instance;
    }

    private static boolean hasGetAndSetSupport() {
        try {
            Unsafe.class.getMethod("getAndSetObject", new Class[] { Object.class, long.class, Object.class });
            return true;
        } catch (Exception var1) {
            return false;
        }
    }

    private static boolean hasGetAndAddLongSupport() {
        try {
            Unsafe.class.getMethod("getAndAddLong", new Class[] { Object.class, long.class, long.class });
            return true;
        } catch (Exception var1) {
            return false;
        }
    }

    public static long fieldOffset(Class clz, String fieldName) throws RuntimeException {
        try {
            return UNSAFE.objectFieldOffset(clz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException var3) {
            throw new RuntimeException(var3);
        }
    }
}
