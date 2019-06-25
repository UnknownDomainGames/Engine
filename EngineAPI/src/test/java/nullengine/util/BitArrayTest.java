package nullengine.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitArrayTest {

    private static final int[] bit8RawArray = {0, (1 << 8) - 1}, bit8RawArray2 = {(1 << 8) - 1, 0};
    private static final int[] bit24RawArray = {0, (1 << 24) - 1, (1 << 24) - 1, (1 << 24) - 1, 0}, bit24RawArray2 = {(1 << 24) - 1, 0, 0, 0, (1 << 24) - 1};
    private static final int[] bit31RawArray = {0, 0x7fffffff}, bit31RawArray2 = {0x7fffffff, 0};
    private static BitArray bit8Array, bit24Array, bit31Array;

    @BeforeAll
    public static void init() {
        bit8Array = new BitArray(8, 2);
        for (int i = 0; i < bit8Array.length(); i++) {
            bit8Array.set(i, bit8RawArray[i]);
        }
        bit24Array = new BitArray(24, 5);
        for (int i = 0; i < bit24Array.length(); i++) {
            bit24Array.set(i, bit24RawArray[i]);
        }
        bit31Array = new BitArray(31, 2);
        for (int i = 0; i < bit31Array.length(); i++) {
            bit31Array.set(i, bit31RawArray[i]);
        }
    }

    @Test
    public void get() {
        for (int i = 0; i < bit8Array.length(); i++) {
            assertEquals(bit8Array.get(i), bit8RawArray[i]);
        }
        for (int i = 0; i < bit24Array.length(); i++) {
            assertEquals(bit24Array.get(i), bit24RawArray[i]);
        }
        for (int i = 0; i < bit31Array.length(); i++) {
            assertEquals(bit31Array.get(i), bit31RawArray[i]);
        }
    }

    @Test
    public void getAndSet() {
        for (int i = 0; i < bit8Array.length(); i++) {
            assertEquals(bit8Array.getAndSet(i, bit8RawArray2[i]), bit8RawArray[i]);
        }
        for (int i = 0; i < bit8Array.length(); i++) {
            assertEquals(bit8Array.get(i), bit8RawArray2[i]);
        }
        for (int i = 0; i < bit24Array.length(); i++) {
            assertEquals(bit24Array.getAndSet(i, bit24RawArray2[i]), bit24RawArray[i]);
        }
        for (int i = 0; i < bit24Array.length(); i++) {
            assertEquals(bit24Array.get(i), bit24RawArray2[i]);
        }
        for (int i = 0; i < bit31Array.length(); i++) {
            assertEquals(bit31Array.getAndSet(i, bit31RawArray2[i]), bit31RawArray[i]);
        }
        for (int i = 0; i < bit31Array.length(); i++) {
            assertEquals(bit31Array.get(i), bit31RawArray2[i]);
        }
    }
}
