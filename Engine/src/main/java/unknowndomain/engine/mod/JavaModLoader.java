package unknowndomain.engine.mod;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;

import unknowndomain.engine.api.Platform;
import unknowndomain.engine.api.mod.ModContainer;
import unknowndomain.engine.api.mod.ModLoader;

public class JavaModLoader implements ModLoader {

    private final Path path;

    private Map<String, ModContainer> mods;

    public JavaModLoader(Path path) {
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

                InputStream inputStream = jarFile.getInputStream(entry);
                Reader reader = new InputStreamReader(inputStream, "utf-8");
                JsonObject jo = new JsonParser().parse(reader).getAsJsonObject();
                SimpleModMetadata meta = new SimpleModMetadata();

                String version;
                if (!jo.has("version")) {
                    Platform.getLogger().warn("mod %s does not provide its version! Contact the mod authors to correct it.", modId);
                    Platform.getLogger().warn("Considering version of mod %s as 1.0", modId);
                    version = "1.0";
                } else {
                    version = jo.get("version").getAsString();
                }
                JavaModContainer container = new JavaModContainer(modId, version, mod);
                ModClassLoader loader = new ModClassLoader(container, Thread.currentThread().getContextClassLoader());
                loader.addPath(mod);
                container.setClassLoader(loader);
                if(!jo.has("mainClass")){
                    Platform.getLogger().warn("mod %s does not identify its main class! Contact the mod authors to correct it.", modId);
                    return null;
                }else{
                    String mainclazz = jo.get("mainClass").getAsString();
                    Class mainClazzType = loader.loadClass(mainclazz);
                    Object mainInstance = mainClazzType.newInstance();
                    container.setInstance(mainInstance);
                }
                if (jo.has("name")) {
                    meta.setName(jo.get("name").getAsString());
                }
                if (jo.has("description")) {
                    meta.setDescription(jo.get("description").getAsString());
                }
                if (jo.has("url")) {
                    meta.setUrl(jo.get("url").getAsString());
                }
                if (jo.has("logoFile")) {
                    meta.setLogoFile(jo.get("logoFile").getAsString());
                }
                if (jo.has("authors")) {
                    List<String> authors = new ArrayList<>();
                    for (JsonElement je : jo.getAsJsonArray("authors")) {
                        if (je.isJsonPrimitive()) {
                            authors.add(je.getAsString());
                        }
                    }
                    meta.setAuthors(authors);
                }
                container.setMetadata(meta);
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
        if(modIdMap.isEmpty()){
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
