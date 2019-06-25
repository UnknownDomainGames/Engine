package nullengine.mod.annotation.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class ProcessingUtils {

    public static FileObject createFile(ProcessingEnvironment processingEnv, JavaFileManager.Location location, String relativeName) {
        try {
            return processingEnv.getFiler().createResource(location, "", relativeName);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasModifier(Element element, Modifier modifier) {
        return element.getModifiers().stream().anyMatch(m -> modifier == m);
    }

    public static AnnotationMirror getAnnotation(Element element, Class<? extends Annotation> anno) {
        return getAnnotation(element, anno.getName());
    }

    public static AnnotationMirror getAnnotation(Element element, String annoName) {
        for (AnnotationMirror anno : element.getAnnotationMirrors()) {
            if (annoName.equals(anno.getAnnotationType().toString())) {
                return anno;
            }
        }
        return null;
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotation, String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
            if (name.equals(entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static AnnotationValue getAnnotationValue(Element element, String annoName, String key) {
        return getAnnotationValue(getAnnotation(element, annoName), key);
    }

    public static AnnotationValue getAnnotationValue(Element element, Class<? extends Annotation> anno, String key) {
        return getAnnotationValue(getAnnotation(element, anno), key);
    }

    public static Map<String, Object> getAnnotationValues(AnnotationMirror annotation) {
        Map<String, Object> values = new HashMap<>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
            values.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        }
        return values;
    }

    public static Map<String, Object> getAnnotationValues(Element element, String annoName) {
        return getAnnotationValues(getAnnotation(element, annoName));
    }

    public static Map<String, Object> getAnnotationValues(Element element, Class<? extends Annotation> anno) {
        return getAnnotationValues(getAnnotation(element, anno));
    }

    public static Name getQualifiedName(TypeMirror typeMirror) {
        return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getQualifiedName();
    }
}
