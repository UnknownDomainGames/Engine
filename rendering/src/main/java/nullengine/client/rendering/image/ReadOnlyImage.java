package nullengine.client.rendering.image;

import java.nio.ByteBuffer;

public interface ReadOnlyImage {
    ByteBuffer getPixelBuffer();

    int getWidth();

    int getHeight();
}
