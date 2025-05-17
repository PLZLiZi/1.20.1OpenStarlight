package net.daichang.starlight.server.util.daichangs;

import net.daichang.starlight.server.util.GodPlayerList;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("DCMethodTest")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DCTransformerProcessor extends AbstractProcessor  {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                DCMethodTest dcAnnotation = element.getAnnotation(DCMethodTest.class);
                Class<?> targetClazz = dcAnnotation.clazz();
                GodPlayerList.addGod(targetClazz);
            }
        }
        return true;
    }
}