package unknowndomain.engine.math;

import org.joml.AABBd;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;

class AABBsTest {

    @Test
    void translate() {
    }

    @Test
    void around() {
        List<BlockPos>[] around = AABBs.around(new AABBd(0.1, 0.3, 0.1, 1.1, 1.3, 1.1), new Vector3f(0.1F, 0.1F, 0.1F));
        System.out.println(around[0]);
        System.out.println(around[1]);
        System.out.println(around[2]);
    }
}