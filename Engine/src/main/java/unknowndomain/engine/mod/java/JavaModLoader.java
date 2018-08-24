package unknowndomain.engine.mod.java;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.harvester.HarvestedAnnotation;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;

public class JavaModLoader implements ModLoader {

    @Override
    public ModContainer load(LoadableMod loadableMod) {
        Path source = loadableMod.getSource();
        ModMetadata metadata = loadableMod.getMetadata();
        try {
            JavaModContainer container = new JavaModContainer(metadata.getModId(), source);
            ModClassLoader classLoader = new ModClassLoader(container, Thread.currentThread().getContextClassLoader());

            HarvestedInfo harvestedInfo = new HarvestedInfo(source);
            harvestedInfo.startHarvest();

            HarvestedAnnotation modAnno = getModAnno(metadata.getModId(), harvestedInfo.getHarvestedAnnotations(Mod.class));
            if (modAnno == null) {
                throw new ModLoadException(String.format("Can't find the main class for mod %s!", metadata.getModId()));
            }

            Class<?> mainClass = Class.forName(modAnno.getOwnerType().getClassName(), true,
                    classLoader);
            Object instance = mainClass.newInstance();

            container.initialize(classLoader, metadata, harvestedInfo, instance);

            return container;
        } catch (IOException e) {
            throw new ModLoadException(String.format("Can't load mod %s!", metadata.getModId()), e);
        } catch (ClassNotFoundException e) {
            throw new ModLoadException(String.format("Can't find the main class for mod %s!", metadata.getModId()), e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ModLoadException(String.format("Can't instantiate the main class for mod %s!", metadata.getModId()), e);
        } catch (Exception e) {
            throw new ModLoadException(String.format("Catch unknown exception when loading mod %s !", metadata.getModId()), e);
        }
    }

    private HarvestedAnnotation getModAnno(String modId, Collection<HarvestedAnnotation> harvestedAnnos) {
        for (HarvestedAnnotation anno : harvestedAnnos) {
            if (modId.equals(anno.getHarvestedInfo().get("value")))
                return anno;
        }
        return null;
    }
}
