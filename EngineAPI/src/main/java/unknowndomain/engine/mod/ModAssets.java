package unknowndomain.engine.mod;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;

public interface ModAssets {

    Path get(String first);

    Path get(String first, String... more);

    InputStream openStream(String first);

    InputStream openStream(String first, String... more);

    boolean exists(String first);

    boolean exists(String first, String... more);

    void copy(Path target, String first);

    void copy(Path target, String first, String... more);

    void copy(OutputStream output, String first);

    void copy(OutputStream output, String first, String... more);

    void copy(Writer writer, String first);

    void copy(Writer writer, String first, String... more);
}
