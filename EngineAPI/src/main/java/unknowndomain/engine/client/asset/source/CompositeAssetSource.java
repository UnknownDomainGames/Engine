package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.client.asset.AssetPath;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CompositeAssetSource implements AssetSource {

    private final Collection<Source> sources;

    public CompositeAssetSource(Collection<Path> paths, String root, ClassLoader classLoader) throws IOException {
        List<Source> sources = new ArrayList<>();
        for (Path path : paths) {
            if (Files.isDirectory(path)) {
                sources.add(new DirectorySource(path.resolve(root)));
            } else {
                sources.add(new FileSystemSource(FileSystems.newFileSystem(path, classLoader), root));
            }
        }
        this.sources = List.copyOf(sources);
    }

    @Override
    public Optional<Path> toPath(AssetPath path) {
        var fullPath = path.getRealPath();
        for (Source source : sources) {
            Path _path = source.toPath(fullPath);
            if (Files.exists(_path)) {
                return Optional.of(_path);
            }
        }
        return Optional.empty();
    }

    private interface Source {
        Path toPath(String path);
    }

    private class DirectorySource implements Source {

        private final Path root;

        public DirectorySource(Path root) {
            this.root = root;
        }

        @Override
        public Path toPath(String path) {
            return root.resolve(path);
        }
    }

    private class FileSystemSource implements Source {

        private final FileSystem fileSystem;
        private final Path root;

        public FileSystemSource(FileSystem fileSystem, String root) {
            this.fileSystem = fileSystem;
            this.root = fileSystem.getPath(root);
        }

        @Override
        public Path toPath(String path) {
            return root.resolve(path);
        }
    }
}
