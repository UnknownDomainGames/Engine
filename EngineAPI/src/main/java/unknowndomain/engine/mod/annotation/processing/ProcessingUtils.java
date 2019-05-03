package unknowndomain.engine.mod.annotation.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.util.List;
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

    public static Name getQualifiedName(TypeMirror typeMirror) {
        return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getQualifiedName();
    }

    public static List<? extends TypeMirror> getInterfaces(TypeMirror typeMirror) {
        return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getInterfaces();
    }

    public static TypeMirror getSuperclass(TypeMirror typeMirror) {
        return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getSuperclass();
    }
}
