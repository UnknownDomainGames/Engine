package unknowndomain.engine.mod.annotation.processing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import unknowndomain.engine.mod.annotation.Inject;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.createFile;
import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.hasModifier;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class InjectProcessor extends AbstractProcessor {

    private static final String CLASS_NAME = Inject.class.getName();

    private final JsonArray injectItems = new JsonArray();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(CLASS_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(Inject.class)) {
                if (element instanceof VariableElement) {
                    if (!hasModifier(element, Modifier.STATIC)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot inject non static field.", element);
                        continue;
                    }

                    if (hasModifier(element, Modifier.FINAL)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot inject final field.", element);
                        continue;
                    }

                    JsonObject item = new JsonObject();
                    item.addProperty("owner", ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString());
                    item.addProperty("name", element.getSimpleName().toString());
                    item.addProperty("type", element.asType().toString());
                    injectItems.add(item);
                }
            }
        } else {
            save();
        }
        return false;
    }

    private void save() {
        FileObject fileObject = createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "META-INF/cache/Inject.json");
        try (Writer writer = fileObject.openWriter()) {
            writer.write(injectItems.toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
