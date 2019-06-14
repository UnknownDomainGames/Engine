package unknowndomain.engine.mod.impl;

import org.apache.commons.lang3.StringUtils;
import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.ModAssets;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class AbstractModAssets implements ModAssets {

    public InputStream openStream(String first) throws IOException {
        Path path = get(first);
        if (path == null) {
            return null;
        }
        return Files.newInputStream(path);
    }

    public InputStream openStream(String first, String... more) throws IOException {
        Path path = get(first, more);
        if (path == null) {
            return null;
        }
        return Files.newInputStream(path);
    }

    public Stream<Path> list(String first) {
        Path path = get(first);
        if (path == null) return Stream.empty();
        try {
            return Files.isDirectory(path) ? Files.list(path) : Stream.empty();
        } catch (IOException e) {
            Platform.getLogger().warn(format("cannot list files of path %s", path), e);
            return Stream.empty();
        }
    }

    public Stream<Path> list(String first, String... more) {
        Path path = get(first, more);
        if (path == null) return Stream.empty();
        try {
            return Files.isDirectory(path) ? Files.list(path) : Stream.empty();
        } catch (IOException e) {
            Platform.getLogger().warn(format("cannot list files of path %s", path), e);
            return Stream.empty();
        }
    }

    public void copy(Path target, boolean forceCopying, String first) {
        Path source = get(first);
        if (source == null) {
            Platform.getLogger().warn(format("Source file not exists when copying file from %s to %s", first, target));
            return;
        }
        copy(target, forceCopying, source);
    }

    public void copy(Path target, boolean forceCopying, String first, String... more) {
        Path source = get(first, more);
        if (source == null) {
            Platform.getLogger().warn(format("Source file not exists when copying file from %s to %s", first + "/" + StringUtils.join(more, '/'), target));
            return;
        }

        copy(target, forceCopying, source);
    }

    private void copy(Path target, boolean forceCopying, Path source) {
        try {
            Files.copy(source, target, forceCopying ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES} : new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES});
        } catch (IOException e) {
            Platform.getLogger().warn(format("Exception thrown when copying file from %s to %s", source, target), e);
        }
    }

    public void copy(OutputStream output, String first) {
        try (var input = openStream(first)) {
            input.transferTo(output);
        } catch (IOException e) {
            Platform.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    public void copy(OutputStream output, String first, String... more) {
        try (var input = openStream(first, more)) {
            input.transferTo(output);
        } catch (IOException e) {
            Platform.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    public void copy(Writer writer, String first) {
        try (var reader = new FileReader(get(first).toFile())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            Platform.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    public void copy(Writer writer, String first, String... more) {
        try (var reader = new FileReader(get(first, more).toFile())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            Platform.getLogger().warn(format("Exception thrown when copying file from %s", get(first)), e);
        }
    }
}
