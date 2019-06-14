package unknowndomain.engine.mod.impl;

import org.apache.commons.lang3.StringUtils;
import unknowndomain.engine.mod.ModAssets;
import unknowndomain.engine.mod.ModContainer;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class AbstractModAssets implements ModAssets {

    private ModContainer mod;

    public ModContainer getMod() {
        return mod;
    }

    public void setMod(ModContainer mod) {
        this.mod = mod;
    }

    public Optional<InputStream> openStream(String first) throws IOException {
        var path = get(first);
        if (path.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Files.newInputStream(path.get()));
    }

    public Optional<InputStream> openStream(String first, String... more) throws IOException {
        var path = get(first, more);
        if (path.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Files.newInputStream(path.get()));
    }

    @Nonnull
    public Stream<Path> list(String first) {
        var path = get(first);
        if (path.isEmpty()) return Stream.empty();
        try {
            return Files.isDirectory(path.get()) ? Files.list(path.get()) : Stream.empty();
        } catch (IOException e) {
            mod.getLogger().warn(format("cannot list files of path %s", path), e);
            return Stream.empty();
        }
    }

    @Nonnull
    public Stream<Path> list(String first, String... more) {
        var path = get(first, more);
        if (path.isEmpty()) return Stream.empty();
        try {
            return Files.isDirectory(path.get()) ? Files.list(path.get()) : Stream.empty();
        } catch (IOException e) {
            mod.getLogger().warn(format("cannot list files of path %s", path), e);
            return Stream.empty();
        }
    }

    public void copy(Path target, boolean forceCopying, String first) {
        var source = get(first);
        if (source.isEmpty()) {
            mod.getLogger().warn(format("Source file not exists when copying file from %s to %s", first, target));
            return;
        }
        copy(target, forceCopying, source.get());
    }

    public void copy(Path target, boolean forceCopying, String first, String... more) {
        var source = get(first, more);
        if (source.isEmpty()) {
            mod.getLogger().warn(format("Source file not exists when copying file from %s to %s", first + "/" + StringUtils.join(more, '/'), target));
            return;
        }

        copy(target, forceCopying, source.get());
    }

    private void copy(Path target, boolean forceCopying, Path source) {
        try {
            Files.copy(source, target, forceCopying ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES} : new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES});
        } catch (IOException e) {
            mod.getLogger().warn(format("Exception thrown when copying file from %s to %s", source, target), e);
        }
    }

    public void copy(OutputStream output, String first) {
        try (var input = openStream(first).get()) {
            input.transferTo(output);
        } catch (IOException e) {
            mod.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    public void copy(OutputStream output, String first, String... more) {
        try (var input = openStream(first, more).get()) {
            input.transferTo(output);
        } catch (IOException e) {
            mod.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    public void copy(Writer writer, String first) {
        try (var reader = new FileReader(get(first).get().toFile())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            mod.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    public void copy(Writer writer, String first, String... more) {
        try (var reader = new FileReader(get(first, more).get().toFile())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            mod.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }
}
