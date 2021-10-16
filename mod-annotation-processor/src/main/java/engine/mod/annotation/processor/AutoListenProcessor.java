package engine.mod.annotation.processor;

import com.google.gson.JsonArray;
import engine.mod.annotation.AutoListen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("engine.mod.annotation.AutoListen")
public class AutoListenProcessor extends AbstractProcessor {

    private final JsonArray items = new JsonArray();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            save();
        } else {
            for (var element : roundEnv.getElementsAnnotatedWith(AutoListen.class)) {
                items.add(((TypeElement) element).getQualifiedName().toString());
            }
        }
        return false;
    }

    private void save() {
        try (var writer = ProcessorUtils.createFile(processingEnv, StandardLocation.CLASS_OUTPUT,
                "META-INF/data/AutoListen.json").openWriter()) {
            writer.write(items.toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
