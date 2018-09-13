package unknowndomain.engine.mod.java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.google.common.collect.Maps;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.Engine;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.JavaModContainer;
import unknowndomain.engine.mod.java.harvester.HarvestedAnnotation;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;

/**
 * intermediate loader class
 */
public class JavaModLoader implements ModLoader {
    private Map<String, JavaModContainer> cache = Maps.newHashMap();
    private ModStore store;

    public JavaModLoader(ModStore st) {
        store = st;
    }

    public ModContainer load(ModIdentifier identifier) {
        if (!store.exists(identifier))
            return null;
        Path source = store.path(Validate.notNull(identifier));
        try (JarFile jarFile = new JarFile(source.toFile())) {

            ZipEntry entry = jarFile.getEntry("metadata.json");
            if (entry == null) {
                Engine.getLogger().warn("metadata.json isn't exists. Path: " + source.toAbsolutePath().toString());
                return null;
            }

            ModMetadata metadata = ModMetadata.fromJsonStream(jarFile.getInputStream(entry));

            Logger log = LoggerFactory.getLogger(metadata.getId());
            ModClassLoader classLoader = new ModClassLoader(log, source,
                    Thread.currentThread().getContextClassLoader());

            metadata.getDependencies().stream().forEach(d -> {
                if (d.getLoadOrder() == ModDependencyEntry.LoadOrder.REQUIRED) {
                    JavaModContainer dep = cache.get(d.getModId());
                    if (dep == null) {
                        throw new IllegalStateException("");
                    }
                    classLoader.getDependencyClassLoaders().add(dep.getClassLoader());
                }
            });

            HarvestedInfo info = HarvestedInfo.harvest(jarFile);
            Collection<HarvestedAnnotation> modAnnos = info.getHarvestedAnnotations(Mod.class);
            if (modAnnos.isEmpty()) {
                Engine.getLogger().warn(String.format("cannot find the main class for mod %s!", metadata.getId()));
                classLoader.close();
                return null;
            }

            HarvestedAnnotation modAnno = getModAnno(metadata.getId(), info.getHarvestedAnnotations(Mod.class));
            if (modAnno == null) {
                throw new ModLoadException(String.format("Can't find the main class for mod %s!", metadata.getId()));
            }

            Class<?> mainClass = Class.forName(modAnno.getOwnerType().getClassName(), true,
                    classLoader);
            Object instance = mainClass.newInstance(); // TODO: Inject and register listener

            JavaModContainer container = new JavaModContainer(metadata.getId());
            container.initialize(classLoader, metadata, info, instance);

            return container;
        } catch (IOException e) {
            Engine.getLogger().warn(String.format("cannot load mod %s!", source.toString()), e);
        } catch (ClassNotFoundException e) {
            Engine.getLogger().warn(String.format("cannot find the main class for mod %s!", source.toString()), e);
        } catch (IllegalAccessException | InstantiationException e) {
            Engine.getLogger().warn(String.format("cannot instantiate the main class for mod %s!", source.toString()),
                    e);
        }
        return null;
    }

    private HarvestedAnnotation getModAnno(String modId, Collection<HarvestedAnnotation> harvestedAnnos) {
        for (HarvestedAnnotation anno : harvestedAnnos) {
            if (modId.equals(anno.getHarvestedInfo().get("value")))
                return anno;
        }
        return null;
    }
}
