package engine.world.provider;

import engine.event.block.cause.BlockChangeCause;
import engine.game.Game;
import engine.math.BlockPos;
import engine.math.OctaveOpenSimplexNoiseSampler;
import engine.registry.Registries;
import engine.world.BaseWorldProvider;
import engine.world.World;
import engine.world.WorldCommonDebug;
import engine.world.WorldCreationSetting;
import engine.world.chunk.ChunkStatus;
import engine.world.gen.ChunkGeneratorNodeInfo;
import engine.world.gen.NodeBasedChunkGenerator;
import engine.world.gen.NodeBasedChunkGeneratorInfo;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static engine.world.chunk.ChunkConstants.*;

public class TestWorldProvider extends BaseWorldProvider {
    @Nonnull
    @Override
    public World create(@Nonnull Game game, @Nonnull Path storagePath, @Nonnull String name, @Nonnull WorldCreationSetting creationSetting) {
        var info = new NodeBasedChunkGeneratorInfo();
        var sampler = new OctaveOpenSimplexNoiseSampler(0, IntStream.range(0, 16).boxed().collect(Collectors.toList()));
        var seaLevel = 20;
        var noiseScaleY = 5;
        info.addNodes(new ChunkGeneratorNodeInfo(new ChunkStatus.Builder().name("layers").build(), (chunk, ctx) -> {
            int cx = chunk.getX();
            int cy = chunk.getY();
            int cz = chunk.getZ();
            if (cy < 0) //not making negative-Y chunks
                return;
            var yOffset = cy * CHUNK_Y_SIZE;
            for (int j = 0; j < CHUNK_Y_SIZE; j++) {
//                if (j + cy * CHUNK_Y_SIZE >= 5) {
//                    break;
//                }
                for (int i = 0; i < CHUNK_X_SIZE; i++) {
                    for (int k = 0; k < CHUNK_Z_SIZE; k++) {
                        if (j + yOffset < sampler.sample(cx + i, cz + k) * noiseScaleY + seaLevel) {
                            chunk.setBlock(BlockPos.of(i, j, k), Registries.getBlockRegistry().getValue(1).getDefaultState(), new BlockChangeCause.WorldGenCause());
                        }
                    }
                }
            }
        }));
        return new WorldCommonDebug(game, this, creationSetting, new NodeBasedChunkGenerator(info, creationSetting));
    }

    @Nonnull
    @Override
    public World load(@Nonnull Game game, @Nonnull Path storagePath) {
        var info = new NodeBasedChunkGeneratorInfo();
        var sampler = new OctaveOpenSimplexNoiseSampler(0, IntStream.range(0, 16).boxed().collect(Collectors.toList()));
        var seaLevel = 20;
        var noiseScaleY = 5;
        info.addNodes(new ChunkGeneratorNodeInfo(new ChunkStatus.Builder().name("layers").build(), (chunk, ctx) -> {
            int cx = chunk.getX();
            int cy = chunk.getY();
            int cz = chunk.getZ();
            if (cy < 0) //not making negative-Y chunks
                return;
            var xOffset = cx * CHUNK_X_SIZE;
            var yOffset = cy * CHUNK_Y_SIZE;
            var zOffset = cz * CHUNK_Z_SIZE;
            for (int j = 0; j < CHUNK_Y_SIZE; j++) {
//                if (j + cy * CHUNK_Y_SIZE >= 5) {
//                    break;
//                }
                for (int i = 0; i < CHUNK_X_SIZE; i++) {
                    for (int k = 0; k < CHUNK_Z_SIZE; k++) {
                        if (j + yOffset < sampler.sample((xOffset + i) / 64.0, (zOffset + k) / 64.0) * noiseScaleY + seaLevel) {
                            chunk.setBlock(BlockPos.of(i, j, k), Registries.getBlockRegistry().getValue(1).getDefaultState(), new BlockChangeCause.WorldGenCause());
                        }
                    }
                }
            }
        }));
        WorldCreationSetting creationSetting = new WorldCreationSetting() {
        };
        return new WorldCommonDebug(game, this, creationSetting, new NodeBasedChunkGenerator(info, creationSetting));
    }
}
