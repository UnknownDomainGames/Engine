package unknowndomain.engine.mod.java;

import unknowndomain.engine.Platform;
import unknowndomain.engine.mod.ModAssets;

import java.io.*;
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
    public InputStream openStream(String first) throws IOException {
        return get(first).toUri().toURL().openStream();
    }

    @Override
    public InputStream openStream(String first, String... more) throws IOException {
        return get(first, more).toUri().toURL().openStream();
    }

    @Override
    public boolean exists(String first) {
        return get(first).toFile().exists();
    }

    @Override
    public boolean exists(String first, String... more) {
        return get(first, more).toFile().exists();
    }

    @Override
    public void copy(Path target, String first) {
        try (var output = new FileOutputStream(target.toFile())){
            copy(output,first);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s to %s", get(first), target), e);
        }
    }

    @Override
    public void copy(Path target, String first, String... more) {
        try (var output = new FileOutputStream(target.toFile())){
            copy(output,first, more);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s to %s", get(first, more), target), e);
        }
    }

    @Override
    public void copy(OutputStream output, String first) {
        try(var input = openStream(first)){
            input.transferTo(output);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    @Override
    public void copy(OutputStream output, String first, String... more) {
        try(var input = openStream(first, more)){
            input.transferTo(output);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    @Override
    public void copy(Writer writer, String first) {
        try (var reader = new FileReader(get(first).toFile())){
            reader.transferTo(writer);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }

    @Override
    public void copy(Writer writer, String first, String... more) {
        try (var reader = new FileReader(get(first, more).toFile())){
            reader.transferTo(writer);
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Exception thrown when copying file from %s", get(first)), e);
        }
    }
}
