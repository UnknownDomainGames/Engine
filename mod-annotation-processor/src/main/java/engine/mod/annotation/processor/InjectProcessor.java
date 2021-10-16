package engine.mod.annotation.processor;

import com.google.inject.Inject;
import engine.util.JsonUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("com.google.inject.Inject")
public class InjectProcessor extends AbstractProcessor {

    private final List<String> items = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            save();
        } else {
            for (Element element : roundEnv.getElementsAnnotatedWith(Inject.class)) {
                if (!(element instanceof VariableElement)) {
                    continue;
                }

                if (!ProcessorUtils.hasModifier(element, Modifier.STATIC)) {
                    continue;
                }

                items.add(((TypeElement) element.getEnclosingElement()).getQualifiedName().toString());
            }
        }
        return false;
    }

    private void save() {
        try (Writer writer = ProcessorUtils.createFile(processingEnv, StandardLocation.CLASS_OUTPUT,
                "META-INF/data/Inject.json").openWriter()) {
            writer.write(JsonUtils.gson().toJson(items));
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
