package unknowndomain.engine.client.rendering.world.chunk;

import unknowndomain.engine.client.rendering.util.BufferBuilder;

public class BakeChunkThread extends Thread {

    private final BufferBuilder buffer = new BufferBuilder(0x200000);

    public BakeChunkThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public BufferBuilder getBuffer() {
        return buffer;
    }
}
