package engine.mod.annotation.processor;

import engine.mod.ModMetadata;
import engine.mod.annotation.Mod;
import engine.mod.metadata.ModMetadataUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("engine.mod.annotation.Mod")
public class ModProcessor extends AbstractProcessor {

    private boolean foundMod = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(Mod.class)) {
                if (foundMod) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Too many mod annotations found.", element);
                    return true;
                }

                saveMetadata((TypeElement) element);
                foundMod = true;
            }
        }
        return false;
    }

    private void saveMetadata(TypeElement element) {
        Mod mod = element.getAnnotation(Mod.class);
        if (!mod.generateMetadata()) {
            return;
        }

        try (Writer writer = ProcessorUtils.createFile(processingEnv, StandardLocation.CLASS_OUTPUT,
                ModMetadataUtils.METADATA_NAME).openWriter()) {
            ModMetadataUtils.toJson(ModMetadata.fromAnnotation(mod, element.getQualifiedName().toString()), writer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
