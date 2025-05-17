package net.daichang.starlightbyte.methods;

import org.objectweb.asm.*;

import java.io.IOException;

public class StarDynamic {
    public static void run(Object clazz){
        try {
            byte[] modifiedClass = modifyClass(clazz);
            interceptPlayerHealth(modifiedClass);
        } catch (Exception ignored){

        }
    }

    private static void interceptPlayerHealth(byte[] classfileBuffer) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (name.equals("getHealth") && descriptor.equals("()F")) {
                    return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.FRETURN) {
                                mv.visitInsn(Opcodes.FCONST_0);
                                mv.visitInsn(Opcodes.RETURN);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                return methodVisitor;
            }
        };
        ClassReader classReader = new ClassReader(classfileBuffer);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        classWriter.toByteArray();
    }


    private static byte[] modifyClass(Object clazz) throws IOException {
        ClassReader classReader = new ClassReader(((Class<?>) clazz).getName());
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new MyClassVisitor(classWriter);

        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

}
