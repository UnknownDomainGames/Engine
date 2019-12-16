package nullengine.client.rendering.image;

import java.nio.ByteBuffer;

public interface LoadedImage {
    ByteBuffer getPixelBuffer();

    int getWidth();

    int getHeight();
}
