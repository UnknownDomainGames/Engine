package nullengine.client.rendering.world.chunk;

import nullengine.client.rendering.gl.buffer.GLBuffer;

public class BakeChunkThread extends Thread {

    private final GLBuffer buffer = GLBuffer.createDirectBuffer(0x200000);

    public BakeChunkThread(Runnable target, String name) {
        super(target, name);
    }

    public GLBuffer getBuffer() {
        return buffer;
    }
}
