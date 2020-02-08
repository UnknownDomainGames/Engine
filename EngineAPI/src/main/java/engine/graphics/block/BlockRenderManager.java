package engine.graphics.block;

import engine.block.Block;
import engine.graphics.vertex.VertexDataBuf;
import engine.math.BlockPos;
import engine.util.Direction;
import engine.world.BlockGetter;

import java.util.function.Supplier;

public interface BlockRenderManager {

    boolean canRenderFace(BlockGetter world, BlockPos pos, Block block, Direction direction);

    boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, Block block, Direction direction);

    void generateMesh(Block block, BlockGetter world, BlockPos pos, VertexDataBuf buffer);

    void generateMesh(Block block, VertexDataBuf buffer);

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
