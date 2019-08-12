package nullengine.client.world;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.World;
import nullengine.world.WorldCreationSetting;
import nullengine.world.WorldProvider;
import nullengine.world.WorldSetting;
import nullengine.world.chunk.Chunk;
import nullengine.world.collision.WorldCollisionManager;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class WorldClient implements World, Runnable {

    private final Game game;
    private final WorldProvider worldProvider;

    public WorldClient(Game game, WorldProvider provider){
        this.game = game;
        this.worldProvider = provider;
    }

    @Override
    public void run() {

    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public WorldProvider getProvider() {
        return worldProvider;
    }

    @Override
    public Path getStoragePath() {
        return null;
    }

    @Override
    public WorldCreationSetting getCreationSetting() {
        return null;
    }

    @Override
    public WorldSetting getSetting() {
        return null;
    }

    @Override
    public WorldCollisionManager getCollisionManager() {
        return null;
    }

    @Override
    public long getGameTick() {
        return 0;
    }

    @Override
    public List<Entity> getEntities() {
        return null;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return null;
    }

    @Override
    public Collection<Chunk> getLoadedChunks() {
        return null;
    }

    @Nonnull
    @Override
    public Block getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return 0;
    }

    @Nonnull
    @Override
    public Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause) {
        return null;
    }

    @Nonnull
    @Override
    public Block destoryBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause) {
        return null;
    }
}
