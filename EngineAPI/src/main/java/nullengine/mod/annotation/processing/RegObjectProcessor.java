package nullengine.mod.annotation.processing;

import nullengine.mod.annotation.RegObject;
import nullengine.util.JsonUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static nullengine.mod.annotation.processing.ProcessingUtils.createFile;
import static nullengine.mod.annotation.processing.ProcessingUtils.hasModifier;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class RegObjectProcessor extends AbstractProcessor {

    private final Map<String, List<String>> items = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(RegObject.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(RegObject.class)) {
                if (!hasModifier(element, Modifier.STATIC)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The field which annotated by RegObject must be static.", element);
                }

                var owner = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
                var field = element.getSimpleName().toString();
                items.computeIfAbsent(owner, key -> new ArrayList<>()).add(field);
            }
        } else {
            save();
        }
        return false;
    }

    private void save() {
        FileObject fileObject = createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "META-INF/data/RegObject.json");
        try (Writer writer = fileObject.openWriter()) {
            writer.write(JsonUtils.gson().toJson(items));
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
