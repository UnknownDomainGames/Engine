package nullengine.math;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        Vector3f dir = new Vector3f(dest.x() - pos.x(), dest.y() - pos.y(), dest.z() - pos.z());
        System.out.println(dir);
        dir.mul(1F / Math.abs(dir.x));
        System.out.println(dir);
        List<BlockPos> posList = new ArrayList<>();
        posList.add(pos);
        BlockPos last = pos;
        for (int x : intRange(pos.x(), dest.x())) {
            int y = (int) (pos.y() + dir.y);
            int z = (int) (pos.z() + dir.z);
            boolean yMiss = Math.abs(y - last.y()) > 0,
                    zMiss = Math.abs(z - last.z()) > 0;
            System.out.println(BlockPos.of(x, y, z));
            if (yMiss && zMiss) {
            } else if (yMiss) {
                System.out.println("Y");
                for (int j : intRange(last.y() + 1, y - 1)) {
                    posList.add(last = BlockPos.of(x, j, z));
                }
            } else if (zMiss) {
                for (int j : intRange(last.z() + 1, z - 1)) {
                    posList.add(last = BlockPos.of(x, y, j));
                }
            }
            posList.add(last = BlockPos.of(x, y, z));
        }

        return posList;
    }

    @Test
    void step() {
        BlockPos pos = BlockPos.of(1, 0, 0);
        BlockPos dest = BlockPos.of(0, 2, 0);
//

//        List<BlockPos> all = getAll(pos, dest);

//        System.out.println(all);
//        System.out.println(Lists.newArrayList(new BlockPos(2, 0, 0), new BlockPos(2, 0, 0)));
//        for (int x = pos.getX(); x < dest; x++) {
//
//        }
    }
}