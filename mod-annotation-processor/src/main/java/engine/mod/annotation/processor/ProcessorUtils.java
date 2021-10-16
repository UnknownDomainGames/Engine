package engine.mod.annotation.processor;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProcessorUtils {

    public static @Nonnull
    FileObject createFile(ProcessingEnvironment processingEnv, JavaFileManager.Location location, String relativeName) {
        try {
            return processingEnv.getFiler().createResource(location, "", relativeName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasModifier(Element element, Modifier modifier) {
        return element.getModifiers().stream().anyMatch(m -> modifier == m);
    }

    public static Name getQualifiedName(TypeMirror typeMirror) {
        return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getQualifiedName();
    }

    public static boolean isAssignableIgnoreGeneric(Types types, TypeMirror type, TypeMirror assignable) {
        return isSameTypeIgnoreGeneric(type, assignable) || isSubtypeIgnoreGeneric(types, type, assignable);
    }

    public static boolean isSameTypeIgnoreGeneric(TypeMirror type, TypeMirror other) {
        return getQualifiedName(type).equals(getQualifiedName(other));
    }

    public static boolean isSubtypeIgnoreGeneric(Types types, TypeMirror type, TypeMirror subtype) {
        Name typeName = getQualifiedName(type);
        for (TypeMirror supertype : supertypes(types, subtype)) {
            if (typeName.equals(getQualifiedName(supertype))) {
                return true;
            }
        }
        return false;
    }

    public static List<? extends TypeMirror> supertypes(Types types, TypeMirror type) {
        Set<TypeMirror> supertypes = new LinkedHashSet<>();
        supertypes(supertypes, types, type);
        return List.copyOf(supertypes);
    }

    private static void supertypes(Set<TypeMirror> supertypes, Types types, TypeMirror type) {
        List<? extends TypeMirror> directSupertypes = types.directSupertypes(type);
        supertypes.addAll(directSupertypes);
        directSupertypes.forEach(supertype -> supertypes(supertypes, types, supertype));
    }
}
