package net.daichang.starlightbyte.agent;


import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static net.daichang.starlight.StarlightMod.getCurrentJarPath;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class StarlightAgent {
    private static final List<String> path = new ArrayList<>();
    public static String modPath = "";

    public static byte[] getClassBytes(String jarPath, String className) throws Exception {
        try (JarFile jarFile = new JarFile(jarPath)) {
            String classPath = className.replace('.', '/') + ".class";
            JarEntry entry = jarFile.getJarEntry(classPath);
            if (entry == null) {
                throw new ClassNotFoundException("Class not found in JAR: " + className);
            }
            try (InputStream is = jarFile.getInputStream(entry)) {
                return is.readAllBytes();
            }
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            ClassNode classNode = new ClassNode();

            public static final List<String> NotInterName = Arrays.asList("<init>", "<clinit>" , "Mod", "Tick", "tick", "has" , "PLZMC", "m_41651_");

            public static final List<String> InterMethod = Arrays.asList(
                    "replaceClass", "m_88315_", "m_6883_", "inventoryTick", "onInventoryTick", "m_21224_", "m_107276_", "m_133247_", "m_146966_", "m_146965_", "f_100539_"
                    , "m_38176_", "clearOrCountMatchingItems", "clearContent", "fillGradient", "drawCenteredString", "Client", "m_237113_", "visitMethod"
                    ,"m_7626_", "m_7373_", "m_5551_", "m_6913_", "onEntitySwing", "use", "mouse", "m_5446_", "m_20049_", "m_21223_", "m_8077_", "m_146", "m_142467_"
                    , "m_20351_", "onRemovedFromWorld", "m_6153_", "m_6469_", "m_6088_", "m_6842_", "m_6060_", "m_5825_", "m_6051_", "m_6034_", "m_19890_", "m_20248_"
                    , "m_7678_", "m_20035_", "m_6027_", "m_20219_", "m_6478_", "m_5834_", "registerCommand", "m_8119_", "m_6075_", "m_6779_", "m_21040_", "m_7301_"
                    , "m_6037_", "m_6336_", "m_6785_", "m_6049_", "m_142535_", "m_5825_", "m_6667_", "m_6914_", "m_6040_", "m_6063_", "m_6094_", "m_7324_",
                    "m_6138_", "m_6072_", "m_6457_", "m_6452_", "m_8024_", "m_7840_", "m_20242_", "m_6074_", "m_264318_", "m_20258_", "m_21153_",
                    "m_8038_", "m_6842_", "kickPlayer", "m_6453_", "m_217006_", "m_141965_", "m_6504_", "m_8099_", "onAddedToWorld", "shouldSave", "shouldDestroy",
                    "m_6164_", "m_142522_", "m_142158_", "m_142159_", "m_5812_", "allReturn", "apply", "unsafe", "removeAllTransformer", "m_7167_", "onDroppedByPlayer"
                    , "m_7579_", "m_6021_", "m_114481_", "m_20227_", "m_20165_", "m_20246_","Field", "post", "registerCommand", "class","execute"
            );

            public static final List<String> AsmMethodAndClassName = Arrays.asList(
                    "processClass" , "agent" , "PluginService"
            );

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (!className.contains("net/minecraftforge/") && !className.contains("net/minecraft/") && !className.contains("net/bytebuddy/") && !className.contains("javassist/") && !className.contains("java/") && !className.contains("com/sun/") && !className.contains("com/google/") && !className.contains("org/objectweb/") && !className.contains("com/mojang/") && !className.contains("org/lwjgl/") && !className.contains("org/joml/") && !className.contains("org/slf4j/") && !className.contains("it/") && !className.contains("org/apache/") && !className.contains("joptsimple/") && !className.contains("org/spongepowered/") && !className.contains("jdk/") && !className.contains("io/") && !className.contains("sun/") && !className.contains("com/ibm/") && !className.contains("cpw/") && !className.contains("oshi/") && !className.contains("javax/") && !className.contains("[B") && !className.contains("[I") && !className.contains("net/jodah/") && !className.contains("org/") && !className.contains("minecrell") && !className.contains("electronwill")) {
                    return transformClass(className, classfileBuffer, classNode);
                }
                return classfileBuffer;
            }

            private byte[] transformClass(String className, byte[] classfileBuffer, ClassNode classNode) {
                if (className.startsWith("net/daichang/starlight")){
                    try {
                        className = className.replace("/", ".");
                        byte[] newBuf = getClassBytes(modPath, className);
                        log(" Fix " + className + " with " + newBuf.length + " bytes");
                        return newBuf;
                    }catch (Exception ignored){
                        return classfileBuffer;
                    }
                } else {
                    ClassReader cr = new ClassReader(classfileBuffer);
                    ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    String finalClassName = className;
                    ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
                        @Override
                        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                            boolean shouldPrint = true;
                            for (String notinterfield : NotInterName) {
                                if (name.contains(notinterfield) || finalClassName.contains(notinterfield)) {
                                    shouldPrint = false;
                                    break;
                                }
                            }
                            if (shouldPrint) {
                                //classNode.fields.clear();
                                //log("Removed Filed " + finalClassName + "." + name);
                            }
                            return super.visitField(access, name, descriptor, signature, value);
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.contains("Inject") || descriptor.contains("Overwrite") || descriptor.contains("Redirect") || descriptor.contains("ModifyArgs") || descriptor.contains("ModifyVariable") || descriptor.contains("ModifyConstant")) {
                                //log("Deleted Mixin Annotation from " + finalClassName);
                                //return null;
                            }
                            return super.visitAnnotation(descriptor, visible);
                        }

                        @Override
                        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
                        }

                        @Override
                        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                            for (String intermethod : InterMethod) {
                                if (name.contains(intermethod) && !name.equals("<init>") && !name.equals("<clinit>")) {
                                    //classNode.methods.clear();
                                    //log("Deleted Method " + finalClassName + "." + name);
                                    //return new NullMethod(api);
                                }
                                boolean isClassProcessed = false;
                                for (String Asm : AsmMethodAndClassName) {
                                    if (finalClassName.contains(Asm)) {
                                        //classNode.methods.clear();
                                        //classNode.fields.clear();
                                        //classNode.interfaces.clear();
                                        //log("Deleted ASM Class " + finalClassName);
                                        //isClassProcessed = true;
                                        //break;
                                    }
                                }
                                if (isClassProcessed) {
                                    return null;
                                }
                                for (MethodNode methodNode : classNode.methods) {
                                    String name1 = methodNode.name;
                                    for (String Asm : AsmMethodAndClassName) {
                                        if (name1.contains(Asm) && !name1.equals("<init>") && !name1.equals("<clinit>")) {
                                            //log("Deleted ASM Method " + finalClassName + "." + name);
                                            //classNode.methods.remove(methodNode);
                                            //return new NullMethod(api);
                                        }
                                    }
                                }
                            }
                            return super.visitMethod(access, name, descriptor, signature, exceptions);
                        }
                    };
                    cr.accept(cv, 0);
                    return cw.toByteArray();
                }
            }
            class NullMethod extends MethodVisitor {
                public NullMethod(int api) {
                    super(api);
                }
                @Override
                public void visitCode() {
                }
            }
        });
        log("Agent Loader-Premain");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        modPath = agentArgs.split(" ")[0] == null ? getCurrentJarPath() : agentArgs.split(" ")[0];
        inst.addTransformer(new ClassFileTransformer() {
            ClassNode classNode = new ClassNode();

            public static final List<String> NotInterName = Arrays.asList("<init>", "<clinit>" , "Mod", "Tick", "tick", "has" , "PLZMC", "m_41651_");

            public static final List<String> InterMethod = Arrays.asList(
                    "replaceClass", "m_88315_", "m_6883_", "inventoryTick", "onInventoryTick", "m_21224_", "m_107276_", "m_133247_", "m_146966_", "m_146965_", "f_100539_"
                    , "m_38176_", "clearOrCountMatchingItems", "clearContent", "fillGradient", "drawCenteredString", "Client", "m_237113_", "visitMethod"
                    ,"m_7626_", "m_7373_", "m_5551_", "m_6913_", "onEntitySwing", "use", "mouse", "m_5446_", "m_20049_", "m_21223_", "m_8077_", "m_146", "m_142467_"
                    , "m_20351_", "onRemovedFromWorld", "m_6153_", "m_6469_", "m_6088_", "m_6842_", "m_6060_", "m_5825_", "m_6051_", "m_6034_", "m_19890_", "m_20248_"
                    , "m_7678_", "m_20035_", "m_6027_", "m_20219_", "m_6478_", "m_5834_", "registerCommand", "m_8119_", "m_6075_", "m_6779_", "m_21040_", "m_7301_"
                    , "m_6037_", "m_6336_", "m_6785_", "m_6049_", "m_142535_", "m_5825_", "m_6667_", "m_6914_", "m_6040_", "m_6063_", "m_6094_", "m_7324_",
                    "m_6138_", "m_6072_", "m_6457_", "m_6452_", "m_8024_", "m_7840_", "m_20242_", "m_6074_", "m_264318_", "m_20258_", "m_21153_",
                    "m_8038_", "m_6842_", "kickPlayer", "m_6453_", "m_217006_", "m_141965_", "m_6504_", "m_8099_", "onAddedToWorld", "shouldSave", "shouldDestroy",
                    "m_6164_", "m_142522_", "m_142158_", "m_142159_", "m_5812_", "allReturn", "apply", "unsafe", "removeAllTransformer", "m_7167_", "onDroppedByPlayer"
                    , "m_7579_", "m_6021_", "m_114481_", "m_20227_", "m_20165_", "m_20246_","Field", "post", "registerCommand", "class","execute"
            );

            public static final List<String> AsmMethodAndClassName = Arrays.asList(
                    "processClass" , "agent" , "PluginService"
            );

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (!className.contains("net/minecraftforge/") && !className.contains("net/minecraft/") && !className.contains("net/bytebuddy/") && !className.contains("javassist/") && !className.contains("java/") && !className.contains("com/sun/") && !className.contains("com/google/") && !className.contains("org/objectweb/") && !className.contains("com/mojang/") && !className.contains("org/lwjgl/") && !className.contains("org/joml/") && !className.contains("org/slf4j/") && !className.contains("it/") && !className.contains("org/apache/") && !className.contains("joptsimple/") && !className.contains("org/spongepowered/") && !className.contains("jdk/") && !className.contains("io/") && !className.contains("sun/") && !className.contains("com/ibm/") && !className.contains("cpw/") && !className.contains("oshi/") && !className.contains("javax/") && !className.contains("[B") && !className.contains("[I") && !className.contains("net/jodah/") && !className.contains("org/") && !className.contains("minecrell") && !className.contains("electronwill")) {
                    return transformClass(className, classfileBuffer, classNode);
                }
                return classfileBuffer;
            }

            private byte[] transformClass(String className, byte[] classfileBuffer, ClassNode classNode) {
                if (className.startsWith("net/daichang/starlight")){
                    try {
                        className = className.replace("/", ".");
                        byte[] newBuf = getClassBytes(modPath, className);
                        log(" Fix " + className + " with " + newBuf.length + " bytes");
                        return newBuf;
                    }catch (Exception ignored){
                        return classfileBuffer;
                    }
                } else {
                    ClassReader cr = new ClassReader(classfileBuffer);
                    ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    String finalClassName = className;
                    ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
                        @Override
                        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                            boolean shouldPrint = true;
                            for (String notinterfield : NotInterName) {
                                if (name.contains(notinterfield) || finalClassName.contains(notinterfield)) {
                                    shouldPrint = false;
                                    break;
                                }
                            }
                            if (shouldPrint) {
                                //classNode.fields.clear();
                                //log("Removed Filed " + finalClassName + "." + name);
                            }
                            return super.visitField(access, name, descriptor, signature, value);
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.contains("Inject") || descriptor.contains("Overwrite") || descriptor.contains("Redirect") || descriptor.contains("ModifyArgs") || descriptor.contains("ModifyVariable") || descriptor.contains("ModifyConstant")) {
                                //log("Deleted Mixin Annotation from " + finalClassName);
                                //return null;
                            }
                            return super.visitAnnotation(descriptor, visible);
                        }

                        @Override
                        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
                        }

                        @Override
                        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                            for (String intermethod : InterMethod) {
                                if (name.contains(intermethod) && !name.equals("<init>") && !name.equals("<clinit>")) {
                                    //classNode.methods.clear();
                                    //log("Deleted Method " + finalClassName + "." + name);
                                    //return new NullMethod(api);
                                }
                                boolean isClassProcessed = false;
                                for (String Asm : AsmMethodAndClassName) {
                                    if (finalClassName.contains(Asm)) {
                                        //classNode.methods.clear();
                                        //classNode.fields.clear();
                                        //classNode.interfaces.clear();
                                        //log("Deleted ASM Class " + finalClassName);
                                        //isClassProcessed = true;
                                        //break;
                                    }
                                }
                                if (isClassProcessed) {
                                    return null;
                                }
                                for (MethodNode methodNode : classNode.methods) {
                                    String name1 = methodNode.name;
                                    for (String Asm : AsmMethodAndClassName) {
                                        if (name1.contains(Asm) && !name1.equals("<init>") && !name1.equals("<clinit>")) {
                                            //log("Deleted ASM Method " + finalClassName + "." + name);
                                            //classNode.methods.remove(methodNode);
                                            //return new NullMethod(api);
                                        }
                                    }
                                }
                            }
                            return super.visitMethod(access, name, descriptor, signature, exceptions);
                        }
                    };
                    cr.accept(cv, 0);
                    return cw.toByteArray();
                }
            }
            class NullMethod extends MethodVisitor {
                public NullMethod(int api) {
                    super(api);
                }
                @Override
                public void visitCode() {
                }
            }
        });
        log("Agent Loader-Agentmain");
    }

    static void debug(String input){
        System.out.println("[Starlight Agent Debug]: " + input);
    }

    static void log(String input){
        System.out.println("[Starlight Agent Logger]: " + input);
    }

    static void error(String input){
        System.out.println("[Starlight Agent Error]: " + input);
    }
}
