package unknowndomain.engine.mod;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface ModAssets {

    @Nullable
    Path get(String first);

    @Nullable
    Path get(String first, String... more);

    InputStream openStream(String first) throws IOException;

    InputStream openStream(String first, String... more) throws IOException;

    boolean exists(String first);

    boolean exists(String first, String... more);

    Stream<Path> list(String first);

    Stream<Path> list(String first, String... more);

    default void copy(Path target, String first) {
        copy(target, false, first);
    }

    void copy(Path target, boolean forceCopying, String first);

    default void copy(Path target, String first, String... more) {
        copy(target, false, first, more);
    }

    void copy(Path target, boolean forceCopying, String first, String... more);

    void copy(OutputStream output, String first);

    void copy(OutputStream output, String first, String... more);

    void copy(Writer writer, String first);

    void copy(Writer writer, String first, String... more);
}
