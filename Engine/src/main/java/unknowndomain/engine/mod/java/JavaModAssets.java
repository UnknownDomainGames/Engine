package unknowndomain.engine.mod.java;

import unknowndomain.engine.mod.ModAssets;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class JavaModAssets implements ModAssets {

    private FileSystem fileSystem;

    public JavaModAssets(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public Path get(String first) {
        return fileSystem.getPath(first);
    }

    @Override
    public Path get(String first, String... more) {
        return fileSystem.getPath(first, more);
    }

    @Override
    public InputStream openStream(String first) {
        return null;
    }

    @Override
    public InputStream openStream(String first, String... more) {
        return null;
    }

    @Override
    public boolean exists(String first) {
        return false;
    }

    @Override
    public boolean exists(String first, String... more) {
        return false;
    }

    @Override
    public void copy(Path target, String first) {

    }

    @Override
    public void copy(Path target, String first, String... more) {

    }

    @Override
    public void copy(OutputStream output, String first) {

    }

    @Override
    public void copy(OutputStream output, String first, String... more) {

    }

    @Override
    public void copy(Writer writer, String first) {

    }

    @Override
    public void copy(Writer writer, String first, String... more) {

    }
}
