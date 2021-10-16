package engine.mod.annotation.processing;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import engine.mod.Dependency;
import engine.mod.DependencyType;
import engine.mod.InstallationType;
import engine.mod.ModMetadata;
import engine.mod.annotation.Mod;
import engine.mod.metadata.ModMetadataUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static engine.mod.InstallationType.CLIENT_REQUIRED;
import static engine.mod.annotation.processing.ProcessingUtils.createFile;
import static engine.mod.annotation.processing.ProcessingUtils.getAnnotationValues;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ModProcessor extends AbstractProcessor {

    private boolean foundMod = false;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Mod.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(Mod.class)) {
                if (foundMod) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Too many mods.", element);
                }

                saveMetadata((TypeElement) element);
                foundMod = true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void saveMetadata(TypeElement element) {
        Map<String, Object> values = getAnnotationValues(element, Mod.class);
        if (!(boolean) values.getOrDefault("generateMetadata", true)) {
            return;
        }

        FileObject fileObject = createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "metadata.json");
        if(fileObject == null) {
            return;
        }

        try (Writer writer = fileObject.openWriter()) {
            ModMetadata metadata = ModMetadata.builder()
                    .id((String) values.get("id"))
                    .version((String) values.getOrDefault("version", "1.0.0"))
                    .mainClass(element.getQualifiedName().toString())
                    .name((String) values.getOrDefault("name", ""))
                    .installationType(InstallationType.valueOf(values.getOrDefault("installationType", CLIENT_REQUIRED.name()).toString()))
                    .description((String) values.getOrDefault("description", ""))
                    .license((String) values.getOrDefault("license", ""))
                    .url((String) values.getOrDefault("url", ""))
                    .logo((String) values.getOrDefault("logo", ""))
                    .authors((String[]) values.getOrDefault("authors", new String[0]))
                    .credits((String[]) values.getOrDefault("authors", new String[0]))
                    .permissions((String[]) values.getOrDefault("permissions", new String[0]))
                    .dependencies(createDependencyList((List<AnnotationMirror>) values.get("dependencies")))
                    .elements(createElementMap((List<AnnotationMirror>) values.get("customElements")))
                    .build();
            ModMetadataUtils.toJson(metadata, writer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Dependency> createDependencyList(List<AnnotationMirror> dependencies) {
        List<Dependency> list = new ArrayList<>();
        if (dependencies == null)
            return list;
        for (AnnotationMirror dependency : dependencies) {
            Map<String, Object> values = getAnnotationValues(dependency);
            list.add(new Dependency((String) values.get("id"),
                    (String) values.getOrDefault("version", "*"),
                    DependencyType.valueOf((String) values.getOrDefault("type", DependencyType.REQUIRED.name()))));
        }
        return list;
    }

    private Map<String, JsonElement> createElementMap(List<AnnotationMirror> elements) {
        Map<String, JsonElement> map = new HashMap<>();
        if (elements == null)
            return map;
        for (AnnotationMirror element : elements) {
            Map<String, Object> values = getAnnotationValues(element);
            map.put((String) values.get("key"), JsonParser.parseString((String) values.get("value")));
        }
        return map;
    }
}
