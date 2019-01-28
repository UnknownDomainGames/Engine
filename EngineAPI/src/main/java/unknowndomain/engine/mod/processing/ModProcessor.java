package unknowndomain.engine.mod.processing;

import unknowndomain.engine.mod.Mod;
import unknowndomain.engine.mod.ModIdentifier;
import unknowndomain.engine.mod.impl.DefaultModDescriptor;
import unknowndomain.engine.mod.util.JsonModDescriptorSerializer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

public class ModProcessor extends AbstractProcessor {

    private final String MOD_CLASS_NAME = Mod.class.getName();

    private boolean hasMod = false;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(MOD_CLASS_NAME);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) { // 判断是否为最终轮
            for (Element element : roundEnv.getElementsAnnotatedWith(Mod.class)) {
                if (!(element instanceof TypeElement)) {
                    continue;
                }
                if (hasMod) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Too many mods.");
                }
                saveMetadata((TypeElement) element);
                hasMod = true;
            }
        }
        return false;
    }

    private AnnotationValue getAnnoValue(Element element, String annoTypeName, String key) {
        for (AnnotationMirror anno : element.getAnnotationMirrors()) {
            if (!annoTypeName.equals(anno.getAnnotationType().toString())) {
                continue;
            }
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : anno.getElementValues().entrySet()) {
                if (key.equals(entry.getKey().getSimpleName().toString())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private void saveMetadata(TypeElement element) {
        FileObject fileObject = null;
        try {
            fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "metadata.json");
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }

        try (Writer writer = fileObject.openWriter()) {
            ModIdentifier modIdentifier = ModIdentifier.from((String) getAnnoValue(element, MOD_CLASS_NAME, "value").getValue());
            writer.append(JsonModDescriptorSerializer.toJson(DefaultModDescriptor.Builder.create().modId(modIdentifier.getId()).version(modIdentifier.getVersion()).mainClass(element.getQualifiedName().toString()).build()).toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
