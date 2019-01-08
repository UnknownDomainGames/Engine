package unknowndomain.engine.client.rendering.world.chunk;

import unknowndomain.engine.client.rendering.util.BufferBuilder;

public class BakeChunkThread extends Thread {

    private final BufferBuilder buffer = new BufferBuilder(0x200000);

    public BakeChunkThread(Runnable target, String name) {
        super(target, name);
    }

    public BufferBuilder getBuffer() {
        return buffer;
    }
}
