package unknowndomain.engine.math;

import org.joml.AABBd;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

class AABBsTest {

    int encode(int x, int y, int z) {
        return (x << 8) | (y << 4) | z;
    }

    @Test
    void testDirection() {
        int pos = encode(10, 2, 8);

        int x = ((pos >> 8) & 0xF);
        int y = ((pos >> 4) & 0xF);
        int z = (pos & 0xF);

        System.out.println(x + " " + y + " " + z);
        int npos = encode(9, 2, 8);
        System.out.println(npos - pos);

        IntBuffer i = IntBuffer.allocate(1024);
        i.put(4).put(4).flip();
        System.out.println(i.limit());

    }

    @Test
    void translate() {
        int radius = 2;
        List<BlockPos> poses = new ArrayList<>();
        BlockPos pos = BlockPos.of(0, 0, 0);
        for (int i = -radius; i <= radius; i++) {
            if (i <= 0) {
                int zOff = i + radius;
                for (int j = -zOff; j <= zOff; j++)
                    poses.add(pos.add(i, 0, j));
            } else {
                int zOff = radius - i;
                for (int j = -zOff; j <= zOff; j++)
                    poses.add(pos.add(i, 0, j));
            }
        }
    }

    @Test
    void xNotCrossing() {
        List<BlockPos>[] around = AABBs.around(new AABBd(0.8, 0, -0.1, 1.2, 1, 0.8), new Vector3f(0.1F, 0, 0));
        Assertions.assertEquals(4, around[0].size());
        Assertions.assertEquals(0, around[1].size());
        Assertions.assertEquals(0, around[2].size());
        System.out.println(around[0]);
    }

    @Test
    void xCrossing() {
        AABBd src = new AABBd(-0.4, -0.5, -0.4, 0.4, 0.5, 0.4);
        AABBs.translate(src, new Vector3f(0.55F, 0, 0.3F));
        List<BlockPos>[] around = AABBs.around(src, new Vector3f(0.1F, 0, 0));
        Assertions.assertEquals(4, around[0].size());
        Assertions.assertEquals(0, around[1].size());
        Assertions.assertEquals(0, around[2].size());
    }
}