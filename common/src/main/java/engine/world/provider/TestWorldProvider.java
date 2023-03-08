package engine.world.provider;

import engine.event.block.cause.BlockChangeCause;
import engine.game.Game;
import engine.math.BlockPos;
import engine.math.OctaveOpenSimplexNoiseSampler;
import engine.registry.Name;
import engine.registry.Registries;
import engine.world.BaseWorldProvider;
import engine.world.World;
import engine.world.WorldCommonDebug;
import engine.world.WorldCreationSetting;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkStatus;
import engine.world.gen.ChunkGeneratorNodeInfo;
import engine.world.gen.NodeBasedChunkGenerator;
import engine.world.gen.NodeBasedChunkGeneratorInfo;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestWorldProvider extends BaseWorldProvider {

    private final NodeBasedChunkGeneratorInfo info;

    public TestWorldProvider() {
        info = new NodeBasedChunkGeneratorInfo();
        var sampler = new OctaveOpenSimplexNoiseSampler(0, IntStream.range(0, 16).boxed().collect(Collectors.toList()));
        var seaLevel = 20;
        var noiseScaleY = 5;
        var xResolution = 64.0;
        var zResolution = 64.0;
        info.addNodes(new ChunkGeneratorNodeInfo(new ChunkStatus.Builder().name("layers").build(), (chunk, ctx) -> {
            int cx = chunk.getX();
            int cy = chunk.getY();
            int cz = chunk.getZ();
            if (cy < 0) //not making negative-Y chunks
                return;
            var xOffset = cx * Chunk.CHUNK_X_SIZE;
            var yOffset = cy * Chunk.CHUNK_Y_SIZE;
            var zOffset = cz * Chunk.CHUNK_Z_SIZE;
            for (int j = 0; j < Chunk.CHUNK_Y_SIZE; j++) {
                for (int i = 0; i < Chunk.CHUNK_X_SIZE; i++) {
                    for (int k = 0; k < Chunk.CHUNK_Z_SIZE; k++) {
                        if (j + yOffset <= Math.floor(sampler.sample((xOffset + i) / xResolution, (zOffset + k) / zResolution) * noiseScaleY + seaLevel)) {
                            var state = Registries.getBlockRegistry().getValue(Name.of("foundation", "grass")).getDefaultState();
                            chunk.setBlock(BlockPos.of(i, j, k), state, new BlockChangeCause.WorldGenCause());
                            if (ctx.getTargetChunkColumn() != null) {
                                ctx.getTargetChunkColumn().getHeightmap().updatePos(i, j, k, state);
                            }
                        }
                    }
                }
            }
        }));
    }

    @Nonnull
    @Override
    public World create(@Nonnull Game game, @Nonnull Path storagePath, @Nonnull String name, @Nonnull WorldCreationSetting creationSetting) {
        return new WorldCommonDebug(game, this, creationSetting, new NodeBasedChunkGenerator(info, creationSetting));
    }

    @Nonnull
    @Override
    public World load(@Nonnull Game game, @Nonnull Path storagePath) {
        WorldCreationSetting creationSetting = new WorldCreationSetting() {
        };
        return new WorldCommonDebug(game, this, creationSetting, new NodeBasedChunkGenerator(info, creationSetting));
    }
}
