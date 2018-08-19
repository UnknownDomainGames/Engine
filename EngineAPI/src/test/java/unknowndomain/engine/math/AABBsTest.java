package unknowndomain.engine.math;

import org.joml.AABBd;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class AABBsTest {

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