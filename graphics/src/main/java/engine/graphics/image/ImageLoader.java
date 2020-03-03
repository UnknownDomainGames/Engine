package engine.graphics.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ServiceLoader;

public abstract class ImageLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger("Graphics");

    private static ImageLoader instance;

    public static ImageLoader instance() {
        return instance;
    }

    public static synchronized void initialize(String factoryName) {
        if (instance != null) throw new IllegalStateException("ImageLoader has been initialized");
        instance = ServiceLoader.load(Factory.class)
                .stream()
                .filter(provider -> provider.get().getName().equals(factoryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No found ImageLoader: " + factoryName))
                .get()
                .create();
    }

    public abstract ReadOnlyImage loadImage(File file) throws IOException;

    public abstract ReadOnlyImage loadImage(Path path) throws IOException;

    public abstract ReadOnlyImage loadImage(String url) throws IOException;

    public abstract ReadOnlyImage loadImage(URL url) throws IOException;

    public abstract ReadOnlyImage loadImage(InputStream input) throws IOException;

    public abstract ReadOnlyImage loadImage(ByteBuffer bytes) throws IOException;

    public abstract BufferedImage loadWritableImage(File file) throws IOException;

    public abstract BufferedImage loadWritableImage(Path path) throws IOException;

    public abstract BufferedImage loadWritableImage(String url) throws IOException;

    public abstract BufferedImage loadWritableImage(URL url) throws IOException;

    public abstract BufferedImage loadWritableImage(InputStream input) throws IOException;

    public abstract BufferedImage loadWritableImage(ByteBuffer bytes) throws IOException;

    public interface Factory {
        String getName();

        ImageLoader create();
    }
}
