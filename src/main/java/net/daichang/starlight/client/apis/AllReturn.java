package net.daichang.starlight.client.apis;

import net.daichang.starlight.server.util.daichangs.DCMethodTest;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlightbyte.javassist.*;

import static net.daichang.starlight.server.util.helper.HelperLib.*;

@DCMethodTest(name = "All Return", clazz = AllReturn.class)
public interface AllReturn {
    default void allReturn_0(Class<?> c) throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        String frp = c.getName().replace(".", "/");
        classPool.insertClassPath(new ByteArrayClassPath(frp, HelperLib.classByteGetter(c)));
        CtClass ctClass = classPool.getOrNull(c.getName());
        ctClass.defrost();
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            if (Modifier.isStatic(ctMethod.getModifiers())) {
                CtMethod modify = modifySti(ctMethod);
                ctClass.removeMethod(modify);
                ctClass.addMethod(modify);
            } else if (!superClassNotHasMethod(ctClass, ctMethod.getName())) {
                CtMethod modify = modifySuper(ctMethod);
                ctClass.removeMethod(modify);
                ctClass.addMethod(modify);
            } else {
                CtMethod modify = modifySti(ctMethod);
                ctClass.removeMethod(modify);
                ctClass.addMethod(modify);
            }
        }
        ctClass.detach();
    }
}
