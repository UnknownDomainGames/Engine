package engine.mod.annotation.processing;

import com.google.gson.JsonArray;
import engine.mod.annotation.AutoListen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AutoListenProcessor extends AbstractProcessor {

    private final JsonArray autoListenItems = new JsonArray();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(AutoListen.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(AutoListen.class)) {
                autoListenItems.add(((TypeElement) element).getQualifiedName().toString());
            }
        } else {
            save();
        }
        return false;
    }

    private void save() {
        FileObject fileObject = ProcessingUtils.createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "META-INF/data/AutoListen.json");
        try (Writer writer = fileObject.openWriter()) {
            writer.write(autoListenItems.toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
