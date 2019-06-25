package nullengine.mod;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface ModAssets {

    /**
     * @param first
     * @return if path is not exists, return {@link Optional#empty()}
     */
    Optional<Path> get(String first);

    /**
     * @param first
     * @param more
     * @return if path is not exists, return {@link Optional#empty()}
     */
    Optional<Path> get(String first, String... more);

    /**
     * @param first
     * @return if file is not exists, return {@link Optional#empty()}
     */
    Optional<InputStream> openStream(String first) throws IOException;

    /**
     * @param first
     * @param more
     * @return if file is not exists, return {@link Optional#empty()}
     */
    Optional<InputStream> openStream(String first, String... more) throws IOException;

    @Nonnull
    Stream<Path> list(String first);

    @Nonnull
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
