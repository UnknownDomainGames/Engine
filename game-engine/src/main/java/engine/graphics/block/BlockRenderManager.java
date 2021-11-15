package engine.graphics.block;

import engine.block.state.BlockState;
import engine.graphics.vertex.VertexDataBuffer;
import engine.math.BlockPos;
import engine.util.Direction;
import engine.world.BlockGetter;

import java.util.function.Supplier;

public interface BlockRenderManager {

    boolean canRenderFace(BlockGetter world, BlockPos pos, BlockState block, Direction direction);

    boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, BlockState block, Direction direction);

    void generateMesh(BlockState block, BlockGetter world, BlockPos pos, VertexDataBuffer buffer);

    void generateMesh(BlockState block, VertexDataBuffer buffer);

    static BlockRenderManager instance() {
        return BlockRenderManager.Internal.instance.get();
    }

    class Internal {
        private static Supplier<BlockRenderManager> instance = () -> {
            throw new IllegalStateException("BlockRenderManager is uninitialized");
        };

        public static void setInstance(BlockRenderManager instance) {
            BlockRenderManager.Internal.instance = () -> instance;
        }
    }
}
