package unknowndomain.engine.mod;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;

public interface ModAssets {

    @Nullable
    Path get(String first);

    @Nullable
    Path get(String first, String... more);

    InputStream openStream(String first) throws IOException, FileNotFoundException;

    InputStream openStream(String first, String... more) throws IOException, FileNotFoundException;

    boolean exists(String first);

    boolean exists(String first, String... more);

    void copy(Path target, String first);

    void copy(Path target, boolean forceCopying, String first);

    void copy(Path target, String first, String... more);

    void copy(Path target, boolean forceCopying, String first, String... more);

    void copy(OutputStream output, String first);

    void copy(OutputStream output, String first, String... more);

    void copy(Writer writer, String first);

    void copy(Writer writer, String first, String... more);
}
