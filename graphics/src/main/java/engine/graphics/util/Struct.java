package engine.graphics.util;

import java.nio.ByteBuffer;

public interface Struct {

    int sizeof();

    default ByteBuffer get(ByteBuffer buffer) {
        return get(0, buffer);
    }

    ByteBuffer get(int index, ByteBuffer buffer);
}
