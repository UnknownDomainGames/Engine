package unknowndomain.engine.mod.annotation.processing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import unknowndomain.engine.mod.annotation.AutoRegister;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.createFile;
import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.hasModifier;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AutoRegisterProcessor extends AbstractProcessor {

    private static final String CLASS_NAME = AutoRegister.class.getName();

    private final JsonArray autoRegisterItems = new JsonArray();

    private TypeMirror registryEntryTypeMirror;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(CLASS_NAME);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        registryEntryTypeMirror = processingEnv.getElementUtils().getTypeElement(RegistryEntry.class.getName()).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(AutoRegister.class)) {
                if (element instanceof TypeElement) {
                    JsonObject item = new JsonObject();
                    item.addProperty("kind", "CLASS");
                    item.addProperty("name", ((TypeElement) element).getQualifiedName().toString());
                    autoRegisterItems.add(item);
                } else if (element instanceof VariableElement) {
                    if (!processingEnv.getTypeUtils().isAssignable(element.asType(), registryEntryTypeMirror)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot auto register field which type isn't RegistryEntry or its sub class.", element);
                        continue;
                    }

                    if (!hasModifier(element, Modifier.STATIC)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot auto register non static field.", element);
                        continue;
                    }

                    JsonObject item = new JsonObject();
                    item.addProperty("kind", "FIELD");
                    item.addProperty("owner", ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString());
                    item.addProperty("name", element.getSimpleName().toString());
                    item.addProperty("type", element.asType().toString());
                    autoRegisterItems.add(item);
                }
            }
        } else {
            save();
        }
        return false;
    }

    private void save() {
        FileObject fileObject = createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "META-INF/cache/AutoRegister.json");
        try (Writer writer = fileObject.openWriter()) {
            writer.write(autoRegisterItems.toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
