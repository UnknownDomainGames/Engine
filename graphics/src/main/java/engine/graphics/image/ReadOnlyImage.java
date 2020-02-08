package engine.graphics.image;

import java.nio.ByteBuffer;

public interface ReadOnlyImage {
    ByteBuffer getPixelBuffer();

    int getWidth();

    int getHeight();
}
