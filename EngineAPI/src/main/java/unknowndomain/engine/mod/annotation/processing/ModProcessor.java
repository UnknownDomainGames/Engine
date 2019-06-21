package unknowndomain.engine.mod.annotation.processing;

import unknowndomain.engine.mod.DependencyType;
import unknowndomain.engine.mod.ModDependencyEntry;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.annotation.Mod;
import unknowndomain.engine.mod.misc.DefaultModMetadata;
import unknowndomain.engine.mod.util.JsonModMetadataUtils;

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

import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.createFile;
import static unknowndomain.engine.mod.annotation.processing.ProcessingUtils.getAnnotationValues;

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
                if (!(element instanceof TypeElement))
                    continue;

                if (foundMod) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Too many mods.", element);
                }

                saveMetadata((TypeElement) element);
                foundMod = true;
            }
        }
        return false;
    }

    private void saveMetadata(TypeElement element) {
        Map<String, Object> values = getAnnotationValues(element, Mod.class);
        if (!(boolean) values.getOrDefault("generateMetadata", true)) {
            return;
        }

        FileObject fileObject = createFile(processingEnv, StandardLocation.CLASS_OUTPUT, "metadata.json");

        try (Writer writer = fileObject.openWriter()) {
            ModMetadata metadata = DefaultModMetadata.builder()
                    .id((String) values.get("id"))
                    .version((String) values.getOrDefault("version", "1.0.0"))
                    .mainClass(element.getQualifiedName().toString())
                    .name((String) values.getOrDefault("name", ""))
                    .description((String) values.getOrDefault("description", ""))
                    .license((String) values.getOrDefault("license", ""))
                    .url((String) values.getOrDefault("url", ""))
                    .logo((String) values.getOrDefault("logo", ""))
                    .authors(Arrays.asList((String[]) values.getOrDefault("authors", new String[0])))
                    .dependencies(createDependencyList((List<AnnotationMirror>) values.get("dependencies")))
                    .properties(createPropertyMap((List<AnnotationMirror>) values.get("properties")))
                    .build();
            writer.append(JsonModMetadataUtils.toJson(metadata).toString());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private List<ModDependencyEntry> createDependencyList(List<AnnotationMirror> dependencies) {
        List<ModDependencyEntry> list = new ArrayList<>();
        if (dependencies == null)
            return list;
        for (AnnotationMirror dependency : dependencies) {
            Map<String, Object> values = getAnnotationValues(dependency);
            list.add(new ModDependencyEntry((String) values.get("id"),
                    (String) values.getOrDefault("version", "*"),
                    DependencyType.valueOf((String) values.getOrDefault("type", DependencyType.REQUIRED.name()))));
        }
        return list;
    }

    private Map<String, String> createPropertyMap(List<AnnotationMirror> properties) {
        Map<String, String> map = new HashMap<>();
        if (properties == null)
            return map;
        for (AnnotationMirror property : properties) {
            Map<String, Object> values = getAnnotationValues(property);
            map.put((String) values.get("key"), (String) values.get("value"));
        }
        return map;
    }
}
