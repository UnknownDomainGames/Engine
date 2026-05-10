package engine.world.chunk.storage;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.BitSet;

import static engine.world.chunk.storage.RegionConstants.*;

@ThreadSafe
public class RegionFile implements AutoCloseable {

    private static final int SECTOR_SIZE_BITS = 12;
    private static final int SECTOR_SIZE = 1 << SECTOR_SIZE_BITS;
    private static final int SECTOR_MAX_INDEX = SECTOR_SIZE - 1;
    private static final int REGION_HEADER_SIZE = REGION_SIZE * Integer.BYTES;
    private static final int CHUNK_HEADER_SIZE = Integer.BYTES;

    private final RandomAccessFile file;
    private final int[] chunkStartSectors;
    private final BitSet usedSectors;
    private int sectorCount;

    public RegionFile(File file) throws IOException {
        this.file = new RandomAccessFile(file, "rw");
        this.chunkStartSectors = new int[REGION_SIZE];
        Arrays.fill(chunkStartSectors, -1);
        this.usedSectors = new BitSet();

        if (this.file.length() < REGION_HEADER_SIZE) { // Initialize empty region file
            this.file.seek(0);
            for (int i = 0; i < REGION_SIZE; i++) {
                this.file.writeInt(-1);
            }
            sectorCount = 0;
            return;
        }

        // Initialize exists region file
        loadChunkStartSectors();

        sectorCount = getSectorCount(this.file.length()) - 4;
        initUsedSectors();
    }

    private void loadChunkStartSectors() throws IOException {
        file.seek(0);
        for (int i = 0; i < REGION_SIZE; i++) {
            chunkStartSectors[i] = file.readInt();
        }
    }

    private void initUsedSectors() throws IOException {
        for (int startSector : chunkStartSectors) {
            if (startSector == -1) {
                continue;
            }

            int chunkSectorCount = getSectorCount(getChunkDataLength(startSector));
            useSectors(startSector, startSector + chunkSectorCount);
        }
    }

    public synchronized void write(int chunkX, int chunkY, int chunkZ, byte[] data, int length) throws IOException {
        int chunkIndex = getChunkIndex(chunkX, chunkY, chunkZ);
        int startSector = chunkStartSectors[chunkIndex];

        if (startSector != -1) {
            int oldSectorCount = getSectorCount(getChunkDataLength(startSector) + CHUNK_HEADER_SIZE);
            int newSectorCount = getSectorCount(length + CHUNK_HEADER_SIZE);
            if (oldSectorCount >= newSectorCount) {
                freeSectors(startSector + newSectorCount, startSector + oldSectorCount);
            } else {
                freeSectors(startSector, startSector + oldSectorCount);
                startSector = allocateSectors(getSectorCount(length + CHUNK_HEADER_SIZE));
                setStartSector(chunkIndex, startSector);
            }
        } else {
            startSector = allocateSectors(getSectorCount(length + CHUNK_HEADER_SIZE));
            setStartSector(chunkIndex, startSector);
        }

        long sectorOffset = getSectorPosition(startSector);
        file.seek(sectorOffset);
        file.writeInt(length);
        file.write(data, 0, length);
    }

    public synchronized byte[] read(int chunkX, int chunkY, int chunkZ) throws IOException {
        int startSector = chunkStartSectors[getChunkIndex(chunkX, chunkY, chunkZ)];
        if (startSector == -1) {
            return null;
        }

        int sectorPosition = getSectorPosition(startSector);
        file.seek(sectorPosition);
        int length = file.readInt();
        byte[] data = new byte[length];
        file.read(data);
        return data;
    }

    public boolean hasChunk(int chunkX, int chunkY, int chunkZ) {
        return chunkStartSectors[getChunkIndex(chunkX, chunkY, chunkZ)] != -1;
    }

    private int getChunkDataLength(int startSector) throws IOException {
        long sectorPosition = getSectorPosition(startSector);
        file.seek(sectorPosition);
        return file.readInt();
    }

    private int getSectorCount(long length) {
        return (int) ((length & SECTOR_MAX_INDEX) == 0 ? length >> SECTOR_SIZE_BITS : (length >> SECTOR_SIZE_BITS) + 1);
    }

    private int getSectorPosition(int sector) {
        return sector * SECTOR_SIZE + REGION_HEADER_SIZE;
    }

    private int getChunkIndex(int chunkX, int chunkY, int chunkZ) {
        return (chunkX & REGION_MAX_X) | ((chunkY & REGION_MAX_Y) << REGION_X_BITS) | ((chunkZ & REGION_MAX_Z) << (REGION_X_BITS + REGION_Y_BITS));
    }

    private void useSectors(int start, int end) {
        usedSectors.set(start, end);
    }

    private void freeSectors(int start, int end) {
        usedSectors.clear(start, end);
    }

    private void setStartSector(int chunkIndex, int startSector) throws IOException {
        chunkStartSectors[chunkIndex] = startSector;
        file.seek(chunkIndex * Integer.BYTES);
        file.writeInt(startSector);
    }

    private int allocateSectors(int count) {
        int startSector = -1;
        int allocatedSector = 0;
        for (int i = 0; i < sectorCount; i++) {
            if (usedSectors.get(i)) {
                startSector = -1;
                allocatedSector = 0;
            } else {
                if (startSector == -1) {
                    startSector = i;
                }
                allocatedSector++;

                if (allocatedSector == count) {
                    useSectors(startSector, startSector + allocatedSector);
                    return startSector;
                }
            }
        }

        if (startSector == -1) { // if no found sector
            startSector = sectorCount;
        }

        useSectors(startSector, startSector + count);
        if (allocatedSector < count) { // add enough sectors
            sectorCount += count - allocatedSector;
        }

        return startSector;
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
