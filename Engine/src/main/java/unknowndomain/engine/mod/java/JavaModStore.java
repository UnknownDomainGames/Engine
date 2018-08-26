package unknowndomain.engine.mod.java;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.Engine;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.harvester.HarvestedAnnotation;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;
import unknowndomain.engine.util.Transfer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JavaModStore implements ModStore {
    /**
     * Since we have already decide all the mod will be downloaded to local.
     * <p>The loader could confirm that it can only load from disk</p>
     * <p>Otherwise, we will have a much pure api: (InputStream)->ModContainer</p>
     */
    private final Path root;

    public JavaModStore(Path root) {
        this.root = root;
    }

    /**
     * There might be different structure to store mod.
     */
    protected Path resolve(ModIdentifier identifier) {
        return root.resolve(identifier.getGroup()).resolve(identifier.getId() + "-" + identifier.getVersion() + ".jar");
    }

    @Override
    public boolean exists(@NonNull ModIdentifier identifier) {
        Path source = resolve(Validate.notNull(identifier));
        return Files.exists(source);
    }

    @Override
    public ModContainer load(@NonNull ModIdentifier identifier) {
        Path source = resolve(Validate.notNull(identifier));
        try (JarFile jarFile = new JarFile(source.toFile())) {
            ZipEntry entry = jarFile.getEntry("metadata.json");
            if (entry == null) {
                Engine.getLogger().warn("metadata.json isn't exists. Path: " + source.toAbsolutePath().toString());
                return null;
            }

            ModMetadata metadata = ModMetadata.fromJsonStream(jarFile.getInputStream(entry));

            JavaModContainer container = new JavaModContainer(metadata.getId());
            ModClassLoader classLoader = new ModClassLoader(container, source, Thread.currentThread().getContextClassLoader());

            HarvestedInfo harvestedInfo = new HarvestedInfo(source);
            harvestedInfo.startHarvest();
            Collection<HarvestedAnnotation> modAnnos = harvestedInfo.getHarvestedAnnotations(Mod.class);
            if (modAnnos.isEmpty()) {
                Engine.getLogger().warn(String.format("cannot find the main class for mod %s!", metadata.getId()));
                return null;
            }

            Class<?> mainClass = Class.forName(
                    modAnnos.toArray(new HarvestedAnnotation[modAnnos.size()])[0].getOwnerType().getClassName(), true,
                    classLoader);
            Object instance = mainClass.newInstance();

            container.initialize(classLoader, metadata, harvestedInfo, instance);

            return container;
        } catch (IOException e) {
            Engine.getLogger().warn(String.format("cannot load mod %s!", source.toString()), e);
        } catch (ClassNotFoundException e) {
            Engine.getLogger().warn(String.format("cannot find the main class for mod %s!", source.toString()), e);
        } catch (IllegalAccessException | InstantiationException e) {
            Engine.getLogger().warn(String.format("cannot instantiate the main class for mod %s!", source.toString()), e);
        }
        return null;
    }

    @Override
    public void store(@Nonnull ModIdentifier identifier, InputStream stream) {
        Path resolve = resolve(identifier);
        try {
            long size = identifier instanceof ModRepository.RemoteModMetadata ? ((ModRepository.RemoteModMetadata) identifier).getSize() : -1;
            Transfer transfer = new Transfer(Channels.newChannel(stream), FileChannel.open(resolve, StandardOpenOption.WRITE), size);
            transfer.call(); // TODO: monitor transfer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
