package nullengine.client.rendering.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

public interface ImageHelper {
    LoadedImage loadImage(String url) throws IOException;

    LoadedImage loadImage(URL url) throws IOException;

    LoadedImage loadImage(InputStream input) throws IOException;

    LoadedImage loadImage(ByteBuffer bytes) throws IOException;

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
