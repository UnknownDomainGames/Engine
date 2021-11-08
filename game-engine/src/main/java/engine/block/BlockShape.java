package engine.block;

import engine.math.BlockPos;
import engine.world.World;
import org.joml.Vector2d;
import org.joml.primitives.AABBd;
import org.joml.primitives.Rayd;

public interface BlockShape {

    BlockShape NORMAL_CUBE = new Impl(new AABBd(0, 0, 0, 1, 1, 1));
    BlockShape EMPTY = new Impl() {
        @Override
        public boolean intersectsRay(World world, BlockPos pos, Block block, Rayd ray, Vector2d result) {
            return false;
        }

        @Override
        public boolean intersectsRay(World world, BlockPos pos, Block block, double originX, double originY, double originZ, double dirX, double dirY, double dirZ, Vector2d result) {
            return false;
        }
    };

    static BlockShape of(AABBd... boundingBoxes) {
        return new Impl(boundingBoxes);
    }

    AABBd[] getBoundingBoxes();

    AABBd[] getBoundingBoxes(World world, BlockPos pos, Block block);

    boolean intersectsRay(World world, BlockPos pos, Block block, double originX, double originY, double originZ, double dirX, double dirY, double dirZ, Vector2d result);

    boolean intersectsRay(World world, BlockPos pos, Block block, Rayd ray, Vector2d result);

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
        public boolean intersectsRay(World world, BlockPos pos, Block block, double originX, double originY, double originZ, double dirX, double dirY, double dirZ, Vector2d result) {
            AABBd[] boundingBoxes = getBoundingBoxes(world, pos, block);
            for (int i = 0; i < boundingBoxes.length; i++) {
                if (boundingBoxes[i].intersectsRay(originX, originY, originZ, dirX, dirY, dirZ, result)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean intersectsRay(World world, BlockPos pos, Block block, Rayd ray, Vector2d result) {
            AABBd[] boundingBoxes = getBoundingBoxes(world, pos, block);
            for (int i = 0; i < boundingBoxes.length; i++) {
                if (boundingBoxes[i].intersectsRay(ray, result)) {
                    return true;
                }
            }
            return false;
        }
    }
}
