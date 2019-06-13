package unknowndomain.engine.mod.impl;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.source.AssetSource;
import unknowndomain.engine.mod.ModAssets;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class EngineModAssets implements ModAssets {

    private final AssetSource source;

    public EngineModAssets(AssetSource source){
        this.source = source;
    }

    @Nullable
    @Override
    public Path get(String first) {
        return source.toPath(AssetPath.of(fixExtraAssets(first)));
    }

    @Nullable
    @Override
    public Path get(String first, String... more) {
        return source.toPath(AssetPath.of(fixExtraAssets(first), more));
    }

    @Override
    public InputStream openStream(String first) throws IOException {
        return source.openStream(AssetPath.of(fixExtraAssets(first)));
    }

    @Override
    public InputStream openStream(String first, String... more) throws IOException {
        return source.openStream(AssetPath.of(fixExtraAssets(first), more));
    }

    @Override
    public boolean exists(String first) {
        return source.exists(AssetPath.of(fixExtraAssets(first)));
    }

    @Override
    public boolean exists(String first, String... more) {
        return source.exists(AssetPath.of(fixExtraAssets(first), more));
    }

    private String fixExtraAssets(String first) {
        return first.replaceFirst("^assets/", "").replaceFirst("^assets", "");
    }

    @Override
    public Stream<Path> list(String first) {
        Path path = get(first);
        if(path == null) return Stream.empty();
        try {
            return Files.isDirectory(path) ? Files.list(path) : Stream.empty();
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("cannot list files of path %s", path), e);
            return Stream.empty();
        }
    }

    @Override
    public Stream<Path> list(String first, String... more) {
        Path path = get(first, more);
        if(path == null) return Stream.empty();
        try {
            return Files.isDirectory(path) ? Files.list(path) : Stream.empty();
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("cannot list files of path %s", path), e);
            return Stream.empty();
        }
    }

    @Override
    public void copy(Path target, boolean forceCopying, String first) {
        if (Files.notExists(target)) {
            try {
                Files.createFile(target);
            } catch (IOException e) {
                Platform.getLogger().warn(String.format("Exception thrown when attempted to create file %s", target), e);
                return;
            }
        }
        Path source = get(first);
        if (source == null) {
            Platform.getLogger().warn(String.format("Source file not exists when copying file from %s to %s", first, target));
            return;
        }

        try {
            Files.copy(source, target, forceCopying ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES} : new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES});
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s to %s", source, target), e);
        }
    }

    @Override
    public void copy(Path target, boolean forceCopying, String first, String... more) {
        if (Files.notExists(target)) {
            try {
                Files.createFile(target);
            } catch (IOException e) {
                Platform.getLogger().warn(String.format("Exception thrown when attempted to create file %s", target), e);
                return;
            }
        }
        Path source = get(first, more);
        if (source == null) {
            Platform.getLogger().warn(String.format("Source file not exists when copying file from %s to %s", Path.of(first, more), target));
            return;
        }

        try {
            Files.copy(source, target, forceCopying ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES} : new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES});
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s to %s", source, target), e);
        }
    }

    @Override
    public void copy(OutputStream output, String first) {
        try (var input = openStream(first)) {
            input.transferTo(output);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    @Override
    public void copy(OutputStream output, String first, String... more) {
        try (var input = openStream(first, more)) {
            input.transferTo(output);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    @Override
    public void copy(Writer writer, String first) {
        try (var reader = new FileReader(get(first).toFile())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    @Override
    public void copy(Writer writer, String first, String... more) {
        try (var reader = new FileReader(get(first, more).toFile())) {
            reader.transferTo(writer);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }
}
