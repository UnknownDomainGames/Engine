package engine.graphics.lwjgl;

import engine.graphics.image.BufferedImage;
import engine.graphics.image.ImageLoader;
import engine.graphics.image.ReadOnlyImage;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class STBImageLoader extends ImageLoader {

    @Override
    public ReadOnlyImage loadImage(File file) throws IOException {
        try {
            return loadImage(new FileInputStream(file));
        } catch (IOException e) {
            throw new IOException("Cannot load image from file: " + file, e);
        }
    }

    @Override
    public ReadOnlyImage loadImage(Path path) throws IOException {
        try {
            return loadImage(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IOException("Cannot load image from file: " + path, e);
        }
    }

    @Override
    public ReadOnlyImage loadImage(String url) throws IOException {
        try {
            return loadImage(new URL(url).openStream());
        } catch (IOException e) {
            throw new IOException("Cannot load image from url: " + url, e);
        }
    }

    @Override
    public ReadOnlyImage loadImage(URL url) throws IOException {
        try {
            return loadImage(url.openStream());
        } catch (IOException e) {
            throw new IOException("Cannot load image from url: " + url, e);
        }
    }

    @Override
    public ReadOnlyImage loadImage(InputStream input) throws IOException {
        try (input) {
            byte[] bytes = IOUtils.toByteArray(input);
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes).flip();
            return loadImage(buffer);
        }
    }

    @Override
    public ReadOnlyImage loadImage(ByteBuffer bytes) throws IOException {
        ByteBuffer directBytes = bytes.isDirect() ? bytes : ByteBuffer.allocateDirect(bytes.capacity()).put(bytes).flip();
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            ByteBuffer pixelBuffer = STBImage.stbi_load_from_memory(directBytes, w, h, c, 4);
            if (pixelBuffer == null) {
                throw new IOException("Failed to load image");
            }
            return new ReadOnlyImageImpl(pixelBuffer.asReadOnlyBuffer(), w.get(0), h.get(0));
        }
    }

    @Override
    public BufferedImage loadWritableImage(File file) throws IOException {
        try {
            return loadWritableImage(new FileInputStream(file));
        } catch (IOException e) {
            throw new IOException("Cannot load image from file: " + file, e);
        }
    }

    @Override
    public BufferedImage loadWritableImage(Path path) throws IOException {
        try {
            return loadWritableImage(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IOException("Cannot load image from file: " + path, e);
        }
    }

    @Override
    public BufferedImage loadWritableImage(String url) throws IOException {
        try {
            return loadWritableImage(new URL(url).openStream());
        } catch (IOException e) {
            throw new IOException("Cannot load image from url: " + url, e);
        }
    }

    @Override
    public BufferedImage loadWritableImage(URL url) throws IOException {
        try {
            return loadWritableImage(url.openStream());
        } catch (IOException e) {
            throw new IOException("Cannot load image from url: " + url, e);
        }
    }

    @Override
    public BufferedImage loadWritableImage(InputStream input) throws IOException {
        try (input) {
            byte[] bytes = IOUtils.toByteArray(input);
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes).flip();
            return loadWritableImage(buffer);
        }
    }

    @Override
    public BufferedImage loadWritableImage(ByteBuffer bytes) throws IOException {
        ByteBuffer directBytes = bytes.isDirect() ? bytes : ByteBuffer.allocateDirect(bytes.capacity()).put(bytes).flip();
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            ByteBuffer pixelBuffer = STBImage.stbi_load_from_memory(directBytes, w, h, c, 4);
            if (pixelBuffer == null) {
                throw new IOException("Failed to load image");
            }
            return BufferedImage.wrap(pixelBuffer, w.get(0), h.get(0));
        }
    }

    private static final class ReadOnlyImageImpl implements ReadOnlyImage {

        private final ByteBuffer pixelBuffer;
        private final int width;
        private final int height;

        public ReadOnlyImageImpl(ByteBuffer pixelBuffer, int width, int height) {
            this.pixelBuffer = pixelBuffer;
            this.width = width;
            this.height = height;
        }

        @Override
        public ByteBuffer getPixelBuffer() {
            return pixelBuffer;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }

    public static final class Factory implements ImageLoader.Factory {

        @Override
        public String getName() {
            return "stb";
        }

        @Override
        public ImageLoader create() {
            return new STBImageLoader();
        }
    }
}
