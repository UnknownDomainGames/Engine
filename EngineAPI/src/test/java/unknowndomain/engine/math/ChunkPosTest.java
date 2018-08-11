package unknowndomain.engine.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChunkPosTest {

    @Test
    void compact() {
        ChunkPos pos = new ChunkPos(-1, -1, -1);
        int compact = pos.compact();
        int x = compact >> 16;
        int z = compact & 0xFFFF;
        assertEquals(x, pos.getChunkX());
        assertEquals(z, pos.getChunkZ());
    }

    @Test
    void fat() {
    }
}