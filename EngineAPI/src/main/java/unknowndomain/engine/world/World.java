package unknowndomain.engine.world;

import org.joml.Vector3f;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.math.BlockPos;

import java.util.Set;

public interface World extends RuntimeObject {
    BlockPrototype.Hit rayHit(Vector3f from, Vector3f dir, float distance);

    BlockPrototype.Hit rayHit(Vector3f from, Vector3f dir, float distance, Set<Block> ignore);

    Chunk getChunk(int x, int z);

    Block getBlock(BlockPos pos);

    Block setBlock(BlockPos pos, Block block);
}
