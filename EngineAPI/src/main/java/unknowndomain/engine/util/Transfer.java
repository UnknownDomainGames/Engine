package unknowndomain.engine.util;

import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

public class Transfer implements Callable<Long> {
    private ReadableByteChannel src;
    private FileChannel dest;
    private long position = 0;
    private long total = 0;
    private long chunk = 4096 * 1024;

    public Transfer(ReadableByteChannel src, FileChannel dest, long total) {
        this.src = src;
        this.dest = dest;
        this.total = total;
    }

    public Transfer(ReadableByteChannel src, FileChannel dest) {
        this.src = src;
        this.dest = dest;
    }

    public long getProgress() {
        return position;
    }

    public long getTotal() {
        return total;
    }

    public Transfer setChunk(long chunk) {
        this.chunk = chunk;
        return this;
    }

    @Override
    public Long call() throws Exception {
        for (long wrote;
             (wrote = dest.transferFrom(src, position, chunk)) > 0;
             position += wrote)
            ;
        return position;
    }
}
