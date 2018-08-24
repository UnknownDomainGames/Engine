package unknowndomain.engine.mod.java;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;

import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.harvester.HarvestedAnnotation;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;

public class JavaModLoader implements ModLoader {

    public JavaModLoader() {
    }

    @Override
    public ModContainer load(LoadableMod loadableMod) {
        Path source = loadableMod.getSource();
        ModMetadata metadata = loadableMod.getMetadata();
        try {
            JavaModContainer container = new JavaModContainer(metadata.getModId(), source);
            ModClassLoader classLoader = new ModClassLoader(container, Thread.currentThread().getContextClassLoader());

            HarvestedInfo harvestedInfo = new HarvestedInfo(source);
            harvestedInfo.startHarvest();
            Collection<HarvestedAnnotation> modAnnos = harvestedInfo.getHarvestedAnnotations(Mod.class);
            if (modAnnos.isEmpty()) {
                Platform.getLogger().warn(String.format("cannot find the main class for mod %s!", metadata.getModId()));
                return null;
            }

            Class<?> mainClass = Class.forName(
                    modAnnos.toArray(new HarvestedAnnotation[modAnnos.size()])[0].getOwnerType().getClassName(), true,
                    classLoader);
            Object instance = mainClass.newInstance();

            container.initialize(classLoader, metadata, harvestedInfo, instance);

            return container;
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("cannot load mod %s!", metadata.getModId()), e);
        } catch (ClassNotFoundException e) {
            Platform.getLogger().warn(String.format("cannot find the main class for mod %s!",  metadata.getModId()), e);
        } catch (IllegalAccessException | InstantiationException e) {
            Platform.getLogger().warn(String.format("cannot instantiate the main class for mod %s!",  metadata.getModId()), e);
        }

        return null;
    }
}
