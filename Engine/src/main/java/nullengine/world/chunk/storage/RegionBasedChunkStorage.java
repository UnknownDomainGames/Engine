package nullengine.world.chunk.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nullengine.util.Files2;
import nullengine.world.World;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkStorage;
import nullengine.world.chunk.CubicChunk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static nullengine.world.chunk.storage.RegionConstants.getRegionIndex;

public class RegionBasedChunkStorage implements ChunkStorage {

    private final World world;
    private final Path storagePath;

    private final Cache<Long, RegionFile> regionFileCache;

    public RegionBasedChunkStorage(World world, Path storagePath) {
        this.world = world;
        this.storagePath = storagePath;
        Files2.createDirectories(storagePath);
        regionFileCache = CacheBuilder.newBuilder().maximumSize(256).concurrencyLevel(8)
                .removalListener(notification -> {
                    try {
                        ((RegionFile) notification.getValue()).close();
                    } catch (IOException ignored) {
                    }
                }).build();
    }

    @Override
    public Path getStoragePath() {
        return storagePath;
    }

    @Override
    public Chunk load(int chunkX, int chunkY, int chunkZ) {
        long regionIndex = getRegionIndex(chunkX, chunkY, chunkZ);
        try {
            byte[] data = regionFileCache.get(regionIndex, () -> {
                Path regionFile = storagePath.resolve(regionIndex + ".region");
                if (!Files.exists(regionFile)) {
                    Files.createFile(regionFile);
                }
                return new RegionFile(regionFile.toFile());
            }).read(chunkX, chunkY, chunkZ);
            if (data == null) {
                return null;
            }

            CubicChunk cubicChunk = new CubicChunk(world, chunkX, chunkY, chunkZ);
            cubicChunk.read(new DataInputStream(new ByteArrayInputStream(data)));
            return cubicChunk;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Chunk chunk) {
        if (!(chunk instanceof CubicChunk)) {
            return;
        }

        long regionIndex = getRegionIndex(chunk.getX(), chunk.getY(), chunk.getZ());
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ((CubicChunk) chunk).write(new DataOutputStream(byteArrayOutputStream));
            regionFileCache.get(regionIndex, () -> {
                Path regionFile = storagePath.resolve(regionIndex + ".region");
                if (!Files.exists(regionFile)) {
                    Files.createFile(regionFile);
                }
                return new RegionFile(regionFile.toFile());
            }).write(chunk.getX(), chunk.getY(), chunk.getZ(), byteArrayOutputStream.toByteArray(), byteArrayOutputStream.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
