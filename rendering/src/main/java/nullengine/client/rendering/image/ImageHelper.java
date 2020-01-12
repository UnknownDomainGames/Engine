package nullengine.client.rendering.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.function.Supplier;

public interface ImageHelper {

    ReadOnlyImage loadImage(File file) throws IOException;

    ReadOnlyImage loadImage(Path path) throws IOException;

    ReadOnlyImage loadImage(String url) throws IOException;

    ReadOnlyImage loadImage(URL url) throws IOException;

    ReadOnlyImage loadImage(InputStream input) throws IOException;

    ReadOnlyImage loadImage(ByteBuffer bytes) throws IOException;

    BufferedImage loadWritableImage(File file) throws IOException;

    BufferedImage loadWritableImage(Path path) throws IOException;

    BufferedImage loadWritableImage(String url) throws IOException;

    BufferedImage loadWritableImage(URL url) throws IOException;

    BufferedImage loadWritableImage(InputStream input) throws IOException;

    BufferedImage loadWritableImage(ByteBuffer bytes) throws IOException;

    static ImageHelper instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<ImageHelper> instance = () -> {
            throw new IllegalStateException("ImageHelper is not initialized");
        };

        public static void setInstance(ImageHelper instance) {
            Internal.instance = () -> instance;
        }
    }
}
