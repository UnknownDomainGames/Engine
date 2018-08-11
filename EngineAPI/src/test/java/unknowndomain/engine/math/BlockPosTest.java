package unknowndomain.engine.math;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockPosTest {

    BlockPos fix(BlockPos last, int y) {
        return null;
    }

    int[] intRange(int from, int to) {
        System.out.println(from + " -> " + to);
        Predicate<Integer> condition = from < to
                ? i -> i <= to
                : i -> i >= to;
        int direction = from < to ? 1 : -1;
        int[] arr = new int[Math.abs(from - to) + 1];
        int index = 0;
        for (int i = from; condition.test(i); i += direction)
            arr[index++] = i;
        return arr;
    }

    List<BlockPos> getAll(BlockPos pos, BlockPos dest) {
        Vector3f dir = new Vector3f(dest.getX() - pos.getX(), dest.getY() - pos.getY(), dest.getZ() - pos.getZ());
        System.out.println(dir);
        dir.mul(1F / Math.abs(dir.x));
        System.out.println(dir);
        List<BlockPos> posList = new ArrayList<>();
        posList.add(pos);
        BlockPos last = pos;
        for (int x : intRange(pos.getX(), dest.getX())) {
            int y = (int) (pos.getY() + dir.y);
            int z = (int) (pos.getZ() + dir.z);
            boolean yMiss = Math.abs(y - last.getY()) > 0,
                    zMiss = Math.abs(z - last.getZ()) > 0;
            System.out.println(new BlockPos(x, y, z));
            if (yMiss && zMiss) {
            } else if (yMiss) {
                System.out.println("Y");
                for (int j : intRange(last.getY() + 1, y - 1)) {
                    posList.add(last = new BlockPos(x, j, z));
                }
            } else if (zMiss) {
                for (int j : intRange(last.getZ() + 1, z - 1)) {
                    posList.add(last = new BlockPos(x, y, j));
                }
            }
            posList.add(last = new BlockPos(x, y, z));
        }

        return posList;
    }

    @Test
    void step() {
        BlockPos pos = new BlockPos(1, 0, 0);
        BlockPos dest = new BlockPos(0, 2, 0);
//

//        List<BlockPos> all = getAll(pos, dest);

//        System.out.println(all);
//        System.out.println(Lists.newArrayList(new BlockPos(2, 0, 0), new BlockPos(2, 0, 0)));
//        for (int x = pos.getX(); x < dest; x++) {
//
//        }
    }

    @Test
    void pack() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockPos from = new BlockPos(i, j, k);
                    int pack = from.pack();
                    assertEquals(from.getX() & 0xF, pack >> 8);
                    assertEquals(from.getY() & 0xF, (pack >> 4) & 0xF);
                    assertEquals(from.getZ() & 0xF, pack & 0xF);
                }
            }
        }
    }
}