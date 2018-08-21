package unknowndomain.engine.mod.java;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import unknowndomain.engine.Platform;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.ModStartLoadEvent;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.harvester.HarvestedAnnotation;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;
import unknowndomain.engine.mod.metadata.FileModMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class JavaModLoader implements ModLoader {
    private EventBus eventBus;
    private final Path path;

    private Map<String, ModContainer> mods;

    public JavaModLoader(EventBus eventBus, Path path) {
        this.eventBus = eventBus;
        Validate.notNull(path);
        if (!Files.exists(path))
            throw new IllegalArgumentException(path.toAbsolutePath() + " don't exist.");
        if (!Files.isDirectory(path))
            throw new IllegalArgumentException(path.toAbsolutePath() + " isn't a directory.");

        this.path = path;
        this.mods = new HashMap<>();
        modIdMap = new HashMap<>();
    }


    private Map<String, Path> modIdMap;

    @Override
    public ModContainer loadMod(String modId) {
        if (modIdMap.isEmpty()) {
            initModidForwardMap();
        }
        if (modIdMap.containsKey(modId)) {
            Path mod = modIdMap.get(modId);
            try (JarFile jarFile = new JarFile(mod.toFile())) {
                JarEntry entry = jarFile.getJarEntry("metadata.json");

                ModMetadata metadata = FileModMetadata.create(jarFile.getInputStream(entry));
                eventBus.post(new ModStartLoadEvent(modId, metadata));
                JavaModContainer container = new JavaModContainer(modId, mod);

                ModClassLoader loader = new ModClassLoader(container, Thread.currentThread().getContextClassLoader());

                loader.addPath(mod);
                container.setClassLoader(loader);
                container.setMetadata(metadata);

                HarvestedInfo harvestedInfo = new HarvestedInfo(mod);
                harvestedInfo.startHarvest();
                Collection<HarvestedAnnotation> annos = harvestedInfo.getHarvestedAnnotations(Mod.class);
                if (annos.isEmpty()) {
                    Platform.getLogger().warn(String.format("cannot find the main class for mod %s!", modId));
                    return null;
                }

                Class<?> mainClass = Class.forName(annos.toArray(new HarvestedAnnotation[annos.size()])[0].getOwnerType().getClassName(), true, loader);
                Object instance = mainClass.newInstance();
                container.setInstance(instance);

                mods.put(modId, container);
                return container;
            } catch (IOException e) {
                Platform.getLogger().warn(String.format("cannot load mod %s!", modId), e);
            } catch (ClassNotFoundException e) {
                Platform.getLogger().warn(String.format("cannot find the main class for mod %s!", modId), e);
            } catch (IllegalAccessException | InstantiationException e) {
                Platform.getLogger().warn(String.format("cannot instantiate the main class for mod %s!", modId), e);
            }
        }

        return null;
    }

    private void initModidForwardMap() {
        try {
            for (Path mod : Files.list(path).collect(Collectors.toList())) {
                if (!"jar".equals(FilenameUtils.getExtension(mod.toFile().getAbsolutePath()))) {
                    Platform.getLogger().debug("file %s is probably not a mod file, skip it", mod);
                } else {
                    try (JarFile jarFile = new JarFile(mod.toFile())) {
                        JarEntry entry = jarFile.getJarEntry("metadata.json");
                        if (entry == null) {
                            Platform.getLogger().warn("mod file %s contains no mod metadata file at root dir! Contact the mod authors to correct it.", mod);
                        } else {
                            InputStream inputStream = jarFile.getInputStream(entry);
                            Reader reader = new InputStreamReader(inputStream, "utf-8");
                            JsonObject jo = new JsonParser().parse(reader).getAsJsonObject();
                            if (!jo.has("modid")) {
                                Platform.getLogger().warn("metadata of mod file %s does not provide its modid! Contact the mod authors to correct it.", mod);
                            } else {
                                modIdMap.put(jo.get("modid").getAsString(), mod);
                            }
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        Platform.getLogger().warn("cannot open mod", e);
                    }
                }
            }
        } catch (IOException e) {
            Platform.getLogger().warn("cannot load mods in the path!", e);
        }
    }

    @Override
    public ModContainer getModContainer(String modId) {
        if (!mods.containsKey(modId)) {
            ModContainer mc = loadMod(modId);
            if (mc == null) return null;
            mods.put(modId, mc);
        }
        return mods.get(modId);
    }

    @Override
    public boolean hasMod(String modId) {
        if (modIdMap.isEmpty()) {
            initModidForwardMap();
        }
        return modIdMap.containsKey(modId);
    }

    public Map<String, ModContainer> getLoadedMods() {
        return mods;
    }

    public List<ModContainer> getLoadedModsList() {
        return new ArrayList<>(mods.values());
    }

}
