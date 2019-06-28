package nullengine.block;

import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.AABBd;
import org.joml.Rayd;
import org.joml.Vector2d;

public interface BlockShape {

    BlockShape NORMAL_CUBE = new Impl(new AABBd(0, 0, 0, 1, 1, 1));
    BlockShape EMPTY = new Impl() {
        @Override
        public boolean intersectRay(World world, BlockPos pos, Block block, Rayd ray, Vector2d result) {
            return false;
        }

        @Override
        public boolean intersectRay(World world, BlockPos pos, Block block, double originX, double originY, double originZ, double dirX, double dirY, double dirZ, Vector2d result) {
            return false;
        }
    };

    static BlockShape of(AABBd... boundingBoxes) {
        return new Impl(boundingBoxes);
    }

    AABBd[] getBoundingBoxes();

    AABBd[] getBoundingBoxes(World world, BlockPos pos, Block block);

    boolean intersectRay(World world, BlockPos pos, Block block, double originX, double originY, double originZ, double dirX, double dirY, double dirZ, Vector2d result);

    boolean intersectRay(World world, BlockPos pos, Block block, Rayd ray, Vector2d result);

    class Impl implements BlockShape {

        private final AABBd[] boundingBoxes;

        public Impl(AABBd... boundingBoxes) {
            this.boundingBoxes = boundingBoxes;
        }

        @Override
        public AABBd[] getBoundingBoxes() {
            return boundingBoxes;
        }

        @Override
        public AABBd[] getBoundingBoxes(World world, BlockPos pos, Block block) {
            return getBoundingBoxes();
        }

        @Override
        public boolean intersectRay(World world, BlockPos pos, Block block, double originX, double originY, double originZ, double dirX, double dirY, double dirZ, Vector2d result) {
            AABBd[] boundingBoxes = getBoundingBoxes(world, pos, block);
            for (int i = 0; i < boundingBoxes.length; i++) {
                if (boundingBoxes[i].intersectRay(originX, originY, originZ, dirX, dirY, dirZ, result)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean intersectRay(World world, BlockPos pos, Block block, Rayd ray, Vector2d result) {
            AABBd[] boundingBoxes = getBoundingBoxes(world, pos, block);
            for (int i = 0; i < boundingBoxes.length; i++) {
                if (boundingBoxes[i].intersectRay(ray, result)) {
                    return true;
                }
            }
            return false;
        }
    }
}
