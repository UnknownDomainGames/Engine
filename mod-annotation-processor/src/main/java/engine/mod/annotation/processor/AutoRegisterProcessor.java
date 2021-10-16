package engine.mod.annotation.processor;

import com.google.gson.reflect.TypeToken;
import engine.mod.annotation.AutoRegister;
import engine.mod.annotation.data.AutoRegisterItem;
import engine.registry.Registrable;
import engine.util.JsonUtils;

import javax.annotation.processing.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("engine.mod.annotation.AutoRegister")
public class AutoRegisterProcessor extends AbstractProcessor {

    private final List<AutoRegisterItem> items = new ArrayList<>();
    private TypeMirror registryEntryTypeMirror;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        registryEntryTypeMirror = processingEnv.getElementUtils().getTypeElement(Registrable.class.getName()).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            save();
        } else {
            for (var element : roundEnv.getElementsAnnotatedWith(AutoRegister.class)) {
                if (element instanceof TypeElement) {
                    var owner = ((TypeElement) element).getQualifiedName().toString();
                    items.add(new AutoRegisterItem(owner));
                } else if (element instanceof VariableElement) {
                    if (!ProcessorUtils.isAssignableIgnoreGeneric(processingEnv.getTypeUtils(), registryEntryTypeMirror, element.asType())) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot auto register field which type isn't RegistryEntry or its sub class.", element);
                        continue;
                    }

                    if (!ProcessorUtils.hasModifier(element, Modifier.STATIC)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot auto register non static field.", element);
                        continue;
                    }

                    var owner = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
                    var type = element.asType().toString();
                    var name = element.getSimpleName().toString();
                    items.add(new AutoRegisterItem(owner, type, name));
                }
            }
        }
        return false;
    }

    private void save() {
        try (Writer writer = ProcessorUtils.createFile(processingEnv, StandardLocation.CLASS_OUTPUT,
                "META-INF/data/AutoRegister.json").openWriter()) {
            writer.write(JsonUtils.gson().toJson(items, new TypeToken<List<AutoRegisterItem>>() {
            }.getType()));
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }
}
