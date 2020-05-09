package engine.world.chunk.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import engine.util.Files2;
import engine.world.World;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkStorage;
import engine.world.chunk.CubicChunk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static engine.world.chunk.storage.RegionConstants.getRegionIndex;
import static engine.world.chunk.storage.RegionConstants.toRegionCoordinate;

public class RegionBasedChunkStorage implements ChunkStorage {

    private final World world;
    private final Path storagePath;

    private final Cache<Long, RegionFile> regionFileCache;

    private boolean closed = false;

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
        if (closed) {
            throw new IllegalStateException("Chunk storage has been closed");
        }

        long regionIndex = getRegionIndex(chunkX, chunkY, chunkZ);
        try {
            byte[] data = regionFileCache.get(regionIndex, () -> {
                Path regionFile = storagePath.resolve(getCorrespondingRegionFileName(chunkX, chunkY, chunkZ));
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
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getCorrespondingRegionFileName(int chunkX, int chunkY, int chunkZ) {
        return toRegionCoordinate(chunkX) + "_" + toRegionCoordinate(chunkY) + "_" + toRegionCoordinate(chunkZ) + ".region";
    }

    @Override
    public void save(Chunk chunk) {
        if (closed) {
            throw new IllegalStateException("Chunk storage has been closed");
        }

        if (!(chunk instanceof CubicChunk)) {
            return;
        }

        long regionIndex = getRegionIndex(chunk.getX(), chunk.getY(), chunk.getZ());
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ((CubicChunk) chunk).write(new DataOutputStream(byteArrayOutputStream));
            regionFileCache.get(regionIndex, () -> {
                Path regionFile = storagePath.resolve(getCorrespondingRegionFileName(chunk.getX(), chunk.getY(), chunk.getZ()));
                if (!Files.exists(regionFile)) {
                    Files.createFile(regionFile);
                }
                return new RegionFile(regionFile.toFile());
            }).write(chunk.getX(), chunk.getY(), chunk.getZ(), byteArrayOutputStream.toByteArray(), byteArrayOutputStream.size());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;
        regionFileCache.asMap().forEach((key, value) -> {
            try {
                value.close();
            } catch (IOException ignored) {
            }
        });
    }
}
