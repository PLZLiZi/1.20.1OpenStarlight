package net.daichang.starlight.server.util.helper;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ModuleLayerHandler;
import cpw.mods.modlauncher.api.NamedPath;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.daichang.starlight.server.util.StarLightUnsafeAccess;
import net.daichang.starlightbyte.javassist.CannotCompileException;
import net.daichang.starlightbyte.javassist.CtClass;
import net.daichang.starlightbyte.javassist.CtMethod;
import net.daichang.starlightbyte.javassist.NotFoundException;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import org.objectweb.asm.*;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.module.ResolvedModule;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class HelperLib {
    public static final Unsafe UNSAFE;
    private static final Lookup lookup;
    private static final Object internalUNSAFE;
    private static MethodHandle objectFieldOffsetInternal;

    public static byte[] classByteGetter(Class<?> c) {
        return c.getName().getBytes();
    }

    public static CtMethod modifySuper(CtMethod ctMethod) throws NotFoundException, CannotCompileException {
        CtClass returnType = ctMethod.getReturnType();
        if (returnType != CtClass.voidType)
            ctMethod.setBody("{return super."  + ctMethod.getName() +"($args);}");
        else ctMethod.setBody("super."  + ctMethod.getName() +"($args);");
        return ctMethod;
    }

    //检测父类是否包含方法
    public static boolean superClassNotHasMethod(CtClass ctClass, String name) throws NotFoundException {
        for (CtClass k : superClasses(ctClass))
            for (CtMethod a : k.getDeclaredMethods())
                if (a.getName().equals(name))
                    return false;
        return true;
    }

    //获取所有父类，CtClass形式
    public static  HashSet<CtClass> superClasses(CtClass o) throws NotFoundException {
        HashSet<CtClass> superClasses = new HashSet<>();
        CtClass superClass = o.getSuperclass();
        if (superClass == null) {
            return new HashSet<>();
        }
        boolean isObjectClass = superClass.getName().equals(Object.class.getName());
        while (!isObjectClass) {
            superClasses.add(superClass);
            superClass = superClass.getSuperclass();
            isObjectClass = superClass.getName().equals(Object.class.getName());
        }
        return superClasses;
    }

    public static CtMethod modifySti(CtMethod ctMethod) throws NotFoundException, CannotCompileException {
        String value = "";
        CtClass returnType = ctMethod.getReturnType();
        if (returnType == CtClass.voidType) {
            value = ";";
        } else if (returnType == CtClass.doubleType) {
            value = "0.0D";
        } else if (returnType == CtClass.floatType) {
            value = "0.0F";
        } else if (returnType == CtClass.longType) {
            value = "0L";
        } else if (returnType == CtClass.intType) {
            value = "0";
        } else if (returnType == CtClass.charType) {
            value = "'\\u0000'";
        } else if (returnType == CtClass.byteType) {
            value = "(byte)0";
        } else if (returnType == CtClass.shortType) {
            value = "(short)0";
        } else if (returnType == CtClass.booleanType) {
            value = "false";
        } else {
            value = "null";
        }
        if (returnType == CtClass.voidType)
            ctMethod.setBody("{}");
        else
            ctMethod.setBody("{return " + value + "};");
        return ctMethod;
    }

    public static void replaceMethod(Method method, Method superMethod) {
        ClassReader cr;
        try {
            // 创建一个ClassReader来读取子类的字节码
            cr = new ClassReader(method.getDeclaringClass().getName().replace('.', '/'));
            ClassWriter cw = new ClassWriter(cr, 0);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    // 如果是目标方法，替换其内容
                    if (method.getName().equals(name)) {
                        return new MethodVisitor(Opcodes.ASM7, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                // 读取父类方法的字节码并将其插入到目标方法中
                                ClassReader superCr = null;
                                try {
                                    superCr = new ClassReader(superMethod.getDeclaringClass().getName().replace('.', '/'));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                superCr.accept(new ClassVisitor(Opcodes.ASM7) {
                                    @Override
                                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                                        if (superMethod.getName().equals(name)) {
                                            return cv.visitMethod(access, name, descriptor, signature, exceptions);
                                        }
                                        return null;
                                    }
                                }, ClassReader.SKIP_DEBUG);
                            }
                        };
                    }
                    return mv;
                }
            };
            cr.accept(cv, ClassReader.SKIP_DEBUG);

            // 定义一个新的类加载器来加载修改后的类
            byte[] newClassBytes = cw.toByteArray();
            ClassLoader classLoader = method.getDeclaringClass().getClassLoader();
            Class<?> newClass = new ByteArrayClassLoader(classLoader).defineClass(method.getDeclaringClass().getName(), newClassBytes);

            // 替换原始对象的类为新的类
            setClass(method.getDeclaringClass().newInstance(), newClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 自定义类加载器，用于加载修改后的类
    private static class ByteArrayClassLoader extends ClassLoader {
        public ByteArrayClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }


    static {
        UNSAFE = getUnsafe();
        lookup = getFieldValue(Lookup.class, "IMPL_LOOKUP", Lookup.class);
        internalUNSAFE = getInternalUNSAFE();
        try {
            Class<?> internalUNSAFEClass = lookup.findClass("jdk.internal.misc.Unsafe");
            objectFieldOffsetInternal = lookup.findVirtual(internalUNSAFEClass, "objectFieldOffset", MethodType.methodType(long.class, Field.class)).bindTo(internalUNSAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getInternalUNSAFE() {
        try {
            Class<?> clazz = lookup.findClass("jdk.internal.misc.Unsafe");
            return lookup.findStatic(clazz, "getUnsafe", MethodType.methodType(clazz)).invoke();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field f, Object target, Class<T> clazz) {
        try {
            long offset;
            if (Modifier.isStatic(f.getModifiers())) {
                target = UNSAFE.staticFieldBase(f);
                offset = UNSAFE.staticFieldOffset(f);
            } else offset = objectFieldOffset(f);
            return (T) UNSAFE.getObject(target, offset);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long objectFieldOffset(Field f) {
        try {
            return UNSAFE.objectFieldOffset(f);
        } catch (Throwable e) {
            try {
                return (long) objectFieldOffsetInternal.invoke(f);
            } catch (Throwable t1) {
                t1.printStackTrace();
            }
        }
        return 0L;
    }

    public static <T> T getFieldValue(Object target, String fieldName, Class<T> clazz) {
        try {
            return getFieldValue(target.getClass().getDeclaredField(fieldName), target, clazz);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getFieldValue(Class<?> target, String fieldName, Class<T> clazz) {
        try {
            return getFieldValue(target.getDeclaredField(fieldName), (Object) null, clazz);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            setFieldValue(target.getClass().getDeclaredField(fieldName), target, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setFieldValue(Field f, Object target, Object value) {
        try {
            long offset;
            if (Modifier.isStatic(f.getModifiers())) {
                target = UNSAFE.staticFieldBase(f);
                offset = UNSAFE.staticFieldOffset(f);
            } else offset = objectFieldOffset(f);
            UNSAFE.putObject(target, offset, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getJarPath(Class<?> clazz) {
        String file = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (!file.isEmpty()) {
            if (file.startsWith("union:"))
                file = file.substring(6);
            if (file.startsWith("/"))
                file = file.substring(1);
            file = file.substring(0, file.lastIndexOf(".jar") + 4);
            file = file.replaceAll("/", "\\\\");
        }
        return URLDecoder.decode(file, StandardCharsets.UTF_8);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
    public static void coexistenceCoreAndMod() {
        List<NamedPath> found = HelperLib.getFieldValue(ModDirTransformerDiscoverer.class, "found", List.class);
        found.removeIf(namedPath -> HelperLib.getJarPath(HelperLib.class).equals(namedPath.paths()[0].toString()));

        HelperLib.getFieldValue(HelperLib.getFieldValue(Launcher.INSTANCE, "moduleLayerHandler", ModuleLayerHandler.class), "completedLayers", EnumMap.class).values().forEach(layerInfo -> {
            ModuleLayer layer = HelperLib.getFieldValue(layerInfo, "layer", ModuleLayer.class);

            layer.modules().forEach(module -> {
                if (module.getName().equals(HelperLib.class.getModule().getName())) {
                    Set<ResolvedModule> modules = new HashSet<>(HelperLib.getFieldValue(layer.configuration(), "modules", Set.class));
                    Map<String, ResolvedModule> nameToModule = new HashMap(HelperLib.getFieldValue(layer.configuration(), "nameToModule", Map.class));

                    modules.remove(nameToModule.remove(HelperLib.class.getModule().getName()));

                    HelperLib.setFieldValue(layer.configuration(), "modules", modules);
                    HelperLib.setFieldValue(layer.configuration(), "nameToModule", nameToModule);
                }
            });
        });
    }

    public static void copyProperties(Class<?> clazz, Object source, Object target) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);

            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.set(target, field.get(source));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setClass(Object object, Class<?> targetClass) {
        if (object == null)
            throw new NullPointerException("object null");
        if (targetClass == null)
            throw new NullPointerException("targetClass null");
        try {
            lookup.ensureInitialized(object.getClass());
            lookup.ensureInitialized(targetClass);
            int of = StarLightUnsafeAccess.UNSAFE.getIntVolatile(StarLightUnsafeAccess.UNSAFE.allocateInstance(targetClass), StarLightUnsafeAccess.UNSAFE.addressSize());
            StarLightUnsafeAccess.UNSAFE.putIntVolatile(object, StarLightUnsafeAccess.UNSAFE.addressSize(), of);
        } catch (InstantiationException|IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void replaceClass(Object object, Class<?> targetClass) {
        if (object == null)
            throw new NullPointerException("object==null");
        if (targetClass == null)
            throw new NullPointerException("targetClass==null");
        try {
            int klass_ptr = UnsafeAccess.UNSAFE.getIntVolatile(UnsafeAccess.UNSAFE.allocateInstance(targetClass), 8L);
            UnsafeAccess.UNSAFE.putIntVolatile(object, 8L, klass_ptr);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // We don't want loopback or non-running interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // We only want IPv4 addresses
                    if (addr instanceof java.net.Inet4Address)
                        return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
