package nullengine.mod.java;

import nullengine.mod.impl.AbstractModAssets;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JavaModAssets extends AbstractModAssets {

    private final Collection<Source> sources;

    public JavaModAssets(Collection<Path> paths, ClassLoader classLoader) throws IOException {
        List<Source> sources = new ArrayList<>();
        for (Path path : paths) {
            if (Files.isDirectory(path)) {
                sources.add(new DirectorySource(path));
            } else {
                sources.add(new FileSystemSource(FileSystems.newFileSystem(path, classLoader)));
            }
        }
        this.sources = List.copyOf(sources);
    }

    @Override
    public Optional<Path> get(String first) {
        for (Source source : sources) {
            Path _path = source.toPath(first);
            if (Files.exists(_path)) {
                return Optional.of(_path);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Path> get(String first, String... more) {
        for (Source source : sources) {
            Path _path = source.toPath(first, more);
            if (Files.exists(_path)) {
                return Optional.of(_path);
            }
        }
        return Optional.empty();
    }

    private interface Source {
        Path toPath(String first);

        Path toPath(String first, String... more);
    }

    private class DirectorySource implements Source {

        private final Path root;

        public DirectorySource(Path root) {
            this.root = root;
        }

        @Override
        public Path toPath(String first) {
            return root.resolve(first);
        }

        @Override
        public Path toPath(String first, String... more) {
            return root.resolve(Path.of(first, more));
        }
    }

    private class FileSystemSource implements Source {

        private final FileSystem fileSystem;

        public FileSystemSource(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
        }

        @Override
        public Path toPath(String first) {
            return fileSystem.getPath(first);
        }

        @Override
        public Path toPath(String first, String... more) {
            return fileSystem.getPath(first, more);
        }
    }
}
