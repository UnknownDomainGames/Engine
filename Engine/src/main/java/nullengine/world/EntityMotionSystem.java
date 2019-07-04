package nullengine.world;

import nullengine.entity.Entity;
import nullengine.event.Listener;
import nullengine.event.world.WorldTickEvent;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;
import nullengine.world.chunk.ChunkManager;
import org.joml.Vector3f;

public class EntityMotionSystem {
    @Listener
    public void tickEntityMotion(WorldTickEvent event) {
        var world = event.getWorld();
        ChunkManager<World> chunkManager = world.getChunkManager();
        for (Entity entity : world.getEntities()) {
            Vector3f position = entity.getPosition();
            Vector3f motion = entity.getMotion();
            BlockPos oldPosition = ChunkConstants.toChunkPos(BlockPos.of(position));
            position.add(motion);
            BlockPos newPosition = ChunkConstants.toChunkPos(BlockPos.of(position));

            if (!BlockPos.inSameChunk(oldPosition, newPosition)) {
                Chunk oldChunk = chunkManager.loadChunk(oldPosition.getX(), oldPosition.getY(), oldPosition.getZ()),
                        newChunk = chunkManager.loadChunk(newPosition.getX(), newPosition.getY(), newPosition.getZ());
                oldChunk.getEntities().remove(entity);
                newChunk.getEntities().add(entity);
                // entity leaving and enter chunk event
            }
        }
    }
}
