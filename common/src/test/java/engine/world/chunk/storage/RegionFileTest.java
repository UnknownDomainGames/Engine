package engine.world.chunk.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RegionFileTest {

    @TempDir
    static File tempDir;

    static RegionFile regionFile;

    static byte[] oneSectorData;
    static byte[] twoSectorData;
    static byte[] oneAndHalfSectorData;

    @BeforeAll
    static void setUp() throws IOException {
        regionFile = new RegionFile(new File(tempDir, "region.dat"));

        oneSectorData = new byte[4096];
        Arrays.fill(oneSectorData, (byte) 1);
        twoSectorData = new byte[8192];
        Arrays.fill(twoSectorData, (byte) 2);
        oneAndHalfSectorData = new byte[6144];
        Arrays.fill(oneAndHalfSectorData, (byte) 3);

        regionFile.write(0, 0, 0, oneSectorData, oneSectorData.length);
        regionFile.write(15, 0, 0, twoSectorData, twoSectorData.length);
        regionFile.write(0, 15, 0, oneAndHalfSectorData, oneAndHalfSectorData.length);
        regionFile.write(15, 15, 15, oneSectorData, oneSectorData.length);
    }

    @AfterAll
    static void tearDown() throws IOException {
        regionFile.close();
    }

    @Test
    void write() throws IOException {
        assertArrayEquals(oneSectorData, regionFile.read(15, 15, 15));
        regionFile.write(15, 15, 15, twoSectorData, twoSectorData.length);
        assertArrayEquals(twoSectorData, regionFile.read(15, 15, 15));
        regionFile.write(15, 15, 15, oneAndHalfSectorData, oneAndHalfSectorData.length);
        assertArrayEquals(oneAndHalfSectorData, regionFile.read(15, 15, 15));
    }

    @Test
    void read() throws IOException {
        assertArrayEquals(oneSectorData, regionFile.read(0, 0, 0));
        assertArrayEquals(twoSectorData, regionFile.read(15, 0, 0));
        assertArrayEquals(oneAndHalfSectorData, regionFile.read(0, 15, 0));
    }
}