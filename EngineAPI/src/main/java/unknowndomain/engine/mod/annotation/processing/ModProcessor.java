package unknowndomain.engine.mod.annotation.processing;

import unknowndomain.engine.mod.Mod;
import unknowndomain.engine.mod.ModIdentifier;
import unknowndomain.engine.mod.impl.DefaultModDescriptor;
import unknowndomain.engine.mod.util.JsonModDescriptorSerializer;

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

import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.createFile;
import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.getAnnotationValue;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ModProcessor extends AbstractProcessor {

    private static final String CLASS_NAME = Mod.class.getName();

    private boolean hasMod = false;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(CLASS_NAME);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(Mod.class)) {
                if (!(element instanceof TypeElement))
                    continue;

                if (hasMod) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Too many mods.", element);
                }

                saveMetadata((TypeElement) element);
                hasMod = true;
            }
        }
        return false;
    }

    private void saveMetadata(TypeElement element) {
        FileObject fileObject = createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "metadata.json");

        try (Writer writer = fileObject.openWriter()) {
            ModIdentifier modIdentifier = ModIdentifier.from((String) getAnnotationValue(element, CLASS_NAME, "value").getValue());
            writer.append(JsonModDescriptorSerializer.toJson(DefaultModDescriptor.Builder.create().modId(modIdentifier.getId()).version(modIdentifier.getVersion()).mainClass(element.getQualifiedName().toString()).build()).toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
