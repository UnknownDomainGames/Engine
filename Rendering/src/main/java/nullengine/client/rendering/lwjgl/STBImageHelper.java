package nullengine.client.rendering.lwjgl;

import nullengine.client.rendering.image.ImageHelper;
import nullengine.client.rendering.image.LoadedImage;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class STBImageHelper implements ImageHelper {

    public static void init() {
        Internal.setInstance(new STBImageHelper());
    }

    @Override
    public LoadedImage loadImage(String url) throws IOException {
        return loadImage(new URL(url));
    }

    @Override
    public LoadedImage loadImage(URL url) throws IOException {
        return loadImage(url.openStream());
    }

    @Override
    public LoadedImage loadImage(InputStream input) throws IOException {
        try (input) {
            byte[] bytes = IOUtils.toByteArray(input);
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes).flip();
            return loadImage(buffer);
        }
    }

    @Override
    public LoadedImage loadImage(ByteBuffer bytes) {
        ByteBuffer directBytes;
        if (!bytes.isDirect()) {
            directBytes = ByteBuffer.allocateDirect(bytes.capacity());
            directBytes.put(bytes).flip();
        } else {
            directBytes = bytes;
        }
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            ByteBuffer pixelBuffer = STBImage.stbi_load_from_memory(directBytes, w, h, c, 4);
            return new STBLoadedImage(pixelBuffer, w.get(0), h.get(0));
        }
    }

    private static class STBLoadedImage implements LoadedImage {

        private final ByteBuffer pixelBuffer;
        private final int width;
        private final int height;

        public STBLoadedImage(ByteBuffer pixelBuffer, int width, int height) {
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
}
